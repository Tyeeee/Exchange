package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;
import java.util.Map;

public class RoseChart extends PieChart {

    private Paint innerPaint = null;
    private boolean showInner = true;
    private int intervalAngle = 0;
    private Paint backgroundPaint = null;
    private boolean showBgLines = false;
    private boolean showBgCircle = false;
    private Map<Float, Integer> map = null;
    private int showBgLineColor = Color.BLACK;
    private boolean showOuterLabels = false;
    private int lines = 0;

    public RoseChart() {
        initializeChart();
    }

    @Override
    public ChartType getType() {
        return ChartType.ROSE;
    }

    private void initializeChart() {
        //白色标签
        if (null != getLabelPaint()) {
            getLabelPaint().setColor(Color.WHITE);
            getLabelPaint().setTextSize(22);
            getLabelPaint().setTextAlign(Align.CENTER);
        }
    }

    /**
     * 开放内部背景画笔
     *
     * @return 画笔
     */
    public Paint getInnerPaint() {
        //深色内环
        if (null == innerPaint) {
            innerPaint = new Paint();
            innerPaint.setColor(Color.rgb(68, 68, 68));
            innerPaint.setStyle(Style.FILL);
            innerPaint.setAntiAlias(true);
        }
        return innerPaint;
    }

    /**
     * 设置各扇区间隔角度
     *
     * @param angle 角度
     */
    public void setIntervalAngle(int angle) {
        intervalAngle = angle;
    }

    /**
     * 显示背景环
     */
    public void showInner() {
        showInner = true;
    }

    /**
     * 隐藏背景环
     */
    public void hideInner() {
        showInner = false;
    }

    /**
     * 标签显示在外环上
     */
    public void showOuterLabels() {
        showOuterLabels = true;
    }

    /**
     * 标签不显示在外环
     */
    public void hideOuterLabels() {
        showOuterLabels = false;
    }

    /**
     * 用于绘制背景线，圈的画笔
     *
     * @return 画笔
     */
    public Paint getBgPaint() {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaint.setStyle(Style.STROKE);
            backgroundPaint.setAntiAlias(true);
        }
        return backgroundPaint;
    }

    /**
     * 显示背景线，并指定线的颜色
     *
     * @param color 颜色
     */
    public void showBgLines(int color) {
        showBgLines = true;
        showBgLineColor = color;

    }

    /**
     * 依map传入的比例来设定显示几个圈,并指定各自的颜色
     *
     * @param bgSeg 比例,线颜色
     */
    public void showBgCircle(Map<Float, Integer> bgSeg) {
        showBgCircle = true;
        map = bgSeg;
    }

    /**
     * 显示背景直线
     */
    public void hideBgLines() {
        showBgLines = false;
    }

    /**
     * 不显示背景直线
     */
    public void hideBgCircle() {
        showBgCircle = true;
    }

    /**
     * 指定要显示的线个数
     *
     * @param count 总个数
     */
    public void setBgLines(int count) {
        lines = count;
    }

    @Override
    protected boolean validateParams() {
        return true;
    }

    /**
     * 绘制背景直线与圈
     *
     * @param canvas 画布
     */
    private void drawBGCircle(Canvas canvas) {
        if (!showBgCircle) return;
        if (map == null) return;
        float radius = getRadius();
        for (Map.Entry<Float, Integer> entry : map.entrySet()) {
            float newRadius = mul(radius, entry.getKey());
            if (Float.compare(newRadius, 0.0f) == 0
                    || Float.compare(newRadius, 0.0f) == -1) {
                continue;
            }

            getBgPaint().setColor(entry.getValue());
            canvas.drawCircle(plotAreaRender.getCenterX(), plotAreaRender.getCenterY(),
                              newRadius, getBgPaint());
        }
    }

    private void drawBGLines(Canvas canvas) {
        if (!showBgLines) return;
        if (0 == lines) return;

        int totalAngle = 360 - intervalAngle * lines;

        float currAngle = totalAngle / lines;
        float radius = getRadius();
        float angle = offsetAngle;

        for (int i = 0; i < lines; i++) {
            PointF pointbg = MathUtil.getInstance().calcArcEndPointXY(plotAreaRender.getCenterX(), plotAreaRender.getCenterY(), radius, angle + intervalAngle + currAngle / 2);
            getBgPaint().setColor(showBgLineColor);
            canvas.drawLine(plotAreaRender.getCenterX(), plotAreaRender.getCenterY(), pointbg.x, pointbg.y, getBgPaint());
            angle = add(add(angle, currAngle), intervalAngle);
        }
    }

    private float getLabelRadius() {
        float labelRadius = 0.f;
        float radius = getRadius();
        if (showOuterLabels) {
            labelRadius = radius + DrawUtil.getInstance().getPaintFontHeight(this.getLabelPaint());
        } else {
            labelRadius = radius - radius / 2 / 2;
        }
        return labelRadius;
    }


    /**
     * 绘制图
     */
    @Override
    protected boolean renderPlot(Canvas canvas) {
        //计算中心点坐标
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        float radius = getRadius();
        float arcAngle = 0.0f;
        float newRaidus = 0.0f;
        //数据源
        List<PieData> chartDataSource = this.getDataSource();
        if (null == chartDataSource || chartDataSource.size() == 0) {
             LogUtil.Companion.getInstance().print("数据源为空.");
            return false;
        }
        //内环
        if (showInner) canvas.drawCircle(cirX, cirY, radius, getInnerPaint());
        //画背景
        drawBGCircle(canvas);
        //画背景直线
        drawBGLines(canvas);
        //依参数个数，算出总个要算多少个扇区的角度
        int totalAngle = 360 - intervalAngle * chartDataSource.size();
        arcAngle = totalAngle / chartDataSource.size();
        arcAngle = div(mul(arcAngle, 100), 100);
        if (!validateAngle(arcAngle)) {
             LogUtil.Companion.getInstance().print("计算出来的扇区角度小于等于0度,不能绘制.");
            return false;
        }
        float labelRadius = getLabelRadius();
        for (PieData cData : chartDataSource) {
            geArcPaint().setColor(cData.getSliceColor());
            //将百分比转换为新扇区的半径    
            double p = cData.getPercentage() / 100;
            newRaidus = mul(radius, (float) (p));
            newRaidus = div(mul(newRaidus, 100), 100);
            //在饼图中显示所占比例   
            RectF nRF = new RectF(sub(cirX, newRaidus), sub(cirY, newRaidus), add(cirX, newRaidus), add(cirY, newRaidus));
            canvas.drawArc(nRF, offsetAngle + intervalAngle, arcAngle, true, geArcPaint());
            //标识  
            String label = cData.getLabel();
            if (!TextUtils.isEmpty(label)) {
                //计算百分比标签 
                PointF point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, labelRadius, offsetAngle + intervalAngle + arcAngle / 2);
                //请自行在回调函数中处理显示格式
                DrawUtil.getInstance().drawRotateText(label, point.x, point.y, cData.getItemLabelRotateAngle(), canvas, getLabelPaint());
            }
            //下次的起始角度  
            offsetAngle = add(add(offsetAngle, arcAngle), intervalAngle);
        }
        return true;
    }
}
