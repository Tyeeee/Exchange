package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.CrurveLineStyle;
import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.renderer.LnChart;
import com.hynet.heebit.components.widget.chart.renderer.line.DotInfo;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotCustomLine;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDotRender;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotLine;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.IFormatterTextCallBack;

import java.util.ArrayList;
import java.util.List;

public class SplineChart extends LnChart {

    //数据源
    private List<SplineData> splineData;
    //分类轴的最大，最小值
    private double maxValue = 0d;
    private double minValue = 0d;
    //用于格式化标签的回调接口
    private IFormatterTextCallBack iFormatterTextCallBack;
    //平滑曲线
    private List<PointF> pointFS = new ArrayList<>();
    private Path bezierPath = new Path();
    //key
    private List<LnData> lnData = new ArrayList<>();
    private List<DotInfo> dotInfos = new ArrayList<>();
    //平滑曲线
    private CrurveLineStyle crurveLineStyle = CrurveLineStyle.BEZIERCURVE;
    // 用于绘制定制线(分界线)
    private PlotCustomLine xAxisCustomLine = null;


    public SplineChart() {
        categoryAxisDefaultSetting();
        dataAxisDefaultSetting();
    }

    @Override
    public ChartType getType() {
        return ChartType.SPLINE;
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
    public void setDataSource(List<SplineData> dataSeries) {
        this.splineData = dataSeries;
    }

    public List<SplineData> getDataSource() {
        return this.splineData;
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
     * 设置分类轴的竖向定制线值
     *
     * @param customLineDataset 定制线数据集合
     */
    public void setCategoryAxisCustomLines(List<CustomLineData> customLineDataset) {
        if (null == xAxisCustomLine) xAxisCustomLine = new PlotCustomLine();
        xAxisCustomLine.setCustomLines(customLineDataset);
    }

    /**
     * 设置标签的显示格式
     *
     * @param iFormatterTextCallBack 回调函数
     */
    public void setDotLabelFormatter(IFormatterTextCallBack iFormatterTextCallBack) {
        this.iFormatterTextCallBack = iFormatterTextCallBack;
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
     * 设置曲线显示风格:直线(NORMAL)或平滑曲线(BEZIERCURVE)
     *
     * @param crurveLineStyle 曲线显示风格
     */
    public void setCrurveLineStyle(CrurveLineStyle crurveLineStyle) {
        this.crurveLineStyle = crurveLineStyle;
    }

    /**
     * 返回曲线显示风格
     *
     * @return 曲线显示风格
     */
    public CrurveLineStyle getCrurveLineStyle() {
        return crurveLineStyle;
    }

    private void calcAllPoints(SplineData bd, List<PointF> lstPoints, List<DotInfo> lstDotInfo) {
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
        float initX = plotAreaRender.getLeft();
        float initY = plotAreaRender.getBottom();
        float lineStartX, lineStartY;
        float lineStopX = 0.0f, lineStopY = 0.0f;
        //得到标签对应的值数据集
        List<PointD> chartValues = bd.getLineDataSet();
        if (null == chartValues) return;
        //画出数据集对应的线条
        int count = chartValues.size();
        for (int i = 0; i < count; i++) {
            PointD entry = chartValues.get(i);
            lineStopX = getLnXValPosition(entry.x, maxValue, minValue);
            lineStopY = getVPValPosition(entry.y);
            if (0 == i) {
                lineStartX = lineStopX;
                lineStartY = lineStopY;
                //line
                lstPoints.add(new PointF(lineStartX, lineStartY));
                lstPoints.add(new PointF(lineStopX, lineStopY));
            } else {
                //line
                lstPoints.add(new PointF(lineStopX, lineStopY));
            }
            //dot
            lstDotInfo.add(new DotInfo(entry.x, entry.y, lineStopX, lineStopY));
            lineStartX = lineStopX;
            lineStartY = lineStopY;
        }
    }


    private boolean renderLine(Canvas canvas, SplineData spData, List<PointF> lstPoints) {
        int count = lstPoints.size();
        for (int i = 0; i < count; i++) {
            if (0 == i) continue;
            PointF pointStart = lstPoints.get(i - 1);
            PointF pointStop = lstPoints.get(i);
            DrawUtil.getInstance().drawLine(spData.getLineStyle(), pointStart.x, pointStart.y, pointStop.x, pointStop.y, canvas, spData.getLinePaint());
        }
        return true;
    }

    private boolean renderBezierCurveLine(Canvas canvas, Path bezierPath, SplineData spData, List<PointF> lstPoints) {
        renderBezierCurveLine(canvas, spData.getLinePaint(), bezierPath, lstPoints);
        return true;
    }

    private boolean renderDotAndLabel(Canvas canvas, SplineData spData, int dataID,
                                      List<PointF> lstPoints) {
        PlotLine pLine = spData.getPlotLine();
        if (pLine.getDotStyle().equals(DotStyle.HIDE) && !spData.getLabelVisible()) {
            return true;
        }
        float itemAngle = spData.getItemLabelRotateAngle();
        PlotDot pDot = pLine.getPlotDot();
        float radius = pDot.getDotRadius();
        int count = dotInfos.size();
        for (int i = 0; i < count; i++) {
            DotInfo dotInfo = dotInfos.get(i);
            if (!pLine.getDotStyle().equals(DotStyle.HIDE)) {
                PlotDotRender.getInstance().renderDot(canvas, pDot, dotInfo.x, dotInfo.y, pLine.getDotPaint()); //标识图形
                savePointRecord(dataID, i, dotInfo.x + moveX, dotInfo.y + moveY, dotInfo.x - radius + moveX, dotInfo.y - radius + moveY, dotInfo.x + radius + moveX, dotInfo.y + radius + moveY);
                //childID++;
            }
            //显示批注形状
            drawAnchor(getAnchorDataPoint(), dataID, i, canvas, dotInfo.x, dotInfo.y, radius);
            if (spData.getLabelVisible()) {
                //请自行在回调函数中处理显示格式
                spData.getLabelOptions().drawLabel(canvas, pLine.getDotLabelPaint(), getFormatterDotLabel(dotInfo.getLabel()), dotInfo.x, dotInfo.y, itemAngle, spData.getLineColor());
            }
        }
        return true;
    }


    /**
     * 绘制图
     */
    private boolean renderPlot(Canvas canvas) {
        //检查是否有设置分类轴的最大最小值
        if (Double.compare(maxValue, minValue) == 0 && Double.compare(0d, maxValue) == 0) {
             LogUtil.Companion.getInstance().print("请检查是否有设置分类轴的最大最小值。");
            return false;
        }
        if (null == splineData) {
             LogUtil.Companion.getInstance().print("数据源为空.");
            return false;
        }
        //开始处 X 轴 即分类轴
        int count = splineData.size();
        for (int i = 0; i < count; i++) {
            SplineData splineData = this.splineData.get(i);
            calcAllPoints(splineData, pointFS, dotInfos);
            switch (getCrurveLineStyle()) {
                case BEZIERCURVE:
                    renderBezierCurveLine(canvas, bezierPath, splineData, pointFS);
                    break;
                case BEELINE:
                    renderLine(canvas, splineData, pointFS);
                    break;
                default:
                     LogUtil.Companion.getInstance().print("未知的枚举类型.");
                    continue;
            }
            renderDotAndLabel(canvas, splineData, i, pointFS);
            lnData.add(this.splineData.get(i));
            dotInfos.clear();
            pointFS.clear();
            bezierPath.reset();
        }
        return true;
    }


    @Override
    protected void drawClipPlot(Canvas canvas) {
        if (renderPlot(canvas)) {
            if (null != plotCustomLine) {//画横向定制线
                plotCustomLine.setVerticalPlot(dataAxisRender, plotAreaRender, getPlotScreenHeight());
                plotCustomLine.renderVerticalCustomlinesDataAxis(canvas);
            }
            if (null != xAxisCustomLine) {//画x轴上的竖向定制线
                xAxisCustomLine.renderCategoryAxisCustomlines(canvas, this.getPlotScreenWidth(), this.plotAreaRender, maxValue, minValue);
            }
        }
    }

    @Override
    protected void drawClipLegend(Canvas canvas) {
        //图例
        plotLegendRender.renderLineKey(canvas, lnData);
        lnData.clear();
    }

}
