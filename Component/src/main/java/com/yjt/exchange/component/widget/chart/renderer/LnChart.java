package com.hynet.heebit.components.widget.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.CustomLineData;
import com.hynet.heebit.components.widget.chart.constant.AxisLocation;
import com.hynet.heebit.components.widget.chart.constant.BarCenterStyle;
import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.LegendType;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.event.click.PointPosition;
import com.hynet.heebit.components.widget.chart.renderer.info.AnchorDataPoint;
import com.hynet.heebit.components.widget.chart.renderer.info.PlotAxisTick;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotCustomLine;
import com.hynet.heebit.components.widget.chart.utils.BezierCurvesUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;
import com.hynet.heebit.components.widget.chart.utils.PointUtil;

import java.util.List;

public class LnChart extends AxesChart {

    private PointF[] bezierControls;
    // 批注
    private List<AnchorDataPoint> anchorDataPoints;
    // 用于绘制定制线(分界线)
    protected PlotCustomLine plotCustomLine = null;
    //坐标从第一个刻度而不是轴开始
    protected boolean xCoordFirstTickmarksBegin = false;
    //标签和对象依哪种风格居中显示
    protected BarCenterStyle barCenterStyle = BarCenterStyle.TICKMARKS;

    public LnChart() {

        // 初始化图例
        if (null != plotLegendRender) {
            plotLegendRender.show();
            plotLegendRender.setType(LegendType.ROW);
            plotLegendRender.setHorizontalAlign(HorizontalAlign.LEFT);
            plotLegendRender.setVerticalAlign(VerticalAlign.TOP);
            plotLegendRender.hideBox();
        }

        categoryAxisDefaultSetting();
        dataAxisDefaultSetting();
    }

    /**
     * 返回指定数据在图中的竖向坐标位置
     *
     * @param bv 数据
     *
     * @return 坐标位置
     */
    public float getVPValPosition(double bv) {
        float vaxlen = (float) MathUtil.getInstance().sub(bv, dataAxisRender.getAxisMin());
        float valuePostion = mul(getPlotScreenHeight(), div(vaxlen, dataAxisRender.getAxisRange()));
        return (sub(plotAreaRender.getBottom(), valuePostion));
    }

    protected float getLnXValPosition(double xValue, double maxValue, double minValue) {
        // 对应的X坐标
        double maxminRange = MathUtil.getInstance().sub(maxValue, minValue);
        double xScale = MathUtil.getInstance().div(MathUtil.getInstance().sub(xValue, minValue), maxminRange);
        float XvaluePos = mul(getPlotScreenWidth(), (float) xScale);
        return add(plotAreaRender.getLeft(), XvaluePos);
    }

    private float getVPDataAxisStdY() {
        if (dataAxisRender.getAxisStdStatus()) {
            return getVPValPosition(dataAxisRender.getAxisStd());
        } else {
            return plotAreaRender.getBottom();
        }
    }

    @Override
    protected float getAxisYPos(AxisLocation location) {
        if (dataAxisRender.getAxisStdStatus() && categoryAxisRender.getAxisBuildStdStatus()) {
            return getVPDataAxisStdY();
        } else {
            return super.getAxisYPos(location);
        }
    }

    /**
     * 设置定制线值
     *
     * @param customLineDatas 定制线数据集合
     */
    public void setCustomLines(List<CustomLineData> customLineDatas) {
        if (null == plotCustomLine)
            plotCustomLine = new PlotCustomLine();
        plotCustomLine.setCustomLines(customLineDatas);
    }

