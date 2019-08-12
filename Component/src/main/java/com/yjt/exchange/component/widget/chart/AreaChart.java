package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.CrurveLineStyle;
import com.hynet.heebit.components.widget.chart.constant.Direction;
import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.renderer.LnChart;
import com.hynet.heebit.components.widget.chart.renderer.line.DotInfo;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDotRender;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotLine;
import com.hynet.heebit.components.widget.chart.utils.BezierCurvesUtil;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.PointUtil;

import java.util.ArrayList;
import java.util.List;

public class AreaChart extends LnChart {

    // 画点分类的画笔
    protected Paint areaFillPaint = null;
    // 数据源
    protected List<AreaData> areaDatas;
    // 透明度
    private int areaAlpha = 100;
    // path area
    private List<PointF> pointFs = new ArrayList<>();
    private Path areaPath = null;
    private PointF[] bezierControls = new PointF[2];
    // key
    private List<LnData> lnDatas = new ArrayList<>();
    // line
    private List<PointF> pointFS = new ArrayList<>();
    // dots
    private List<DotInfo> dotInfos = new ArrayList<>();
    // 平滑曲线
    private CrurveLineStyle crurveLineStyle = CrurveLineStyle.BEZIERCURVE;
    private final int Y_MIN = 0;
    private final int Y_MAX = 1;

    public AreaChart() {
        categoryAxisDefaultSetting();
        dataAxisDefaultSetting();
    }

    @Override
    public ChartType getType() {
        return ChartType.AREA;
    }

