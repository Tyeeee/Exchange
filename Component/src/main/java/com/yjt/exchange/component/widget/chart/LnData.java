package com.hynet.heebit.components.widget.chart;

import android.graphics.Paint;

import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.constant.LineStyle;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotLine;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotLabel;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotLabelRender;

public class LnData {

    //标签轴用的到值
    private String label;
    //是否在点上显示标签
    private boolean labelVisible = false;
    //线的基类
    private PlotLine plotLine;
    //线的类型
    private LineStyle lineStyle = LineStyle.SOLID;
    //用于设置标签特性
    private PlotLabelRender plotLabelRender = null;

    public LnData() {
        plotLine = new PlotLine();
    }

    /**
     * 设置是否在线上显示标签
     * @param visible 是否显示
     */
    public void setLabelVisible(boolean visible) {
        labelVisible = visible;
        getLabelOptions().setOffsetY(15.f);
        //getLabelOptions().showBox();
    }

    /**
     * 返回是否在线上显示标签
     * @return 是否显示
     */
    public boolean getLabelVisible() {
        return labelVisible;
    }

    /**
     * 设置标签
     * @param value 标签内容
     */
    public void setLabel(String value) {
        label = value;
    }

    /**
     * 返回标签
     * @return 标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 返回线的基类
     * @return 线的基类
     */
    public PlotLine getPlotLine() {
        return plotLine;
    }

    /**
     * 设置线的颜色	
     * @param color 线的颜色
     */
    public void setLineColor(int color) {
        plotLine.getLinePaint().setColor(color);
        plotLine.getDotLabelPaint().setColor(color);
        plotLine.getDotPaint().setColor(color);
    }

    /**
     * 返回线的颜色
     * @return 线的颜色
     */
    public int getLineColor() {
        return plotLine.getLinePaint().getColor();
    }

    /**
     * 设置点的显示风格
     * @param style 显示风格
     */
    public void setDotStyle(DotStyle style) {
        plotLine.setDotStyle(style);
    }

    /**
     * 返回点的显示风格
     * @return 显示风格
     */
    public DotStyle getDotStyle() {
        return plotLine.getDotStyle();
    }

    /**
     * 设置当前记录的Key值
     * @param value Key值
     */
    public void setLineKey(String value) {
        label = value;
    }

    /**
     * 返回Key值
     * @return Key值
     */
    public String getLineKey() {
        return label;
    }

    /**
     * 开放标签画笔
     * @return 画笔
     */
    public Paint getDotLabelPaint() {
        return plotLine.getDotLabelPaint();
    }

    /**
     * 开放线的画笔
     * @return 画笔
     */
    public Paint getLinePaint() {
        return plotLine.getLinePaint();
    }

    /**
     * 开放交叉点的画笔
     * @return 画笔
     */
    public Paint getDotPaint() {
        return plotLine.getDotPaint();
    }

    /**
     * 开放交叉点的半径,用来决定绘制的点的图形的大小
     * @param radius 半径
     */
    public void setDotRadius(int radius) {
        plotLine.getPlotDot().setDotRadius(radius);
    }


    /**
     * 返回线的绘制类型(此设置对平滑曲线无效)
     * @return 线类型
     */
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    /**
     * 设置线的绘制类型(此设置对平滑曲线无效)
     * @param style 线类型
     */
    public void setLineStyle(LineStyle style) {
        lineStyle = style;
    }

    /**
     * 用于设置标签显示属性
     * @return 标签属性类
     */
    public PlotLabel getLabelOptions() {
        if (null == plotLabelRender) {
            plotLabelRender = new PlotLabelRender();
        }
        return plotLabelRender;
    }
}
