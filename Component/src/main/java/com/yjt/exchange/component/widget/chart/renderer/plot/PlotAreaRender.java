package com.hynet.heebit.components.widget.chart.renderer.plot;


import android.graphics.Canvas;
import android.graphics.LinearGradient;

import com.hynet.heebit.components.widget.chart.constant.Direction;
import com.hynet.heebit.components.widget.chart.renderer.IRender;

public class PlotAreaRender extends PlotArea implements IRender {

    public PlotAreaRender() {

    }

    /**
     * 绘制背景
     */
    protected void drawPlotBackground(Canvas canvas) {
        if (null == canvas) return;
        if (getBackgroundColorVisible()) {
            if (getApplayGradient()) {
                LinearGradient linearGradient;
                if (getGradientDirection() == Direction.VERTICAL) {
                    linearGradient = new LinearGradient(0, 0, 0, getBottom() - getTop(), getBeginColor(), getEndColor(), getGradientMode());
                } else {
                    linearGradient = new LinearGradient(getLeft(), getBottom(), getRight(), getTop(), getBeginColor(), getEndColor(), getGradientMode());
                }
                getBackgroundPaint().setShader(linearGradient);
            } else {
                getBackgroundPaint().setShader(null);
            }
            canvas.drawRect(left, top, right, bottom, getBackgroundPaint());
        }
    }

    /**
     * 得到中心点X坐标
     *
     * @return X坐标
     */
    public float getCenterX() {
        return Math.abs(left + (right - left) / 2);
    }

    /**
     * 得到中心点Y坐标
     *
     * @return Y坐标
     */
    public float getCenterY() {
        return (Math.abs(bottom - (bottom - top) / 2));
    }


    /**
     * 设置绘图区的左边X坐标
     *
     * @param left X坐标
     */
    public void setLeft(float left) {
        this.left = left;
    }

    /**
     * 设置绘图区的上面Y坐标
     *
     * @param top Y坐标
     */
    public void setTop(float top) {
        this.top = top;
    }

    /**
     * 设置绘图区的右边X坐标
     *
     * @param right X坐标
     */
    public void setRight(float right) {
        this.right = right;
    }

    /**
     * 设置绘图区的底部Y坐标
     *
     * @param bottom Y坐标
     */
    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    /**
     * 返回实际绘图区最右边X值，包含了扩展绘图区范围
     *
     * @return 绘图区最右边X值
     */
    @Override
    public float getPlotRight() {
        return right + getExtWidth();
    }

    @Override
    public boolean render(Canvas canvas) {
        if (null == canvas) return false;
        drawPlotBackground(canvas);
        return false;
    }

}