    public Paint getAreaFillPaint() {
        if (null == areaFillPaint) {
            areaFillPaint = new Paint();
            areaFillPaint.setStyle(Style.FILL);
            areaFillPaint.setAntiAlias(true);
            areaFillPaint.setColor(Color.rgb(73, 172, 72));
        }
        return areaFillPaint;
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
     * @param categories 分类集
     */
    public void setCategories(List<String> categories) {
        if (null != categoryAxisRender)
            categoryAxisRender.setDataBuilding(categories);
    }

    /**
     * 设置数据轴的数据源
     *
     * @param areaDatas 数据源
     */
    public void setDataSource(List<AreaData> areaDatas) {
        this.areaDatas = areaDatas;
    }

    /**
     * 返回数据轴的数据源
     *
     * @return 数据源
     */
    public List<AreaData> getDataSource() {
        return this.areaDatas;
    }

    /**
     * 设置透明度,默认为100
     *
     * @param alpha 透明度
     */
    public void setAreaAlpha(int alpha) {
        areaAlpha = alpha;
    }

    /**
     * 设置曲线显示风格:直线(NORMAL)或平滑曲线(BEZIERCURVE)
     *
     * @param style
     */
    public void setCrurveLineStyle(CrurveLineStyle style) {
        crurveLineStyle = style;
    }

    /**
     * 返回曲线显示风格
     *
     * @return 显示风格
     */
    public CrurveLineStyle getCrurveLineStyle() {
        return crurveLineStyle;
    }

    private boolean calcAllPoints(AreaData areaData, List<DotInfo> dotInfos, List<PointF> pointFS, List<PointF> lstPathPoints) {
        if (null == areaData) {
             LogUtil.Companion.getInstance().print("传入的数据序列参数为空.");
            return false;
        }
        // 数据源
        List<Double> chartValues = areaData.getLinePoint();
        if (null == chartValues) {
             LogUtil.Companion.getInstance().print("线数据集合为空.");
            return false;
        }
        float lineStartX = plotAreaRender.getLeft(), lineStartY = plotAreaRender.getBottom();
        float lineStopX = 0.0f, lineStopY = 0.0f;
        float currLablesSteps = div(getPlotScreenWidth(), (categoryAxisRender.getDataSet().size() - 1));
        int count = chartValues.size();
        if (count <= 0)
            return false;
        for (int i = 0; i < count; i++) {
            double bv = chartValues.get(i);
            // 首尾为0,path不能闭合，改成 0.001都可以闭合?
            lineStopX = add(plotAreaRender.getLeft(), mul(i, currLablesSteps));
            lineStopY = getVPValPosition(bv);
            if (0 == i) {
                lineStartX = lineStopX;
                lineStartY = lineStopY;
                if (2 < count) {
                    if (Double.compare(bv, dataAxisRender.getAxisMin()) != 0)
                        lstPathPoints.add(new PointF(plotAreaRender.getLeft(), plotAreaRender.getBottom()));
                }
                lstPathPoints.add(new PointF(lineStartX, lineStartY));
                pointFS.add(new PointF(lineStartX, lineStartY));
            }
            // path
            lstPathPoints.add(new PointF(lineStopX, lineStopY));
            // line
            pointFS.add(new PointF(lineStopX, lineStopY));
            // dot
            dotInfos.add(new DotInfo(bv, lineStopX, lineStopY));
            lineStartX = lineStopX;
            lineStartY = lineStopY;
        }
        if (count > 2) {
            lstPathPoints.add(new PointF(lineStartX, lineStartY));
            if (Double.compare(chartValues.get(count - 1), dataAxisRender.getAxisMin()) != 0) {
                lstPathPoints.add(new PointF(lineStartX, plotAreaRender.getBottom()));
            }
        }
        return true;
    }

    private boolean renderBezierArea(Canvas canvas, Paint paintAreaFill, Path bezierPath, AreaData areaData, List<PointF> lstPathPoints) {
        int count = lstPathPoints.size();
        if (count < 3)
            return false; // 没有或仅一个点就不需要了
        // 设置当前填充色
        paintAreaFill.setColor(areaData.getAreaFillColor());
        // 仅两点
        if (count == 3) {
            if (null == bezierPath)
                bezierPath = new Path();
            bezierPath.moveTo(lstPathPoints.get(0).x, plotAreaRender.getBottom());
            bezierPath.lineTo(lstPathPoints.get(0).x, lstPathPoints.get(0).y);
            PointF ctl3 = PointUtil.percent(lstPathPoints.get(1), 0.5f, lstPathPoints.get(2), 0.8f);
            bezierPath.quadTo(ctl3.x, ctl3.y, lstPathPoints.get(2).x, lstPathPoints.get(2).y);
            bezierPath.lineTo(lstPathPoints.get(2).x, plotAreaRender.getBottom());
            bezierPath.close();

            if (areaData.getApplayGradient()) {
                LinearGradient linearGradient;
                if (areaData.getGradientDirection() == Direction.VERTICAL) {
                    float lineMaxY = getLineMaxMinY(Y_MAX, lstPathPoints);
                    linearGradient = new LinearGradient(0, 0, 0, plotAreaRender.getBottom() - lineMaxY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
                } else {
                    float lineMinY = getLineMaxMinY(Y_MIN, lstPathPoints);
                    linearGradient = new LinearGradient(plotAreaRender.getLeft(), plotAreaRender.getBottom(), lstPathPoints.get(2).x, lineMinY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
                }
                paintAreaFill.setShader(linearGradient);
            } else {
                paintAreaFill.setShader(null);
            }
            canvas.drawPath(bezierPath, paintAreaFill);
            bezierPath.reset();
            return true;
        }
        // 透明度
        paintAreaFill.setAlpha(this.areaAlpha);
        // start point
        bezierPath.moveTo(plotAreaRender.getLeft(), plotAreaRender.getBottom());
        float axisMinValue = plotAreaRender.getBottom();
        for (int i = 0; i < count; i++) {
            if (i < 3)
                continue;
            // 连续两个值都为0,控制点有可能会显示在轴以下，则此种情况下，将其处理为直线
            if (lstPathPoints.get(i - 1).y >= axisMinValue
                    && lstPathPoints.get(i).y >= axisMinValue) {
                // 如果最后两点为0时此处调用了两次，最后一次跳过不做处理,原因是数组的最后一个点的y值必定为0
                if (i == count - 1) {
                    continue;
                }
                if (null == bezierPath) {
                    bezierPath = new Path();
                    bezierPath.moveTo(lstPathPoints.get(i - 2).x, lstPathPoints.get(i - 2).y);
                } else {
                    bezierPath.lineTo(lstPathPoints.get(i - 2).x, lstPathPoints.get(i - 2).y);
                }
                // change by chenqiang
                if (lstPathPoints.get(i - 2).y >= axisMinValue) {//连续3个点为0
                    bezierPath.moveTo(lstPathPoints.get(i - 1).x, lstPathPoints.get(i - 1).y);
                } else {
                    BezierCurvesUtil.curve3(lstPathPoints.get(i - 2), lstPathPoints.get(i - 1), lstPathPoints.get(i - 3), lstPathPoints.get(i), bezierControls);
                    bezierPath.quadTo(bezierControls[0].x, bezierControls[0].y, lstPathPoints.get(i - 1).x, lstPathPoints.get(i - 1).y);
                }
                bezierPath.close();
                if (areaData.getApplayGradient()) {
                    LinearGradient linearGradient;
                    if (areaData.getGradientDirection() == Direction.VERTICAL) {
                        float lineMaxY = getLineMaxMinY(Y_MAX, lstPathPoints);
                        linearGradient = new LinearGradient(0, 0, 0, lineMaxY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
                    } else {
                        float lineMinY = getLineMaxMinY(Y_MIN, lstPathPoints);
                        linearGradient = new LinearGradient(plotAreaRender.getLeft(), plotAreaRender.getBottom(), lstPathPoints.get(i - 1).x, lineMinY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
                    }
                    paintAreaFill.setShader(linearGradient);
                } else {
                    paintAreaFill.setShader(null);
                }
                canvas.drawPath(bezierPath, paintAreaFill);
                bezierPath.reset();
                bezierPath.moveTo(lstPathPoints.get(i).x, lstPathPoints.get(i).y);
                continue;
            }
            BezierCurvesUtil.curve3(lstPathPoints.get(i - 2), lstPathPoints.get(i - 1), lstPathPoints.get(i - 3), lstPathPoints.get(i), bezierControls);
            // change by chenqiang
            bezierCurvePathAxisMinValue(bezierPath, lstPathPoints.get(i - 2), lstPathPoints.get(i - 1), bezierControls);
        }
        // 最后两点间的区域
        PointF stop = lstPathPoints.get(count - 1);// 通过calcAllPoints函数，stop的y值一定是0
        if (lstPathPoints.get(count - 2).y >= axisMinValue) {// 最后一点是0
            // change by chenqiang
            BezierCurvesUtil.curve3(lstPathPoints.get(count - 3), stop, lstPathPoints.get(count - 4), stop, bezierControls);
            bezierCurvePathAxisMinValue(bezierPath, lstPathPoints.get(count - 3), stop, bezierControls);
        } else {
            BezierCurvesUtil.curve3(lstPathPoints.get(count - 2), stop, lstPathPoints.get(count - 3), stop, bezierControls);
            // change by chenqiang
            bezierCurvePathAxisMinValue(bezierPath, lstPathPoints.get(count - 2), lstPathPoints.get(count - 1), bezierControls);
        }
        bezierPath.close();
        if (areaData.getApplayGradient()) {
            LinearGradient linearGradient;
            if (areaData.getGradientDirection() == Direction.VERTICAL) {
                float lineMaxY = getLineMaxMinY(Y_MAX, lstPathPoints);
                linearGradient = new LinearGradient(0, 0, 0, lineMaxY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
            } else {
                float lineMinY = getLineMaxMinY(Y_MIN, lstPathPoints);
                linearGradient = new LinearGradient(plotAreaRender.getLeft(), plotAreaRender.getBottom(), stop.x, lineMinY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
            }
            paintAreaFill.setShader(linearGradient);
        } else {
            paintAreaFill.setShader(null);
        }
        canvas.drawPath(bezierPath, paintAreaFill);
        bezierPath.reset();

        return true;
    }

    private boolean renderArea(Canvas canvas, Paint paintAreaFill, Path pathArea, AreaData areaData, List<PointF> lstPathPoints) {
        int count = lstPathPoints.size();
        if (count < 3)
            return false; // 没有或仅一个点就不需要了
        // 设置当前填充色
        paintAreaFill.setColor(areaData.getAreaFillColor());
        if (areaData.getApplayGradient()) {
            LinearGradient linearGradient;
            if (areaData.getGradientDirection() == Direction.VERTICAL) {
                float lineMaxY = getLineMaxMinY(Y_MAX, lstPathPoints);
                linearGradient = new LinearGradient(0, 0, 0, lineMaxY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
            } else {
                float lineMinY = getLineMaxMinY(Y_MIN, lstPathPoints);
                linearGradient = new LinearGradient(plotAreaRender.getLeft(), plotAreaRender.getBottom(), lstPathPoints.get(count - 1).x, lineMinY, areaData.getAreaBeginColor(), areaData.getAreaEndColor(), areaData.getGradientMode());
            }
            paintAreaFill.setShader(linearGradient);
        } else {
            paintAreaFill.setShader(null);
        }
        // 透明度
        paintAreaFill.setAlpha(this.areaAlpha);
        // 仅两点
        if (count == 3) {
            if (null == pathArea)
                pathArea = new Path();
            pathArea.moveTo(lstPathPoints.get(0).x, plotAreaRender.getBottom());
            pathArea.lineTo(lstPathPoints.get(0).x, lstPathPoints.get(0).y);
            pathArea.lineTo(lstPathPoints.get(1).x, lstPathPoints.get(1).y);
            pathArea.lineTo(lstPathPoints.get(2).x, lstPathPoints.get(2).y);
            pathArea.lineTo(lstPathPoints.get(2).x, plotAreaRender.getBottom());
            pathArea.close();
            // 绘制area
            canvas.drawPath(pathArea, paintAreaFill);
            pathArea.reset();
            return true;
        }
        for (int i = 0; i < count; i++) {
            PointF point = lstPathPoints.get(i);
            if (0 == i) {
                pathArea.moveTo(point.x, point.y);
            } else {
                pathArea.lineTo(point.x, point.y);
            }
        }
        pathArea.close();
        // 绘制area
        canvas.drawPath(pathArea, paintAreaFill);
        pathArea.reset();
        return true;
    }

    private boolean renderLine(Canvas canvas, AreaData areaData, List<PointF> lstPoints) {
        int count = lstPoints.size();
        for (int i = 0; i < count; i++) {
            if (0 == i)
                continue;
            PointF pointStart = lstPoints.get(i - 1);
            PointF pointStop = lstPoints.get(i);
            DrawUtil.getInstance().drawLine(areaData.getLineStyle(), pointStart.x, pointStart.y, pointStop.x, pointStop.y, canvas, areaData.getLinePaint());
        }
        return true;
    }

    private boolean renderBezierCurveLine(Canvas canvas, Path bezierPath, AreaData areaData, List<PointF> lstPoints) {
        renderBezierCurveLine(canvas, areaData.getLinePaint(), bezierPath, lstPoints);
        return true;
    }

    /**
     * 绘制区域
     */
    private boolean renderDotAndLabel(Canvas canvas, AreaData bd, int dataID, List<DotInfo> lstDotInfo) {
        float itemAngle = bd.getItemLabelRotateAngle();
        PlotLine pLine = bd.getPlotLine();
        if (pLine.getDotStyle().equals(DotStyle.HIDE) && !bd.getLabelVisible()) {
            return true;
        }
        PlotDot plotDot = pLine.getPlotDot();
        float radius = plotDot.getDotRadius();
        int count = lstDotInfo.size();
        for (int i = 0; i < count; i++) {
            DotInfo dotInfo = lstDotInfo.get(i);
            if (!pLine.getDotStyle().equals(DotStyle.HIDE)) {
                PlotDotRender.getInstance().renderDot(canvas, plotDot, dotInfo.x, dotInfo.y, pLine.getDotPaint());
                savePointRecord(dataID, i, dotInfo.x + moveX, dotInfo.y + moveY, dotInfo.x - radius + moveX, dotInfo.y - radius + moveY, dotInfo.x + radius + moveX, dotInfo.y + radius + moveY);
            }
            // 显示批注形状
            drawAnchor(getAnchorDataPoint(), dataID, i, canvas, dotInfo.x, dotInfo.y, radius);
            if (bd.getLabelVisible()) {
                bd.getLabelOptions().drawLabel(canvas, pLine.getDotLabelPaint(), getFormatterItemLabel(dotInfo.value), dotInfo.x, dotInfo.y, itemAngle, bd.getLineColor());
            }
        }
        return true;
    }

    private float getLineMaxMinY(int type, List<PointF> lstPathPoints) {
        // 渲染高度
        float lineMaxY = 0.0f;
        float lineMinY = 0.0f;
        int count = lstPathPoints.size();
        for (int i = 0; i < count; i++) {
            if (Y_MAX == type) {
                if (lineMaxY < lstPathPoints.get(i).y)
                    lineMaxY = lstPathPoints.get(i).y;
            } else if (Y_MIN == type) {
                if (0 == i) {
                    lineMinY = lstPathPoints.get(0).y;
                } else {
                    if (lineMinY > lstPathPoints.get(i).y)
                        lineMinY = lstPathPoints.get(i).y;
                }
            }
        }
        if (Y_MAX == type) {
            return lineMaxY;
        } else { // if(Y_MIN == type){
            return lineMinY;
        }
    }

    private boolean renderVerticalPlot(Canvas canvas) {
        if (null == areaDatas) {
             LogUtil.Companion.getInstance().print("数据源为空.");
            return false;
        }
        //this.initPaint();
        if (null == areaPath)
            areaPath = new Path();
        // 透明度。其取值范围是0---255,数值越小，越透明，颜色上表现越淡
        // areaFillPaint.setAlpha( areaAlpha );
        // 开始处 X 轴 即分类轴
        int count = areaDatas.size();
        for (int i = 0; i < count; i++) {
            AreaData areaData = this.areaDatas.get(i);
            calcAllPoints(areaData, dotInfos, pointFS, pointFs);
            switch (getCrurveLineStyle()) {
                case BEZIERCURVE:
                    renderBezierArea(canvas, getAreaFillPaint(), areaPath, areaData, pointFs);
                    renderBezierCurveLine(canvas, areaPath, areaData, pointFS);
                    break;
                case BEELINE:
                    renderArea(canvas, getAreaFillPaint(), areaPath, areaData, pointFs);
                    renderLine(canvas, areaData, pointFS);
                    break;
                default:
                     LogUtil.Companion.getInstance().print("未知的枚举类型.");
                    continue;
            }
            renderDotAndLabel(canvas, areaData, i, dotInfos);
            lnDatas.add(areaData);
            dotInfos.clear();
            pointFS.clear();
            pointFs.clear();
        }
        return true;
    }

    @Override
    protected void drawClipPlot(Canvas canvas) {
        if (renderVerticalPlot(canvas)) {
            // 画横向定制线
            if (null != plotCustomLine) {
                plotCustomLine.setVerticalPlot(dataAxisRender, plotAreaRender, getAxisScreenHeight());
                plotCustomLine.renderVerticalCustomlinesDataAxis(canvas);
            }
        }
    }

    @Override
    protected void drawClipLegend(Canvas canvas) {
        plotLegendRender.renderLineKey(canvas, lnDatas);
        lnDatas.clear();
    }

}
