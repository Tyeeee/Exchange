package com.hynet.heebit.components.widget.chart.renderer.line;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.CustomLineData;
import com.hynet.heebit.components.widget.chart.renderer.axis.DataAxisRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotAreaRender;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class PlotCustomLine {

    //定制集合
    private List<CustomLineData> customLineDatas;
    private DataAxisRender dataAxisRender = null;
    private PlotAreaRender plotAreaRender = null;
    private PlotDot plotDot = null;
    private float axisScreenHeight = 0.0f;
    private float axisScreenWidth = 0.0f;
    //默认箭头大小
    private static final int CAPSIZE = 10;

    public PlotCustomLine() {
    }

    public void setVerticalPlot(DataAxisRender dataAxisRender, PlotAreaRender plotAreaRender, float axisScreenHeight) {
        setDataAxis(dataAxisRender);
        setPlotArea(plotAreaRender);
        setAxisScreenHeight(axisScreenHeight);
    }

    public void setHorizontalPlot(DataAxisRender dataAxis, PlotAreaRender plotArea, float axisScreenWidth) {
        setDataAxis(dataAxis);
        setPlotArea(plotArea);
        setAxisScreenWidth(axisScreenWidth);
    }

    private boolean validateParams() {
        if (null == dataAxisRender) {
             LogUtil.Companion.getInstance().print("数据轴基类没有传过来。");
            return false;
        }
        if (null == plotAreaRender) {
             LogUtil.Companion.getInstance().print("绘图区基类没有传过来。");
            return false;
        }
        if (null == customLineDatas) {
             LogUtil.Companion.getInstance().print("数据集没有传过来。");
            return false;
        }
        return true;
    }


    /**
     * 用来画竖向柱形图，横向的定制线
     *
     * @param canvas 画布
     *
     * @return 是否成功
     */
    public boolean renderVerticalCustomlinesDataAxis(Canvas canvas) {
        if (!validateParams()) return false;
        if (0.0f == axisScreenHeight) {
             LogUtil.Companion.getInstance().print("轴的屏幕高度值没有传过来。");
            return false;
        }
        double axisHeight = MathUtil.getInstance().sub(dataAxisRender.getAxisMax(), dataAxisRender.getAxisMin());
        for (CustomLineData customLineData : customLineDatas) {
            customLineData.getCustomLinePaint().setColor(customLineData.getColor());
            customLineData.getCustomLinePaint().setStrokeWidth(customLineData.getLineStroke());
            double per = MathUtil.getInstance().div(MathUtil.getInstance().sub(customLineData.getValue(), dataAxisRender.getAxisMin()), axisHeight);
            float postion = MathUtil.getInstance().mul(axisScreenHeight, (float) per);
            float currentY = MathUtil.getInstance().sub(plotAreaRender.getBottom(), postion);
            //绘制线
            if (customLineData.isShowLine())
                DrawUtil.getInstance().drawLine(customLineData.getLineStyle(), plotAreaRender.getLeft(), currentY, plotAreaRender.getRight(), currentY, canvas, customLineData.getCustomLinePaint());
            //绘制标签和箭头
            renderCapLabelVerticalPlot(canvas, customLineData, postion);
        }
        return true;
    }

    /**
     * 绘制标签和箭头
     *
     * @param canvas         画布
     * @param customLineData 线基类
     * @param chartPostion   位置
     */
    private void renderCapLabelVerticalPlot(Canvas canvas, CustomLineData customLineData, float chartPostion) {
        if (customLineData.getLabel().length() > 0) {
            float currentX = 0.0f, currentY = 0.0f;
            float capX = 0.0f;
            //显示在哪个高度位置
            currentY = MathUtil.getInstance().sub(plotAreaRender.getBottom(), chartPostion);
            switch (customLineData.getLabelHorizontalPostion()) {
                case LEFT:
                    currentX = MathUtil.getInstance().sub(plotAreaRender.getLeft(), customLineData.getLabelOffset());
                    customLineData.getLineLabelPaint().setTextAlign(Align.RIGHT);
                    capX = plotAreaRender.getRight();
                    break;
                case CENTER:
                    float w = MathUtil.getInstance().div(MathUtil.getInstance().sub(plotAreaRender.getRight(), plotAreaRender.getLeft()), 2);
                    currentX = MathUtil.getInstance().add(plotAreaRender.getLeft(), w);
                    currentX = MathUtil.getInstance().sub(currentX, customLineData.getLabelOffset());
                    customLineData.getLineLabelPaint().setTextAlign(Align.CENTER);
                    float w2 = MathUtil.getInstance().div(MathUtil.getInstance().sub(plotAreaRender.getRight(), plotAreaRender.getLeft()), 2);
                    capX = MathUtil.getInstance().add(plotAreaRender.getLeft(), w2);
                    break;
                case RIGHT:
                    currentX = MathUtil.getInstance().add(plotAreaRender.getRight(), customLineData.getLabelOffset());
                    customLineData.getLineLabelPaint().setTextAlign(Align.LEFT);
                    capX = plotAreaRender.getLeft();
                    break;
            }
            //绘制箭头
            renderLineCapVerticalPlot(canvas, customLineData, capX, currentY);
            //绘制标签
            renderLabel(canvas, customLineData, currentX, currentY);
        }
    }


    private void renderLabel(Canvas canvas, CustomLineData customLineData, float currentX, float currentY) {
        float txtHeight = DrawUtil.getInstance().getPaintFontHeight(customLineData.getLineLabelPaint());
        switch (customLineData.getLabelHorizontalPostion()) {
            case LEFT:
                currentY += txtHeight / 3;
                break;
            case CENTER:
                if (customLineData.isShowLine())
                    currentY -= DrawUtil.getInstance().getPaintFontHeight(customLineData.getCustomLinePaint());
                break;
            case RIGHT:
                currentY += txtHeight / 3;
                break;
        }
        //绘制标签
        DrawUtil.getInstance().drawRotateText(customLineData.getLabel(), currentX, currentY, customLineData.getLabelRotateAngle(), canvas, customLineData.getLineLabelPaint());
    }

    /**
     * 用来画横向柱形图，竖向的定制线
     *
     * @param canvas 画布
     *
     * @return 是否成功
     */
    public boolean renderHorizontalCustomlinesDataAxis(Canvas canvas) {
        if (!validateParams()) return false;
        if (0.0f == axisScreenWidth) {
             LogUtil.Companion.getInstance().print("轴的屏幕宽度值没有传过来。");
            return false;
        }
        double axisHeight = dataAxisRender.getAxisMax() - dataAxisRender.getAxisMin();
        for (CustomLineData line : customLineDatas) {
            line.getCustomLinePaint().setColor(line.getColor());
            line.getCustomLinePaint().setStrokeWidth(line.getLineStroke());
            double postion = axisScreenWidth * ((line.getValue() - dataAxisRender.getAxisMin()) / axisHeight);
            float currentX = (float) (plotAreaRender.getLeft() + postion);
            //绘制线
            if (line.isShowLine())
                DrawUtil.getInstance().drawLine(line.getLineStyle(), currentX, plotAreaRender.getBottom(), currentX, plotAreaRender.getTop(), canvas, line.getCustomLinePaint());
            //绘制标签和箭头
            renderCapLabelHorizontalPlot(canvas, line, postion);
        }
        return true;
    }

    // PlotAreaRender plotArea,

    public boolean renderCategoryAxisCustomlines(Canvas canvas, float plotScreenWidth, PlotAreaRender plotArea, double maxValue, double minValue) {
        setPlotArea(plotArea);
        for (CustomLineData line : customLineDatas) {
            line.getCustomLinePaint().setColor(line.getColor());
            line.getCustomLinePaint().setStrokeWidth(line.getLineStroke());
            float pos = MathUtil.getInstance().getLnPlotXValPosition(plotScreenWidth, plotArea.getLeft(), line.getValue(), maxValue, minValue);
            float currentX = MathUtil.getInstance().add(plotArea.getLeft(), pos);
            //绘制线
            if (line.isShowLine())
                DrawUtil.getInstance().drawLine(line.getLineStyle(), currentX, plotArea.getBottom(), currentX, plotArea.getTop(), canvas, line.getCustomLinePaint());
            //绘制标签和箭头
            renderCapLabelHorizontalPlot(canvas, line, pos);
        }
        return true;
    }


    /**
     * 绘制标签和箭头
     *
     * @param canvas       画布
     * @param customLineData         线基类
     * @param chartPostion 位置
     */
    private void renderCapLabelHorizontalPlot(Canvas canvas, CustomLineData customLineData, double chartPostion) {
        if (customLineData.getLabel().length() > 0) {
            float currentX = 0.0f, currentY = 0.0f;
            float capY = 0.0f;
            currentX = (float) (plotAreaRender.getLeft() + chartPostion);
            switch (customLineData.getLabelVerticalAlign()) {
                case TOP:
                    currentY = plotAreaRender.getTop() - customLineData.getLabelOffset();
                    capY = plotAreaRender.getBottom();
                    break;
                case MIDDLE:
                    currentY = plotAreaRender.getBottom() - (plotAreaRender.getBottom() - plotAreaRender.getTop()) / 2 - customLineData.getLabelOffset();
                    capY = plotAreaRender.getBottom() - (plotAreaRender.getBottom() - plotAreaRender.getTop()) / 2;
                    break;
                case BOTTOM:
                    currentY = plotAreaRender.getBottom() + customLineData.getLabelOffset();
                    capY = plotAreaRender.getTop();
                    break;
            }
            customLineData.getLineLabelPaint().setTextAlign(Align.CENTER);
            //绘制箭头
            renderLineCapHorizontalPlot(canvas, customLineData, currentX, capY);
            //绘制标签
            DrawUtil.getInstance().drawRotateText(customLineData.getLabel(), currentX, currentY, customLineData.getLabelRotateAngle(), canvas, customLineData.getLineLabelPaint());
        }
    }

    //绘制箭头
    private void renderLineCapHorizontalPlot(Canvas canvas, CustomLineData customLineData, float currentX, float currentY) {
        float left = currentX;
        float top = currentY;
        float right = currentX;
        float bottom = currentY;
        renderLineCap(canvas, customLineData, left, top, right, bottom);
    }

    //绘制箭头
    private void renderLineCapVerticalPlot(Canvas canvas, CustomLineData customLineData, float currentX, float currentY) {
        float left = currentX - CAPSIZE * 2;
        float top = currentY - CAPSIZE * 2;
        float right = currentX;
        float bottom = currentY;
        renderLineCap(canvas, customLineData, left, top, right, bottom);
    }

    //绘制箭头
    private void renderLineCap(Canvas canvas, CustomLineData customLineData, float left, float top, float right, float bottom) {
        initPlotDot();
        plotDot.setDotStyle(customLineData.getCustomeLineCap());
        PlotDotRender.getInstance().renderDot(canvas, plotDot, left + (right - left) / 2, top + (bottom - top) / 2, customLineData.getCustomLinePaint()); //标识图形          	
    }

    private void initPlotDot() {
        if (null == plotDot) plotDot = new PlotDot();
    }

    /**
     * 设置定制线值
     *
     * @param customLineDataSet 线数据集合
     */
    public void setCustomLines(List<CustomLineData> customLineDataSet) {
        //if(null != customLineDatas) customLineDatas.clear();
        customLineDatas = customLineDataSet;
    }


    /**
     * 设置当前处理的数据轴
     *
     * @param dataAxisRender 数据轴
     */
    public void setDataAxis(DataAxisRender dataAxisRender) {
        this.dataAxisRender = dataAxisRender;
    }

    /**
     * 设置绘图区
     *
     * @param plotAreaRender 绘图区
     */
    public void setPlotArea(PlotAreaRender plotAreaRender) {
        this.plotAreaRender = plotAreaRender;
    }

    /**
     * 设置轴的屏幕高度值
     *
     * @param height 高度
     */
    public void setAxisScreenHeight(float height) {
        axisScreenHeight = height;
    }

    /**
     * 设置轴的屏幕宽度值
     *
     * @param width 宽度
     */
    public void setAxisScreenWidth(float width) {
        axisScreenWidth = width;
    }

}
