package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Color;
import android.graphics.Paint;

public class PlotQuadrant {

    protected int firstColor = Color.WHITE;
    protected int secondColor = Color.WHITE;
    protected int thirdColor = Color.WHITE;
    protected int fourthColor = Color.WHITE;
    protected boolean show = false;
    protected boolean showBackgroundColor = true;
    protected boolean showVerticalLine = true;
    protected boolean showHorizontalLine = true;
    private Paint backgroundColorPaint = null;
    private Paint verticalLinePaint = null;
    private Paint horizontalLinePaint = null;
    private double quadrantX = 0d;
    private double quadrantYe = 0d;

    public PlotQuadrant() {
    }

    /**
     * 显示象限
     *
     * @param xValue x轴值
     * @param yValue y轴值
     */
    public void show(double xValue, double yValue) {
        setQuadrantXYValue(xValue, yValue);
        show = true;
    }

    /**
     * 隐藏象限
     */
    public void hide() {
        show = false;
    }

    /**
     * 是否显示象限
     *
     * @return
     */
    public boolean isShow() {
        return show;
    }

    /**
     * 显示背景色
     */
    public void showBgColor() {
        showBackgroundColor = true;
    }

    /**
     * 隐藏背景色
     */
    public void hideBgColor() {
        showBackgroundColor = false;
    }

    /**
     * 显示竖线
     */
    public void showVerticalLine() {
        showVerticalLine = true;
    }

    /**
     * 隐藏竖线
     */
    public void hideVerticalLine() {
        showVerticalLine = false;
    }

    /**
     * 显示横线
     */
    public void showHorizontalLine() {
        showHorizontalLine = true;
    }

    /**
     * 隐藏横线
     */
    public void hideHorizontalLine() {
        showHorizontalLine = false;
    }

    /**
     * 设置各个象限的颜色
     *
     * @param first  第一象限
     * @param second 第二象限
     * @param third  第三象限
     * @param fourth 第四象限
     */
    public void setBgColor(int first, int second, int third, int fourth) {
        firstColor = first;
        secondColor = second;
        thirdColor = third;
        fourthColor = fourth;
    }

    /**
     * 开放竖线画笔
     *
     * @return 画笔
     */
    public Paint getVerticalLinePaint() {
        if (null == verticalLinePaint)
            verticalLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        return verticalLinePaint;
    }

    /**
     * 开放横线画笔
     *
     * @return 画笔
     */
    public Paint getHorizontalLinePaint() {
        if (null == horizontalLinePaint)
            horizontalLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        return horizontalLinePaint;
    }

    /**
     * 开放背景色画笔
     *
     * @return 画笔
     */
    public Paint getBgColorPaint() {
        if (null == backgroundColorPaint)
            backgroundColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        return backgroundColorPaint;
    }

    /**
     * 设置象限中心点的值
     *
     * @param xValue x方向实际值
     * @param yValue y方向实际值
     */
    public void setQuadrantXYValue(double xValue, double yValue) {
        quadrantX = xValue;
        quadrantYe = yValue;
    }

    /**
     * 返回x方向实际值
     *
     * @return x值
     */
    public double getQuadrantXValue() {
        return quadrantX;
    }

    /**
     * 返回y方向实际值
     *
     * @return y值
     */
    public double getQuadrantYValue() {
        return quadrantYe;
    }

}
