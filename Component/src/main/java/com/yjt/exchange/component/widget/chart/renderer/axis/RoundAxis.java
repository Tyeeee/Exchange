package com.hynet.heebit.components.widget.chart.renderer.axis;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.constant.RoundAxisType;
import com.hynet.heebit.components.widget.chart.constant.RoundTickAxisType;
import com.hynet.heebit.components.widget.chart.utils.IFormatterTextCallBack;

import java.util.List;

public class RoundAxis extends Axis {

    protected float circleX = 0.0f;
    protected float circleY = 0.0f;
    protected float orgRadius = 0.0f;
    protected float radius = 0.0f;
    protected int detailModeSteps = 1;
    private float radiusPercentage = 1f;
    private float innerRadiusPercentage = 0.9f;
    // 用于格式化标签的回调接口
    private IFormatterTextCallBack iFormatterTextCallBack;
    protected float totalAngle = 0.0f;
    protected float initializeAngle = 0.0f;
    private RoundAxisType roundAxisType = RoundAxisType.ARCLINEAXIS;
    protected List<Float> percentage = null;
    protected List<Integer> color = null;
    protected List<String> labels = null;
    //ringaxis/fillaxis
    private Paint fillAxisPaint = null;
    protected boolean longTickfakeBold = true;
    protected RoundTickAxisType roundTickAxisType = RoundTickAxisType.INNER_TICKAXIS;

    public RoundAxis() {
    }

    /**
     * 设置轴类型,不同类型有不同的显示风格
     *
     * @param axisType 轴类型
     */
    public void setRoundAxisType(RoundAxisType axisType) {
        roundAxisType = axisType;
        // TICKAXIS,RINGAXIS,LENAXIS
        switch (roundAxisType) {
            case TICKAXIS:
                getTickLabelPaint().setTextAlign(Align.CENTER);
                showAxisLabels();
                showTickMarks();
                showAxisLine();
                this.getAxisPaint().setStyle(Style.STROKE);
                break;
            case RINGAXIS:
                showAxisLabels();
                hideTickMarks();
                this.getAxisPaint().setStyle(Style.FILL);
                this.getAxisPaint().setColor(Color.BLUE);
                initializeFillAxisPaint();
                break;
            case ARCLINEAXIS:
                hideAxisLabels();
                hideTickMarks();
                getAxisPaint().setStyle(Style.STROKE);
            case CIRCLEAXIS:
                //getAxisPaint().setStyle(Style.FILL);
                hideAxisLabels();
                hideTickMarks();
                break;
            default:
                break;
        }
    }


    /**
     * 设置后，会启用为明细模式，轴刻度线会分长短,背景线会分粗细
     *
     * @param steps 步长
     */
    public void setDetailModeSteps(int steps) {
        detailModeSteps = steps;
    }


    public void setDetailModeSteps(int steps,                                   boolean isLongTickfakeBold) {
        detailModeSteps = steps;
        longTickfakeBold = isLongTickfakeBold;
    }

    public float getRadius() {
        return radius;
    }

    public void setRoundTickAxisType(RoundTickAxisType type) {
        roundTickAxisType = type;
    }

    /**
     * 绘制半径比例
     *
     * @param percentage 占总半径的比例
     */
    public void setRadiusPercentage(float percentage) {
        radiusPercentage = percentage;
    }

    //ringaxis

    /**
     * 绘制内半径比例
     *
     * @param percentage 占总半径的比例
     */
    public void setRingInnerRadiusPercentage(float percentage) {
        innerRadiusPercentage = percentage;
    }

    /**
     * 内部填充画笔
     *
     * @return 画笔
     */
    public Paint getFillAxisPaint() {
        initializeFillAxisPaint();
        return fillAxisPaint;
    }

    private void initializeFillAxisPaint() {
        if (null == fillAxisPaint) {
            fillAxisPaint = new Paint();
            fillAxisPaint.setStyle(Style.FILL);
            fillAxisPaint.setColor(Color.WHITE);
            fillAxisPaint.setAntiAlias(true);
        }
    }


    /**
     * 设置标签的显示格式
     *
     * @param iFormatterTextCallBack 回调函数
     */
    public void setLabelFormatter(IFormatterTextCallBack iFormatterTextCallBack) {
        this.iFormatterTextCallBack = iFormatterTextCallBack;
    }

    /**
     * 返回标签显示格式
     *
     * @param text 传入当前值
     *
     * @return 显示格式
     */
    protected String getFormatterLabel(String text) {
        String itemLabel;
        try {
            itemLabel = iFormatterTextCallBack.textFormatter(text);
        } catch (Exception ex) {
            itemLabel = text;
        }
        return itemLabel;
    }


    /**
     * 返回轴类型
     *
     * @return 轴类型
     */
    public RoundAxisType getAxisType() {
        return roundAxisType;
    }


    /**
     * 外环显示在哪个比例位置
     *
     * @return 比例
     */
    public float getOuterRadiusPercentage() {
        return radiusPercentage;
    }

    /**
     * 内环显示在哪个比例位置
     *
     * @return 比例
     */
    public float getRingInnerRadiusPercentage() {
        return innerRadiusPercentage;
    }


    /**
     * 外环半径长度
     *
     * @return 半径长度
     */
    public float getOuterRadius() {
        return orgRadius * radiusPercentage;
    }

    /**
     * 内环半径长度
     *
     * @return 半径长度
     */
    public float getRingInnerRadius() {
        return orgRadius * innerRadiusPercentage;
    }

    /**
     * 圆心位置
     *
     * @return 圆心
     */
    public PointF getCenterXY() {
        return (new PointF(circleX, circleY));
    }

}
