package com.hynet.heebit.components.widget.chart.event.touch;

import android.view.MotionEvent;
import android.view.View;

import com.hynet.heebit.components.widget.chart.event.touch.listener.IChartTouch;
import com.hynet.heebit.components.widget.chart.renderer.XChart;

public class ChartTouch implements IChartTouch {

    private View view = null;
    private XChart xChart = null;
    //单点移动前的坐标位置
    private float oldX = 0.0f, oldY = 0.0f;
    //scale
    private float oldDist = 1.0f, newDist = 0.0f;
    private float halfDist = 0.0f, scaleRate = 0.0f;
    //pan
    private int action = 0;
    private float newX = 0.0f, newY = 0.0f;
    private final float FIXED_RANGE = 8.0f;
    private float panRatio = 1.f;

    public ChartTouch(View view, XChart chart) {
        this.xChart = chart;
        this.view = view;
    }

    public ChartTouch(View view, XChart chart, float panRatio) {
        this.xChart = chart;
        this.view = view;
        this.panRatio = panRatio;
    }

    @Override
    public void handleTouch(MotionEvent event) {
        switch (event.getPointerCount()) {
            case 1:
                handleTouch_PanMode(event);
                break;
            case 2:
                handleTouch_Scale(event);
                break;
            default:
                break;
        }
    }


    public void handleTouch_Scale(MotionEvent event) {
        if (null == xChart || !xChart.getScaleStatus()) return;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:  //单点触碰	
                scaleRate = 1.0f;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:  //多点触碰	       
                //两点按下时的距离  
                oldDist = this.spacing(event);
                break;
            case MotionEvent.ACTION_MOVE:
                newDist = spacing(event);
                if (Float.compare(newDist, 10.0f) == 1) {
                    halfDist = newDist / 2;
                    if (Float.compare(oldDist, 0.0f) == 0) return;
                    scaleRate = newDist / oldDist;
                    //目前是采用焦点在哪就以哪范围为中心放大缩小.    
                    xChart.setScale(scaleRate, scaleRate, event.getX() - halfDist, event.getY() - halfDist);
                    if (null != view)
                        view.invalidate((int) xChart.getLeft(), (int) xChart.getTop(), (int) xChart.getRight(), (int) xChart.getBottom());
                }
                break;
            default:
                break;
        }
    }

    public void handleTouch_PanMode(MotionEvent event) {
        action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            if (oldX > 0 && oldY > 0) {
                newX = event.getX(0);
                newY = event.getY(0);
                if (Float.compare(Math.abs(newX - oldX), FIXED_RANGE) == 1 || Float.compare(Math.abs(newY - oldY), FIXED_RANGE) == 1) {
                    setLocation(oldX, oldY, newX, newY);
                    oldX = newX;
                    oldY = newY;
                }
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            //在第一个点被按下时触发
            oldX = event.getX(0);
            oldY = event.getY(0);
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            //当屏幕上已经有一个点被按住，此时再按下其他点时触发。
        } else if (action == MotionEvent.ACTION_UP  //当屏幕上唯一的点被放开时触发
                || action == MotionEvent.ACTION_POINTER_UP) {
            oldX = 0.0f;
            oldY = 0.0f;
            if (action == MotionEvent.ACTION_POINTER_UP) {
                //当屏幕上有多个点被按住，松开其中一个点时触发（即非最后一个点被放开时）。
                oldX = -1f;
                oldY = -1f;
            }
        }
    }

    /**
     * 用于限定可平移的范围(大于0),默认为1
     *
     * @param ratio
     */
    public void setPanRatio(float ratio) {
        panRatio = ratio;
    }

    //用来设置图表的位置 
    private void setLocation(float oldX, float oldY, float newX, float newY) {
        if (null == xChart) return;
        if (null == view) return;
        float xx = 0.0f, yy = 0.0f;
        float[] txy = xChart.getTranslateXY();
        if (null == txy) return;
        xx = txy[0];
        yy = txy[1];
        xx = txy[0] + newX - oldX;
        yy = txy[1] + newY - oldY;
        //不让其滑动出可显示范围
        if (xChart.getCtlPanRangeStatus()) {
            float xr = 1.f, yr = 1.f;
            if (Float.compare(panRatio, 0.f) > 0) {
                xr = xChart.getPlotArea().getPlotWidth() / panRatio;
                yr = xChart.getHeight() / panRatio;
            }
            if (Float.compare(Math.abs(xx), xr) == 1) {
                return;
            }

            if (Float.compare(Math.abs(yy), yr) == 1) {//xChart.getHeight()/2)
                return;
            }
        }
        xChart.setTranslateXY(xx, yy);
        view.invalidate((int) xChart.getLeft(), (int) xChart.getTop(), (int) xChart.getRight(), (int) xChart.getBottom());
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
