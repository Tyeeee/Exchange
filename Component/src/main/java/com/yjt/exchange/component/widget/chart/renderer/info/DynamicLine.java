package com.hynet.heebit.components.widget.chart.renderer.info;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.constant.DynamicLineStyle;
import com.hynet.heebit.components.widget.chart.constant.LineStyle;

public class DynamicLine {

    private Paint linePaint = null;
    protected PointF centerXY = null;
    //设置交叉线显示风格
    private DynamicLineStyle dynamicLineStyle = DynamicLineStyle.CROSS;
    //线绘制为实线，虚实线 	等哪种风格
    private LineStyle lineStyle = LineStyle.SOLID;
    private float oldX = 0.0f, oldY = 0.0f;

    public DynamicLine() {

    }

    /**
     * 线的画笔
     *
     * @return
     */
    public Paint getLinePaint() {
        if (null == linePaint) {
            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //linePaint.setStyle(Style.STROKE);
            linePaint.setColor(Color.rgb(215, 10, 10)); //50, 165, 238));
        }

        return linePaint;
    }

    /**
     * 点击位置坐标
     *
     * @param x x坐标
     * @param y y坐标
     */
    public void setCurrentXY(float x, float y) {
        if (null == centerXY) centerXY = new PointF();
        centerXY.x = x;
        centerXY.y = y;
    }

    /**
     * 是否需要重绘
     *
     * @return 是否允许重绘
     */
    public boolean isInvalidate() {
        if (null == centerXY) return false;
        if (Float.compare(Math.abs(centerXY.x - oldX), 5.f) == 1 || Float.compare(Math.abs(centerXY.y - oldY), 5.f) == 1) {
            oldX = centerXY.x;
            oldY = centerXY.y;
            return true;
        }
        return false;
    }


    /**
     * 设置线的风格
     *
     * @param style 线的交叉风格
     */
    public void setDyLineStyle(DynamicLineStyle style) {
        dynamicLineStyle = style;
    }

    /**
     * 返回线的风格
     *
     * @return 线的交叉风格
     */
    public DynamicLineStyle getDyLineStyle() {
        return dynamicLineStyle;
    }


    /**
     * 设置线绘制为实线，虚实线 	等哪种风格
     *
     * @param style 线的类型
     */
    public void setLineDrawStyle(LineStyle style) {
        lineStyle = style;
    }

    /**
     * 返回线的绘制类型(实线，虚实线等)
     *
     * @return 线的绘制类型
     */
    public LineStyle getLineDrawStyle() {
        return lineStyle;
    }


}
