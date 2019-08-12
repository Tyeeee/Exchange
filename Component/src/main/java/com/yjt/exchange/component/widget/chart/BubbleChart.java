package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.renderer.LnChart;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDotRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotQuadrant;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotQuadrantRender;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.IFormatterTextCallBack;

import java.util.List;

public class BubbleChart extends LnChart {

    //数据源
    private List<BubbleData> bubbleData;
    //分类轴的最大，最小值
    private double maxValue = 0d;
    private double minValue = 0d;
    //用于格式化标签的回调接口
    private IFormatterTextCallBack iFormatterTextCallBack;
    //指定气泡半径的最大大小
    private float bubbleMaxSize = 0.0f;
    private float bubbleMinSize = 0.0f;
    //指定最大气泡大小所表示的实际值，最大气泡大小由 BubbleMaxSize 设置。
    private float bubbleScaleMax = 0.0f;
    private float bubbleScaleMin = 0.0f;
    private Paint paint = null;
    private PlotDot plotDot = new PlotDot();
    private Paint borderPointPaintBorderPoint = null;
    //四象限类
    private PlotQuadrantRender plotQuadrantRender = null;

    public BubbleChart() {
        initChart();
    }

    @Override
    public ChartType getType() {
        return ChartType.BUBBLE;
    }

    private void initChart() {
        if (null != plotDot)
            plotDot.setDotStyle(DotStyle.DOT);
        categoryAxisDefaultSetting();
        dataAxisDefaultSetting();
        this.setAxesClosed(true);
    }

    @Override
    protected void categoryAxisDefaultSetting() {
        if (null != categoryAxisRender)
            categoryAxisRender.setHorizontalTickAlign(Align.CENTER);
    }

    @Override
    protected void dataAxisDefaultSetting() {
        if (null != dataAxisRender)
            dataAxisRender.setHorizontalTickAlign(Align.LEFT);
    }


    /**
     * 指定气泡半径的最大大小
     *
     * @param maxSize 最大气泡半径(px)
     */
    public void setBubbleMaxSize(float maxSize) {
        bubbleMaxSize = maxSize;
    }

    /**
     * 指定气泡半径的最小大小
     *
     * @param minSize 最小气泡半径(px)
     */
    public void setBubbleMinSize(float minSize) {
        bubbleMinSize = minSize;
    }


    /**
     * 指定最大气泡大小所表示的实际值
     *
     * @param scaleMax 最大气泡实际值
     */
    public void setBubbleScaleMax(float scaleMax) {
        bubbleScaleMax = scaleMax;
    }

    /**
     * 指定最小气泡大小所表示的实际值
     *
     * @param scaleMin 最小气泡实际值
     */
    public void setBubbleScaleMin(float scaleMin) {
        bubbleScaleMin = scaleMin;
    }

    /**
     * 分类轴的数据源
     *
     * @param categories 标签集
     */
    public void setCategories(List<String> categories) {
        if (null != categoryAxisRender) categoryAxisRender.setDataBuilding(categories);
    }

    /**
     * 设置数据轴的数据源
     *
     * @param dataSeries 数据序列
     */
    public void setDataSource(List<BubbleData> dataSeries) {
        this.bubbleData = dataSeries;
    }

    /**
     * 显示数据的数据轴最大值
     *
     * @param value 数据轴最大值
     */
    public void setCategoryAxisMax(double value) {
        maxValue = value;
    }

    /**
     * 设置分类轴最小值
     *
     * @param value 最小值
     */
    public void setCategoryAxisMin(double value) {
        minValue = value;
    }

    /**
     * 设置标签的显示格式
     *
     * @param callBack 回调函数
     */
    public void setDotLabelFormatter(IFormatterTextCallBack callBack) {
        this.iFormatterTextCallBack = callBack;
    }

    /**
     * 返回标签显示格式
     *
     * @param text 传入当前值
     *
     * @return 显示格式
     */
    protected String getFormatterDotLabel(String text) {
        return iFormatterTextCallBack.textFormatter(text);
    }


    private float calcRaidus(float scale, float scaleTotalSize, float bubbleRadius) {
        return mul(bubbleRadius, div(scale, scaleTotalSize));
    }


    /**
     * 返回四象限绘制类
     *
     * @return
     */
    public PlotQuadrant getPlotQuadrant() {
        if (null == plotQuadrantRender) plotQuadrantRender = new PlotQuadrantRender();
        return plotQuadrantRender;
    }


    /**
     * 绘制点的画笔
     *
     * @return
     */
    public Paint getPointPaint() {
        if (null == paint) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        return paint;
    }

    public Paint getPointBorderPaint() {
        if (null == borderPointPaintBorderPoint) {
            borderPointPaintBorderPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPointPaintBorderPoint.setStyle(Style.STROKE);
            borderPointPaintBorderPoint.setStrokeWidth(2);
        }
        return borderPointPaintBorderPoint;
    }

