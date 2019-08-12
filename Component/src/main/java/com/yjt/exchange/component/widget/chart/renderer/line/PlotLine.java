package com.hynet.heebit.components.widget.chart.renderer.line;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.widget.chart.constant.DotStyle;

public class PlotLine {

    private Paint linePaint = null;
    private Paint labelPaint = null;
    private Paint dotPaint = null;
    private PlotDot plotDot = null;

    public PlotLine() {
        if (null == plotDot) plotDot = new PlotDot();
    }

    private void initializeLinePaint() {
        if (null == linePaint) {
            linePaint = new Paint();
            linePaint.setColor(Color.BLUE);
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(5);
        }
    }

    private void initLabelPaint() {
        if (null == labelPaint) {
            labelPaint = new Paint();
            labelPaint.setColor(Color.BLUE);
            labelPaint.setTextSize(18);
            labelPaint.setTextAlign(Align.CENTER);
            labelPaint.setAntiAlias(true);
        }
    }


    /**
     * 开放线画笔
     *
     * @return 画笔
     */
    public Paint getLinePaint() {
        initializeLinePaint();
        return linePaint;
    }

    /**
     * 开放标签画笔
     *
     * @return 画笔
     */
    public Paint getDotLabelPaint() {
        initLabelPaint();
        return labelPaint;
    }

    /**
     * 开放点画笔
     *
     * @return 画笔
     */
    public Paint getDotPaint() {
        if (null == dotPaint) {
            dotPaint = new Paint();
            dotPaint.setColor(Color.BLUE);
            dotPaint.setAntiAlias(true);
            dotPaint.setStrokeWidth(5);
        }
        return dotPaint;// plotDot.getDotPaint();
    }

    /**
     * 开放点绘制类
     *
     * @return 点绘制类
     */
    public PlotDot getPlotDot() {
        return plotDot;
    }

    /**
     * 设置点的显示风格
     *
     * @param style 风格
     */
    public void setDotStyle(DotStyle style) {
        plotDot.setDotStyle(style);
    }

    /**
     * 返回点的显示风格
     *
     * @return 风格
     */
    public DotStyle getDotStyle() {
        return plotDot.getDotStyle();
    }


}
