package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.hynet.heebit.components.widget.chart.constant.PointerStyle;

public class Pointer {

    private Paint paint = null;
    private Paint baseCirclePaint = null;
    protected float centerX = 0.0f;
    protected float centerY = 0.0f;
    protected float percentage = 0.0f;
    protected float pointerRadiusPercentage = 0.9f;
    protected float pointerTailRadiusPercentage = 0.0f;
    protected float baseRadius = 20f;
    private PointerStyle pointerStyle = PointerStyle.LINE;
    private boolean showBaseCircle = true;

    public Pointer() {

    }

    /**
     * 设置指针显示风格
     *
     * @param style 显示风格
     */
    public void setPointerStyle(PointerStyle style) {
        pointerStyle = style;
    }

    /**
     * 返回指针显示风格
     *
     * @return 显示风格
     */
    public PointerStyle getPointerStyle() {
        return pointerStyle;
    }

    /**
     * 设置指针长度
     *
     * @param radiusPercentage 占总半径的比例
     */
    public void setLength(float radiusPercentage) {
        setLength(radiusPercentage, 0);
    }

    /**
     * 设置指针长度
     *
     * @param radiusPercentage     占总半径的比例
     * @param tailRadiusPercentage 尾部延长占总半径的比例
     */
    public void setLength(float radiusPercentage, float tailRadiusPercentage) {
        pointerRadiusPercentage = radiusPercentage;
        pointerTailRadiusPercentage = tailRadiusPercentage;
    }


    /**
     * 开放指针画笔
     *
     * @return 指针画笔
     */
    public Paint getPointerPaint() {
        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.rgb(235, 138, 61));
            paint.setStrokeWidth(3);
            paint.setStyle(Style.FILL);
            paint.setAntiAlias(true);
        }
        return paint;
    }

    /**
     * 开放指针底部圆画笔
     *
     * @return 底部圆画笔
     */
    public Paint getBaseCirclePaint() {
        if (null == baseCirclePaint) {
            baseCirclePaint = new Paint();
            baseCirclePaint.setStyle(Style.FILL);
            baseCirclePaint.setAntiAlias(true);
            baseCirclePaint.setColor(Color.rgb(235, 138, 61));
            baseCirclePaint.setStrokeWidth(8);
        }
        return baseCirclePaint;
    }

    /**
     * 开放指针底部半径
     *
     * @param radius 半径
     */
    public void setBaseRadius(float radius) {
        baseRadius = radius;
    }

    /**
     * 返回指针底部半径
     *
     * @return 半径
     */
    public float getBaseRadius() {
        return baseRadius;
    }

    /**
     * 不绘制底部圆
     */
    public void hideBaseCircle() {
        showBaseCircle = false;
    }

    /**
     * 绘制底部圆
     */
    public void showBaseCircle() {
        showBaseCircle = true;
    }

    /**
     * 是否绘制底部圆
     *
     * @return 是否绘制
     */
    public boolean isShowBaseCircle() {
        return showBaseCircle;
    }


    /**
     * 设置指针指向的值，即当前比例(0 - 1)
     *
     * @param percentage 当前比例
     */
    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    /**
     * 返回当前指针指向的比例
     *
     * @return 比例
     */
    public float getPercentage() {
        return percentage;
    }

}
