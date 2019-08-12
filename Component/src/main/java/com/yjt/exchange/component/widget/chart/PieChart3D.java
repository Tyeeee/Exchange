package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.renderer.info.PlotArcLabelInfo;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class PieChart3D extends PieChart {

    //渲染层数
    private final int render3DLevel = 15;

    public PieChart3D() {

    }

    @Override
    public ChartType getType() {
        return ChartType.PIE3D;
    }

    private boolean render3D(Canvas canvas, float initOffsetAngle, List<PieData> chartDataSource, float cirX, float cirY, float radius) {
        if (null == chartDataSource) return false;
        float offsetAngle = initOffsetAngle;
        float currentAngle = 0.0f;
        float newRadius = 0.0f;
        int size = 0;
        for (int i = 0; i < render3DLevel; i++) {
//            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.save();
            canvas.translate(0, render3DLevel - i);
            size = chartDataSource.size();
            for (int j = 0; j < size; j++) {
                PieData cData = chartDataSource.get(j);
                if (null == cData) continue;
                currentAngle = MathUtil.getInstance().getSliceAngle(getTotalAngle(), (float) cData.getPercentage());
                if (!validateAngle(currentAngle)) continue;
                geArcPaint().setColor(cData.getSliceColor());
                if (cData.getSelected()) {//指定突出哪个块
                    //偏移圆心点位置(默认偏移半径的1/10)
                    newRadius = div(radius, getSelectedOffset());
                    //计算百分比标签
                    PointF point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, newRadius, add(offsetAngle, div(currentAngle, 2f)));
                    initializeRectF(sub(point.x, radius), sub(point.y, radius), add(point.x, radius), add(point.y, radius));
                    canvas.drawArc(rectF, offsetAngle, currentAngle, true, geArcPaint());
                } else {
                    //确定饼图范围       
                    initializeRectF(sub(cirX, radius), sub(cirY, radius), add(cirX, radius), add(cirY, radius));
                    canvas.drawArc(rectF, offsetAngle, currentAngle, true, geArcPaint());
                }
                //下次的起始角度  
                offsetAngle = add(offsetAngle, currentAngle);
            }
            canvas.restore();
            offsetAngle = initOffsetAngle;
        }
        return true;
    }

    private boolean renderFlatArcAndLegend(Canvas canvas, float initOffsetAngle, List<PieData> chartDataSource, float cirX, float cirY, float radius) {
        if (null == chartDataSource) return false;
        float offsetAngle = initOffsetAngle;
        float currentAngle = 0.0f;
        float newRadius = 0.0f;
        int size = chartDataSource.size();
        plotArcLabelInfos.clear();
        float left = sub(cirX, radius);
        float top = sub(cirY, radius);
        float right = add(cirX, radius);
        float bottom = add(cirY, radius);
        for (int j = 0; j < size; j++) {
            PieData cData = chartDataSource.get(j);
            if (null == cData) continue;
            //currentAngle = cData.getSliceAngle();
            currentAngle = MathUtil.getInstance().getSliceAngle(getTotalAngle(), (float) cData.getPercentage());
            if (!validateAngle(currentAngle)) continue;
            geArcPaint().setColor(DrawUtil.getInstance().getDarkerColor(cData.getSliceColor()));
            if (cData.getSelected()) { //指定突出哪个块
                //偏移圆心点位置(默认偏移半径的1/10)
                newRadius = div(radius, getSelectedOffset());
                //计算百分比标签
                PointF point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, newRadius, add(offsetAngle, div(currentAngle, 2f)));
                initializeRectF(sub(point.x, radius), sub(point.y, radius), add(point.x, radius), add(point.y, radius));
                canvas.drawArc(rectF, offsetAngle, currentAngle, true, geArcPaint());
                plotArcLabelInfos.add(new PlotArcLabelInfo(j, point.x, point.y, radius, offsetAngle, currentAngle));
            } else {
                //确定饼图范围       
                initializeRectF(left, top, right, bottom);
                canvas.drawArc(rectF, offsetAngle, currentAngle, true, geArcPaint());
                plotArcLabelInfos.add(new PlotArcLabelInfo(j, cirX, cirY, radius, offsetAngle, currentAngle));
            }
            //保存角度
            saveArcRecord(j, cirX + this.translateXY[0], cirY + this.translateXY[1], radius, offsetAngle, currentAngle, getSelectedOffset(), this.getOffsetAngle());
            //下次的起始角度  
            offsetAngle = add(offsetAngle, currentAngle);
        }
        //绘制Label
        renderLabels(canvas);
        //图例
        plotLegendRender.renderPieKey(canvas, chartDataSource);
        return true;
    }


    @Override
    protected boolean renderPlot(Canvas canvas) {
        //数据源
        List<PieData> chartDataSource = this.getDataSource();
        if (null == chartDataSource) {
             LogUtil.Companion.getInstance().print("数据源为空.");
            return false;
        }
        //计算中心点坐标
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        float radius = getRadius();
        if (render3D(canvas, offsetAngle, chartDataSource, cirX, cirY, radius)) {
            return renderFlatArcAndLegend(canvas, offsetAngle, chartDataSource, cirX, cirY, radius);
        } else {
            return false;
        }
    }
}