    /**
     * 绘制底部标签轴
     */
    @Override
    protected void drawClipDataAxisGridlines(Canvas canvas) {
        // 与柱形图不同，无须多弄一个
        float XSteps = 0.0f, YSteps = 0.0f;
        // 数据轴数据刻度总个数
        int tickCount = dataAxisRender.getAixTickCount();
        int labeltickCount = tickCount;
        if (0 == tickCount) {
             LogUtil.Companion.getInstance().print("数据源个数为0!");
            return;
        } else if (1 == tickCount) // label仅一个时右移
            labeltickCount = tickCount - 1;
        // 标签轴(X 轴)
        float axisX = 0.0f, axisY = 0.0f, currentX = 0.0f, currentY = 0.0f;
        // 标签
        double currentTickLabel = 0d;
        // 轴位置
        AxisLocation pos = getDataAxisLocation();
        // 步长
        switch (pos) {
            case LEFT: // Y
            case RIGHT:
            case VERTICAL_CENTER:
                YSteps = getVerticalYSteps(labeltickCount);
                currentX = axisX = getAxisXPos(pos);
                currentY = axisY = plotAreaRender.getBottom();
                break;
            case TOP: // X
            case BOTTOM:
            case HORIZONTAL_CENTER:
                XSteps = getVerticalXSteps(labeltickCount);
                currentY = axisY = getAxisYPos(pos);
                currentX = axisX = plotAreaRender.getLeft();
                break;
            default:
                 LogUtil.Companion.getInstance().print("未知的枚举类型.");
        }
        dataTick.clear();
        // 绘制
        for (int i = 0; i < tickCount + 1; i++) {
            switch (pos) {
                case LEFT: // Y
                case RIGHT:
                case VERTICAL_CENTER:
                    // 依起始数据坐标与数据刻度间距算出上移高度
                    currentY = sub(plotAreaRender.getBottom(), mul(i, YSteps));
                    // 从左到右的横向网格线
                    drawHorizontalGridLines(canvas, plotAreaRender.getLeft(), plotAreaRender.getRight(), i, tickCount + 1, YSteps, currentY);
                    // 这个有点问题，要处理下，
                    // 隐藏时应当不需要这个，但目前主明细模式下，会有问题，加 了一个都显示不出来
                    // 先省略了
                    // if(!dataAxisRender.isShowAxisLabels())continue;
                    // 标签
                    currentTickLabel = MathUtil.getInstance().add(dataAxisRender.getAxisMin(), mul(i, (float) dataAxisRender.getAxisSteps()));
                    dataTick.add(new PlotAxisTick(i, axisX, currentY, Double.toString(currentTickLabel)));
                    break;
                case TOP: // X
                case BOTTOM:
                case HORIZONTAL_CENTER:
                    // 依初超始X坐标与标签间距算出当前刻度的X坐标
                    currentX = add(plotAreaRender.getLeft(), mul(i, XSteps));
                    // 绘制竖向网格线
                    drawVerticalGridLines(canvas, plotAreaRender.getTop(), plotAreaRender.getBottom(), i, tickCount + 1, XSteps, currentX);
                    // 画上标签/刻度线
                    currentTickLabel = MathUtil.getInstance().add(dataAxisRender.getAxisMin(), mul(i, (float) dataAxisRender.getAxisSteps()));
                    dataTick.add(new PlotAxisTick(i, currentX, axisY, Double.toString(currentTickLabel)));
                    break;
            }
        }
    }

    protected int getCategoryAxisCount() {
        int tickCount = categoryAxisRender.getDataSet().size();
        int labeltickCount = 0;
        if (0 == tickCount) {
             LogUtil.Companion.getInstance().print("分类轴数据源为0.");
            return labeltickCount;
        } else if (1 == tickCount) { // label仅一个时右移		
            labeltickCount = tickCount;
        } else {
            if (xCoordFirstTickmarksBegin) {
                if (BarCenterStyle.SPACE == barCenterStyle) {
                    labeltickCount = tickCount;
                } else {
                    labeltickCount = tickCount + 1;
                }
            } else {
                labeltickCount = tickCount - 1;
            }
        }
        return labeltickCount;
    }

