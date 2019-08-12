package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

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

public class ScatterChart extends LnChart {

    //数据源
    private List<ScatterData> scatterData;
    //分类轴的最大，最小值
    private double maxValue = 0d;
    private double minValue = 0d;
    //用于格式化标签的回调接口
    private IFormatterTextCallBack iFormatterTextCallBack;
    //用于绘制点的画笔
    private Paint paint = null;
    //四象限类
    private PlotQuadrantRender plotQuadrantRender = null;

    public ScatterChart() {
        categoryAxisDefaultSetting();
        dataAxisDefaultSetting();

        this.setAxesClosed(true);
    }

    @Override
    public ChartType getType() {
        return ChartType.SCATTER;
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
    public void setDataSource(List<ScatterData> dataSeries) {
        this.scatterData = dataSeries;
    }

    /**
     * 返回数据轴的数据源
     *
     * @return 数据源
     */
    public List<ScatterData> getDataSource() {
        return this.scatterData;
    }

    /**
     * 显示数据的数据轴最大值
     *
     * @param value 数据轴最大值
     */
    public void setcategoryAxisRenderMax(double value) {
        maxValue = value;
    }

    /**
     * 设置分类轴最小值
     *
     * @param value 最小值
     */
    public void setcategoryAxisRenderMin(double value) {
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
     * 用于绘制点的画笔
     *
     * @return 画笔
     */
    public Paint getPointPaint() {
        if (null == paint) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        return paint;
    }

    /**
     * 绘制象限
     *
     * @param canvas 画布
     */
    private void drawQuadrant(Canvas canvas) {
        if (!getPlotQuadrant().isShow()) return;
        float centerX = getLnXValPosition(getPlotQuadrant().getQuadrantXValue(), maxValue, minValue);
        float centerY = getVPValPosition(getPlotQuadrant().getQuadrantYValue());
        plotQuadrantRender.drawQuadrant(canvas, centerX, centerY, plotAreaRender.getLeft(), plotAreaRender.getPlotTop(), plotAreaRender.getPlotRight(), plotAreaRender.getBottom());
    }


    private void renderPoints(Canvas canvas, ScatterData bd, int dataID) {
        if (null == bd) {
             LogUtil.Companion.getInstance().print("传入的数据序列参数为空.");
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
        float axisDataHeight = dataAxisRender.getAxisRange();
        if (Float.compare(axisDataHeight, 0.0f) == 0 || Float.compare(axisDataHeight, 0.0f) == -1) {
             LogUtil.Companion.getInstance().print("数据轴高度小于或等于0.");
            return;
        }
        //得到标签对应的值数据集
        List<PointD> chartValues = bd.getDataSet();
        if (null == chartValues) return;
        //画出数据集对应的线条
        float yPosition = 0.0f, xPosition = 0.0f;
        float itemAngle = bd.getItemLabelRotateAngle();
        PlotDot dot = bd.getPlotDot();
        float radius = dot.getDotRadius();
        int count = chartValues.size();
        for (int i = 0; i < count; i++) {
            PointD entry = chartValues.get(i);
            xPosition = getLnXValPosition(entry.x, maxValue, minValue);
            yPosition = getVPValPosition(entry.y);
            if (!dot.getDotStyle().equals(DotStyle.HIDE)) {
                getPointPaint().setColor(dot.getColor());
                getPointPaint().setAlpha(dot.getAlpha());
                PlotDotRender.getInstance().renderDot(canvas, dot, xPosition, yPosition, getPointPaint());
                savePointRecord(dataID, i, xPosition + moveX, yPosition + moveY, xPosition - radius + moveX, yPosition - radius + moveY, xPosition + radius + moveX, yPosition + radius + moveY);
            }
            //显示批注形状
            drawAnchor(getAnchorDataPoint(), dataID, i, canvas, xPosition, yPosition, radius);
            if (bd.getLabelVisible()) {
                //请自行在回调函数中处理显示格式
                DrawUtil.getInstance().drawRotateText(getFormatterDotLabel(Double.toString(entry.x) + "," + Double.toString(entry.y)), xPosition, yPosition, itemAngle, canvas, bd.getDotLabelPaint());
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
        if (null == scatterData) {
             LogUtil.Companion.getInstance().print("数据源为空.");
            return false;
        }
        //绘制四象限
        drawQuadrant(canvas);
        //开始处 X 轴 即分类轴
        int count = scatterData.size();
        for (int i = 0; i < count; i++) {
            ScatterData bd = scatterData.get(i);
            if (bd.getPlotDot().getDotStyle().equals(DotStyle.HIDE) && !bd.getLabelVisible()) {
                continue;
            }
            renderPoints(canvas, bd, i);
        }
        return true;
    }

    @Override
    protected void drawClipPlot(Canvas canvas) {
        if (renderPlot(canvas) == true) {
            //画横向定制线
            if (null != plotCustomLine) {
                plotCustomLine.setVerticalPlot(dataAxisRender, plotAreaRender, getAxisScreenHeight());
                plotCustomLine.renderVerticalCustomlinesDataAxis(canvas);
            }
        }
    }

    @Override
    protected void drawClipLegend(Canvas canvas) {
        //图例
        plotLegendRender.renderPointKey(canvas, scatterData);
    }
}
