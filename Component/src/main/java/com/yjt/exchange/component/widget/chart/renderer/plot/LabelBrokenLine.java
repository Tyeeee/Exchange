package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Color;
import android.graphics.Paint;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.LabelLinePoint;

public class LabelBrokenLine {

    private LabelLinePoint labelLinePoint = LabelLinePoint.ALL;
    private float radius = 5.f;
    //当标签为Line类型时使用
    private Paint labelLinePaint = null;
    //画点 
    private Paint paint = null;
    //标签与点的转折线长度
    private float labelBrokenLineLength = 30.f;
    //BEZIERCURVE 贝塞尔曲线  
    protected boolean isBZLine = false;
    //折线起始点(1 - 10)
    protected float brokenStartPoint = 3.f;

    public LabelBrokenLine() {
    }

    /**
     * 设置显示线条为贝塞尔曲线
     */
    public void isBZLine() {
        isBZLine = true;
    }

    /**
     * 设置显示线条为普通直线
     */
    public void isBeeLine() {
        isBZLine = false;
    }

    /**
     * 设置在线上点的风格
     *
     * @param style 风格
     */
    public void setLinePointStyle(LabelLinePoint style) {
        labelLinePoint = style;
    }

    /**
     * 返回当前线上点的风格
     *
     * @return 风格
     */
    public LabelLinePoint getLinePointStyle() {
        return labelLinePoint;
    }

    /**
     * 设置点的半径
     *
     * @param radius 半径
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * 返回点的半径
     *
     * @return 半径
     */
    public float getRadius() {
        return radius;
    }

    /**
     * 设置折线长度
     *
     * @param len 长度
     */
    public void setBrokenLine(float len) {
        labelBrokenLineLength = len;
    }

    /**
     * 返回折线长度
     *
     * @return 长度
     */
    public float getBrokenLine() {
        return labelBrokenLineLength;
    }


    /**
     * 开放标签线画笔(当标签为Line类型时有效)
     *
     * @return 画笔
     */
    public Paint getLabelLinePaint() {
        if (null == labelLinePaint) {
            labelLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            labelLinePaint.setColor(Color.BLACK);
            labelLinePaint.setStrokeWidth(2);
        }
        return labelLinePaint;
    }

    /**
     * 返回折线点画笔
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
     * 折线转折起始点(1 - 10)比例
     *
     * @param ratio 比例(1-10)
     */
    public void setBrokenStartPoint(float ratio) {
        if (Float.compare(ratio, 1) == -1 || Float.compare(ratio, 10) == 1) {
             LogUtil.Companion.getInstance().print("值必须在1到10范围内.");
            return;
        } else {
            brokenStartPoint = ratio;
        }
    }
}
