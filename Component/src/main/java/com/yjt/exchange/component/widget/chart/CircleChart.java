package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;

import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.CircleType;
import com.hynet.heebit.components.widget.chart.renderer.CirChart;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class CircleChart extends CirChart {

    private String data;
    private CircleType circleType = CircleType.FULL;
    //内环填充颜色
    private Paint backgroundCirclePaint = null;
    private Paint fillCirclePaint = null;
    private Paint dataPaint = null;
    //内部填充
    private boolean showInnerFill = true;
    //内部背景填充
    private boolean showInnerBackground = true;
    //显示圆形箭头标示
    private boolean showCap = false;
    //数据源
    protected List<PieData> pieData;
    private float moRadius = 0.9f;
    private float miRadius = 0.8f;

    public CircleChart() {

        initChart();
    }

    @Override
    public ChartType getType() {
        return ChartType.CIRCLE;
    }

    private void initChart() {
        if (null != getLabelPaint()) {
            getLabelPaint().setColor(Color.WHITE);
            getLabelPaint().setTextSize(36);
            getLabelPaint().setTextAlign(Align.CENTER);
        }
        //设置起始偏移角度
        setInitialAngle(180);
    }


    /**
     * 设置图表的数据源
     *
     * @param piedata 来源数据集
     */
    public void setDataSource(List<PieData> piedata) {
        this.pieData = piedata;
    }

    /**
     * 设置附加信息
     *
     * @param info 附加信息
     */
    public void setAttributeInfo(String info) {
        data = info;
    }

    /**
     * 设置圆是显示成完整的一个图还是只显示成一个半圆
     *
     * @param display 半圆/完整圆
     */
    public void setCircleType(CircleType display) {
        circleType = display;
    }

    /**
     * 开放内部填充的画笔
     *
     * @return 画笔
     */
    public Paint getFillCirclePaint() {
        if (null == fillCirclePaint) {
            fillCirclePaint = new Paint();
            fillCirclePaint.setColor(Color.rgb(77, 83, 97));
            fillCirclePaint.setAntiAlias(true);
        }
        return fillCirclePaint;
    }

    /**
     * 开放内部背景填充的画笔
     *
     * @return 画笔
     */
    public Paint getBgCirclePaint() {
        if (null == backgroundCirclePaint) {
            backgroundCirclePaint = new Paint();
            backgroundCirclePaint.setColor(Color.rgb(148, 159, 181));
            backgroundCirclePaint.setAntiAlias(true);
        }
        return backgroundCirclePaint;
    }

    /**
     * 开放绘制附加信息的画笔
     *
     * @return 画笔
     */
    public Paint getDataInfoPaint() {
        if (null == dataPaint) {
            dataPaint = new Paint();
            dataPaint.setTextSize(22);
            dataPaint.setColor(Color.WHITE);
            dataPaint.setTextAlign(Align.CENTER);
            dataPaint.setAntiAlias(true);
        }

        return dataPaint;
    }

    /**
     * 隐藏内部背景填充
     */
    public void hideInnerFill() {
        showInnerFill = false;
    }

    /**
     * 隐藏背景
     */
    public void hideInnerBackground() {
        showInnerBackground = false;
    }

    /**
     * 隐藏内部背景填充
     */
    public void showInnerFill() {
        showInnerFill = true;
    }

    /**
     * 显示内部背景填充
     *
     * @return 显示状态
     */
    public boolean isShowInnerFill() {
        return showInnerFill;
    }

    /**
     * 隐藏背景
     */
    public void showInnerBackground() {
        showInnerBackground = true;
    }

    /**
     * 背景显示状态
     *
     * @return 显示状态
     */
    public boolean isShowInnerBackground() {
        return showInnerBackground;
    }

    /**
     * 外环
     *
     * @param radius 半径比例
     */
    public void setORadius(float radius) {
        moRadius = radius;
    }

    /**
     * 内环
     *
     * @param radius 半径比例
     */
    public void setIRadius(float radius) {
        miRadius = radius;
    }

    /**
     * 是否显示圆形箭头标示(仅限360度圆形才有)
     *
     * @return 状态
     */
    public boolean isShowCap() {
        return showCap;
    }

    /**
     * 显示圆形箭头标示(仅限360度圆形才有. <br/>
     * 起始色默认为圆背景色，如没设，则默认为内部填充色)
     */
    public void ShowCap() {
        showCap = true;
    }

    /**
     * 隐藏圆形箭头标示
     */
    public void HideCap() {
        showCap = false;
    }


    /**
     * 依比例绘制扇区
     *
     * @param paintArc    画笔
     * @param cirX        x坐标
     * @param cirY        y坐标
     * @param radius      半径
     * @param offsetAngle 偏移
     * @param curretAngle 当前值
     *
     * @throws Exception 例外
     */
    protected void drawPercent(Canvas canvas, Paint paintArc, final float cirX, final float cirY, final float radius, final float offsetAngle, final float curretAngle) {
        float arcLeft = sub(cirX, radius);
        float arcTop = sub(cirY, radius);
        float arcRight = add(cirX, radius);
        float arcBottom = add(cirY, radius);
        RectF arcRF0 = new RectF(arcLeft, arcTop, arcRight, arcBottom);
        //在饼图中显示所占比例
        canvas.drawArc(arcRF0, offsetAngle, curretAngle, true, paintArc);
    }

    private float getCirY(float cirY, float labelHeight) {
        float txtY = cirY;
        if (TextUtils.isEmpty(data)) {
            txtY = cirY + labelHeight / 3;
        }
        return txtY;
    }


    /**
     * 绘制图
     */
    protected boolean renderPlot(Canvas canvas) {
        //中心点坐标
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        float radius = getRadius();
        //确定去饼图范围
        float arcLeft = sub(cirX, radius);
        float arcTop = sub(cirY, radius);
        float arcRight = add(cirX, radius);
        float arcBottom = add(cirY, radius);
        RectF arcRF0 = new RectF(arcLeft, arcTop, arcRight, arcBottom);
        //画笔初始化
        Paint paintArc = new Paint();
        paintArc.setAntiAlias(true);
        //用于存放当前百分比的圆心角度
        float currentAngle = 0.0f;
        float infoHeight = DrawUtil.getInstance().getPaintFontHeight(getDataInfoPaint());
        float LabelHeight = DrawUtil.getInstance().getPaintFontHeight(getLabelPaint());
        float textHeight = LabelHeight + infoHeight;
        for (PieData cData : pieData) {
            paintArc.setColor(cData.getSliceColor());
            if (CircleType.HALF == circleType) {
                setInitialAngle(180);
                //半圆， 宽应当是高的两倍
                float hRadius = this.getWidth() / 2.f;
                float hCirY = this.getBottom();
                if (this.isShowBorder()) {
                    hRadius -= this.getBorderWidth();
                    hCirY -= this.getBorderWidth() / 2;
                }
                float oRadius = MathUtil.getInstance().round(mul(hRadius, moRadius), 2);
                float iRadius = MathUtil.getInstance().round(mul(hRadius, miRadius), 2);
                //内部背景填充
                if (isShowInnerBackground()) {
                    drawPercent(canvas, getBgCirclePaint(), cirX, hCirY, hRadius, 180f, 180f);
                } else {
                    oRadius = iRadius = hRadius;
                }
                if (isShowInnerFill()) {
                    drawPercent(canvas, getFillCirclePaint(), cirX, hCirY, oRadius, 180f, 180f);
                }
                currentAngle = MathUtil.getInstance().getSliceAngle(180f, (float) cData.getPercentage());
                drawPercent(canvas, paintArc, cirX, hCirY, hRadius, 180f, currentAngle);
                //内部填充
                if (isShowInnerFill()) {
                    drawPercent(canvas, getFillCirclePaint(), cirX, hCirY, iRadius, 180f, 180f);
                }
                if (!TextUtils.isEmpty(cData.getLabel())) {
                    canvas.drawText(cData.getLabel(), cirX, sub(hCirY, textHeight), getLabelPaint());
                }
                if (!TextUtils.isEmpty(data)) {
                    canvas.drawText(data, cirX, hCirY - infoHeight, getDataInfoPaint());
                }
            } else {
                currentAngle = MathUtil.getInstance().getSliceAngle(360.f, (float) cData.getPercentage());
                if (isShowInnerBackground())
                    canvas.drawCircle(cirX, cirY, radius, getBgCirclePaint());
                // canvas.drawCircle(cirX, cirY, (float) (Math.round(radius * 0.9f)), fillCirclePaint);
                if (isShowInnerFill()) {
                    float fillRadius = MathUtil.getInstance().round(mul(radius, moRadius), 2);
                    canvas.drawCircle(cirX, cirY, fillRadius, getFillCirclePaint());
                }
                canvas.drawArc(arcRF0, offsetAngle, currentAngle, true, paintArc);
                if (isShowCap() && (isShowInnerBackground() || isShowInnerFill())) {
                    float cap = MathUtil.getInstance().round(mul(radius, miRadius), 2);
                    float capRadius = cap + (radius - cap) / 2;
                    //箭头
                    if (isShowInnerBackground()) {
                        paintArc.setColor(getBgCirclePaint().getColor());
                    } else {
                        paintArc.setColor(getFillCirclePaint().getColor());
                    }
                    PointF pointBegin = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, capRadius, getInitialAngle());

                    canvas.drawLine(cirX, cirY, pointBegin.x, pointBegin.y, paintArc);
                    canvas.drawCircle(pointBegin.x, pointBegin.y, (radius - cap) / 2, paintArc);
                    //箭头                    
                    PointF point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, capRadius, add(offsetAngle, currentAngle));
                    paintArc.setColor(cData.getSliceColor());
                    canvas.drawLine(cirX, cirY, point.x, point.y, paintArc);
                    canvas.drawCircle(point.x, point.y, (radius - cap) / 2, paintArc);
                }
                if (isShowInnerFill())
                    canvas.drawCircle(cirX, cirY, MathUtil.getInstance().round(mul(radius, miRadius), 2), getFillCirclePaint());
                if (!TextUtils.isEmpty(cData.getLabel()))
                    canvas.drawText(cData.getLabel(), cirX, getCirY(cirY, LabelHeight), getLabelPaint());
                if (!TextUtils.isEmpty(data))
                    canvas.drawText(data, cirX, add(cirY, infoHeight), getDataInfoPaint());
            }
            break;
        }
        return true;
    }

    @Override
    protected boolean postRender(Canvas canvas) {
        // 绘制图表
        super.postRender(canvas);
        return renderPlot(canvas);
    }
}