    /**
     * 绘制底部标签轴
     */
    @Override
    protected void drawClipCategoryAxisGridlines(Canvas canvas) {
        // 得到标签轴数据集
        List<String> dataSet = categoryAxisRender.getDataSet();
        if (null == dataSet) return;
        // 与柱形图不同，无须多弄一个
        float XSteps = 0.0f, YSteps = 0.0f;
        int j = 0;
        int tickCount = dataSet.size();
        if (0 == tickCount) {
             LogUtil.Companion.getInstance().print("分类轴数据源为0.");
            return;
        } else if (1 == tickCount) {// label仅一个时右移
            j = 1;
        }
        int labeltickCount = getCategoryAxisCount();
        // 标签轴(X 轴)
        float axisX = 0.0f, axisY = 0.0f, currentX = 0.0f, currentY = 0.0f;
        AxisLocation pos = getCategoryAxisLocation();
        if (AxisLocation.LEFT == pos || AxisLocation.RIGHT == pos || AxisLocation.VERTICAL_CENTER == pos) {
            YSteps = getVerticalYSteps(labeltickCount);
            currentX = axisX = getAxisXPos(pos);
            currentY = axisY = plotAreaRender.getBottom();
        } else { // TOP BOTTOM
            XSteps = getVerticalXSteps(labeltickCount);
            currentY = axisY = getAxisYPos(pos);
            currentX = axisX = plotAreaRender.getLeft();
        }
        cateTick.clear();
        float labelX, labelY;
        boolean showTicks = true;
        // 绘制
        for (int i = 0; i < tickCount; i++) {
            switch (pos) {
                case LEFT: // Y
                case RIGHT:
                case VERTICAL_CENTER:
                    // 依起始数据坐标与数据刻度间距算出上移高度
                    //currentY = sub(plotAreaRender.getBottom(), j * YSteps);
                    if (xCoordFirstTickmarksBegin) {
                        currentY = sub(plotAreaRender.getBottom(), mul((j + 1), YSteps));
                    } else {
                        currentY = sub(plotAreaRender.getBottom(), mul(j, YSteps));
                    }
                    // 从左到右的横向网格线
                    drawHorizontalGridLines(canvas, plotAreaRender.getLeft(), plotAreaRender.getRight(), i, tickCount, YSteps, currentY);
                    if (!categoryAxisRender.isShowAxisLabels())
                        continue;
                    //cateTick.add(new PlotAxisTick(axisX, currentY, dataSet.get(i)));
                    labelX = axisX;
                    labelY = currentY;
                    if (xCoordFirstTickmarksBegin && BarCenterStyle.SPACE == barCenterStyle) {
                        if (i == tickCount - 1) showTicks = false;
                        labelY = add(currentY, div(YSteps, 2));
                    }
                    cateTick.add(new PlotAxisTick(axisX, currentY, dataSet.get(i), labelX, labelY, showTicks));
                    break;
                case TOP: // X
                case BOTTOM:
                case HORIZONTAL_CENTER:
                    // 依初超始X坐标与标签间距算出当前刻度的X坐标
                    if (xCoordFirstTickmarksBegin) {
                        currentX = add(plotAreaRender.getLeft(), mul((j + 1), XSteps));
                    } else {
                        currentX = add(plotAreaRender.getLeft(), mul(j, XSteps));
                    }
                    // 绘制竖向网格线
                    drawVerticalGridLines(canvas, plotAreaRender.getTop(), plotAreaRender.getBottom(), i, tickCount, XSteps, currentX);
                    if (!categoryAxisRender.isShowAxisLabels())
                        continue;
                    labelX = currentX;
                    labelY = axisY;
                    if (xCoordFirstTickmarksBegin && BarCenterStyle.SPACE == barCenterStyle) {
                        if (i == tickCount - 1) showTicks = false;
                        labelX = sub(currentX, div(XSteps, 2));
                    }
                    cateTick.add(new PlotAxisTick(currentX, axisY, dataSet.get(i), labelX, labelY, showTicks));
                    break;
            }
            j++;
        }

    }

    @Override
    public boolean isPlotClickArea(float x, float y) {
        if (!getListenItemClickStatus())
            return false;
        if (Float.compare(x, getLeft()) == -1)
            return false;
        if (Float.compare(x, getRight()) == 1)
            return false;
        if (Float.compare(y, getPlotArea().getTop()) == -1)
            return false;
        if (Float.compare(y, getPlotArea().getBottom()) == 1)
            return false;
        return true;
    }

    /**
     * 返回当前点击点的信息
     *
     * @param x 点击点X坐标
     * @param y 点击点Y坐标
     *
     * @return 返回对应的位置记录
     */
    public PointPosition getPositionRecord(float x, float y) {
        return getPointRecord(x, y);
    }