    /**
     * 绘制象限
     *
     * @param canvas 画布
     */
    private void drawQuadrant(Canvas canvas) {
        if (!getPlotQuadrant().isShow()) return;
        Double xValue = getPlotQuadrant().getQuadrantXValue();
        Double yValue = getPlotQuadrant().getQuadrantYValue();
        float centerX = getLnXValPosition(xValue, maxValue, minValue);
        float centerY = getVPValPosition(yValue);
        plotQuadrantRender.drawQuadrant(canvas, centerX, centerY, plotAreaRender.getLeft(), plotAreaRender.getPlotTop(), plotAreaRender.getPlotRight(), plotAreaRender.getBottom());
    }


    private void renderPoints(Canvas canvas, BubbleData bd, int dataID) {
        //得到标签对应的值数据集		
        List<PointD> chartValues = bd.getDataSet();
        if (null == chartValues) return;

        //画出数据集对应的线条	
        float YvaluePos = 0.0f, XvaluePos = 0.0f;
        if (Float.compare(bubbleScaleMax, bubbleScaleMin) == 0) {
             LogUtil.Companion.getInstance().print("没有指定用于决定气泡大小的最大最小实际数据值。");
            return;
        }
        if (Float.compare(bubbleMaxSize, bubbleMinSize) == 0) {
             LogUtil.Companion.getInstance().print("没有指定气泡本身，最大最小半径。");
            return;
        }
        if (Double.compare(maxValue, minValue) == -1) {
             LogUtil.Companion.getInstance().print("轴最大值小于最小值.");
            return;
        }
        if (Double.compare(maxValue, minValue) == 0) {
             LogUtil.Companion.getInstance().print("轴最大值与最小值相等.");
            return;
        }
        //double xMM  = MathHelper.getInstance().sub(maxValue , minValue);
        float scale = bubbleScaleMax - bubbleScaleMin;
        float size = bubbleMaxSize - bubbleMinSize;
        List<Double> doubles = bd.getBubble();
        int bubbleSize = doubles.size();
        double bubble = 0;
        float curRadius = 0.0f;
        //汽泡颜色
        getPointPaint().setColor(bd.getColor());
        //边框颜色
        if (bd.getBorderColor() != -1) getPointBorderPaint().setColor(bd.getBorderColor());
        float itemAngle = bd.getItemLabelRotateAngle();
        int count = chartValues.size();
        for (int i = 0; i < count; i++) {
            PointD entry = chartValues.get(i);
            XvaluePos = getLnXValPosition(entry.x, maxValue, minValue);
            YvaluePos = getVPValPosition(entry.y);
            if (i >= bubbleSize) { //j
                continue;
            } else { //j
                bubble = doubles.get(i);
            }
            curRadius = calcRaidus(scale, size, (float) bubble);
            if (Float.compare(curRadius, 0.0f) == 0 || Float.compare(curRadius, 0.0f) == -1) {
                continue;
            }
            plotDot.setDotRadius(curRadius);
            PlotDotRender.getInstance().renderDot(canvas, plotDot, XvaluePos, YvaluePos, getPointPaint());
            savePointRecord(dataID, i, XvaluePos + moveX, YvaluePos + moveY, XvaluePos - curRadius + moveX, YvaluePos - curRadius + moveY, XvaluePos + curRadius + moveX, YvaluePos + curRadius + moveY);
            if (bd.getBorderColor() != -1) {
                canvas.drawCircle(XvaluePos, YvaluePos, curRadius, getPointBorderPaint());
            }
            //显示批注形状
            drawAnchor(getAnchorDataPoint(), dataID, i, canvas, XvaluePos, YvaluePos, curRadius);
            if (bd.getLabelVisible()) {
                //请自行在回调函数中处理显示格式
                DrawUtil.getInstance().drawRotateText(getFormatterDotLabel(Double.toString(entry.x) + "," + Double.toString(entry.y) + " : " + Double.toString(bubble)), XvaluePos, YvaluePos, itemAngle, canvas, bd.getDotLabelPaint()); //lineStopX,lineStopY
            }
        }
    }

    /**
     * 绘制图
     */
    private boolean renderPlot(Canvas canvas) {
        //检查是否有设置分类轴的最大最小值		
        if (maxValue == minValue && 0 == maxValue) {
             LogUtil.Companion.getInstance().print("请检查是否有设置分类轴的最大最小值。");
            return false;
        }
        if (null == bubbleData) {
             LogUtil.Companion.getInstance().print("数据源为空.");
            return false;
        }
        //绘制四象限
        drawQuadrant(canvas);
        //开始处 X 轴 即分类轴
        int size = bubbleData.size();
        for (int i = 0; i < size; i++) {
            BubbleData bd = bubbleData.get(i);
            renderPoints(canvas, bd, i);
        }

        return true;
    }

    @Override
    protected void drawClipPlot(Canvas canvas) {
        if (renderPlot(canvas)) {
            //画横向定制线
            if (null != plotCustomLine) {
                plotCustomLine.setVerticalPlot(dataAxisRender, plotAreaRender, getAxisScreenHeight());
                plotCustomLine.renderVerticalCustomlinesDataAxis(canvas);
            }
        }
    }

    @Override
    protected void drawClipLegend(Canvas canvas) {
        plotLegendRender.renderBubbleKey(canvas, bubbleData);
    }

}