    // 遍历曲线
    protected void renderBezierCurveLine(Canvas canvas, Paint paint, Path bezierPath, List<PointF> lstPoints) {
        if (null == bezierControls)
            bezierControls = new PointF[2];
        paint.setStyle(Style.STROKE);
        int count = lstPoints.size();
        if (count <= 2)
            return; // 没有或仅一个点就不需要了
        if (count == 3) {
            if (null == bezierPath)
                bezierPath = new Path();
            bezierPath.moveTo(lstPoints.get(0).x, lstPoints.get(0).y);
            PointF ctl3 = PointUtil.percent(lstPoints.get(1), 0.5f, lstPoints.get(2), 0.8f);
            bezierPath.quadTo(ctl3.x, ctl3.y, lstPoints.get(2).x, lstPoints.get(2).y);
            canvas.drawPath(bezierPath, paint);
            bezierPath.reset();
            return;
        }
        float axisMinValue = plotAreaRender.getBottom();
        for (int i = 0; i < count; i++) {
            if (i < 3)
                continue;
            // 连续两个值都为0,控制点有可能会显示在轴以下，则此种情况下，将其处理为直线
            if (lstPoints.get(i - 1).y >= axisMinValue && lstPoints.get(i).y >= axisMinValue) {
                if (null == bezierPath) {
                    bezierPath = new Path();
                }
                bezierPath.reset();
                bezierPath.moveTo(lstPoints.get(i - 2).x, lstPoints.get(i - 2).y);
                // change by chenqiang
                if (lstPoints.get(i - 2).y >= axisMinValue) {//连续3个点为0
                    bezierPath.lineTo(lstPoints.get(i - 1).x, lstPoints.get(i - 1).y);
                } else {
                    BezierCurvesUtil.curve3(lstPoints.get(i - 2), lstPoints.get(i - 1), lstPoints.get(i - 3), lstPoints.get(i), bezierControls);
                    bezierPath.quadTo(bezierControls[0].x, bezierControls[0].y, lstPoints.get(i - 1).x, lstPoints.get(i - 1).y);
                    // i-2与i-1之间的曲线
                    canvas.drawPath(bezierPath, paint);
                    bezierPath.reset();
                }
                // i-1与i之间的直线
                canvas.drawLine(lstPoints.get(i - 1).x, lstPoints.get(i - 1).y, lstPoints.get(i).x, lstPoints.get(i).y, paint);
                continue;
            }
            BezierCurvesUtil.curve3(lstPoints.get(i - 2), lstPoints.get(i - 1), lstPoints.get(i - 3), lstPoints.get(i), bezierControls);
            // change by chenqiang
            renderBezierCurvePath(canvas, paint, bezierPath, lstPoints.get(i - 2), lstPoints.get(i - 1), bezierControls);
        }

        if (count > 3) {
            PointF stop = lstPoints.get(count - 1);
            // PointF start = lstPoints.get(lstPoints.size()-2);
            BezierCurvesUtil.curve3(lstPoints.get(count - 2), stop, lstPoints.get(count - 3), stop, bezierControls);
            renderBezierCurvePath(canvas, paint, bezierPath, lstPoints.get(count - 2), lstPoints.get(count - 1), bezierControls);
        }
    }

    // 绘制曲线
    private void renderBezierCurvePath(Canvas canvas, Paint paint, Path bezierPath, PointF start, PointF stop, PointF[] bezierControls) {
        if (null == bezierPath)
            bezierPath = new Path();
        bezierPath.reset();
        bezierPath.moveTo(start.x, start.y);
        // change by chenqiang
        bezierCurvePathAxisMinValue(bezierPath, start, stop, bezierControls);
        canvas.drawPath(bezierPath, paint);
        bezierPath.reset();
    }


    // add by chenqiang
    protected void bezierCurvePathAxisMinValue(Path bezierPath, PointF start, PointF stop, PointF[] bezierControls) {
        float axisMinValue = plotAreaRender.getBottom();
        if (start.y >= axisMinValue && stop.y >= axisMinValue) {
            bezierPath.lineTo(stop.x, stop.y);
        } else {
            if (bezierControls[0].y >= axisMinValue && bezierControls[1].y >= axisMinValue) {
                bezierPath.lineTo(stop.x, stop.y);
            } else if (bezierControls[0].y >= axisMinValue && bezierControls[1].y < axisMinValue) {
                bezierPath.cubicTo(bezierControls[0].x, axisMinValue, bezierControls[1].x, bezierControls[1].y, stop.x, stop.y);
            } else if (bezierControls[0].y < axisMinValue && bezierControls[1].y >= axisMinValue) {
                bezierPath.cubicTo(bezierControls[0].x, bezierControls[0].y, bezierControls[1].x, axisMinValue, stop.x, stop.y);
            } else {
                bezierPath.cubicTo(bezierControls[0].x, bezierControls[0].y, bezierControls[1].x, bezierControls[1].y, stop.x, stop.y);
            }
        }
    }

    /**
     * 设置批注
     *
     * @param anchor 批注
     */
    public void setAnchorDataPoint(List<AnchorDataPoint> anchor) {
        anchorDataPoints = anchor;
    }

    /**
     * 返回批注
     *
     * @return 批注
     */
    public List<AnchorDataPoint> getAnchorDataPoint() {
        return anchorDataPoints;
    }
}
