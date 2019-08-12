package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Canvas;
import android.graphics.Path;

import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class PointerRender extends Pointer {

    private static final int FIX_ANGLE = 90;
    private float startAngle = 0.0f;
    private float totalAngle = 0.0f;
    private float pointerAngle = 0.0f;
    private float parentRadius = 0.0f;
    private float pointerRadius = 0.0f;
    private float pointerTailRadius = 0.0f;
    private float endX = 0.0f;
    private float endY = 0.0f;
    private float tailX = 0.0f;
    private float tailY = 0.0f;
    private Path path = null;

    public PointerRender() {
    }

    /**
     * 设置指针的绘制的起始坐标位置
     *
     * @param x
     * @param y
     */
    public void setStartXY(float x, float y) {
        centerX = x;
        centerY = y;
    }

    public void setCurrentAngle(float currentAngle) {
        pointerAngle = currentAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public void setTotalAngle(float totalAngle) {
        this.totalAngle = totalAngle;
    }

    public void setParentRadius(float radius) {
        parentRadius = radius;
    }

    private void calcRadius() {
        if (Float.compare(pointerRadiusPercentage, 0.0f) == 1) {
            pointerRadius = MathUtil.getInstance().mul(parentRadius, pointerRadiusPercentage);
        }

        if (Float.compare(pointerTailRadiusPercentage, 0.0f) == 1) {
            pointerTailRadius = MathUtil.getInstance().mul(parentRadius, pointerTailRadiusPercentage);
        }
    }

    public void setPointEndXY(float x, float y) {
        endX = x;
        endY = y;
    }

    public float getCurrentPointerAngle() {
        pointerAngle = MathUtil.getInstance().mul(totalAngle, percentage);
        return pointerAngle;
    }

    private void calcEndXY() {
        MathUtil.getInstance().calcArcEndPointXY(centerX, centerY, pointerRadius, MathUtil.getInstance().add(getCurrentPointerAngle(), startAngle));
        endX = MathUtil.getInstance().getPosX();
        endY = MathUtil.getInstance().getPosY();
        if (Float.compare(pointerTailRadiusPercentage, 0.0f) == 1) {
            float tailAgent = pointerAngle + startAngle - 180;
            MathUtil.getInstance().calcArcEndPointXY(centerX, centerY, pointerTailRadius, tailAgent);
            tailX = MathUtil.getInstance().getPosX();
            tailY = MathUtil.getInstance().getPosY();
        } else {
            tailX = centerX;
            tailY = centerY;
        }
    }


    public void renerLine(Canvas canvas) {
        canvas.drawLine(centerX, centerY, endX, endY, getPointerPaint());
    }

    public void renderTriangle(Canvas canvas) {
        float currentAgent1 = MathUtil.getInstance().add(pointerAngle - FIX_ANGLE, startAngle);
        float currentAgent2 = MathUtil.getInstance().add(pointerAngle + FIX_ANGLE, startAngle);
        float bX = 0.0f, bY = 0.0f, eX = 0.0f, eY = 0.0f;
        MathUtil.getInstance().calcArcEndPointXY(tailX, tailY, baseRadius, currentAgent1);
        bX = MathUtil.getInstance().getPosX();
        bY = MathUtil.getInstance().getPosY();
        MathUtil.getInstance().calcArcEndPointXY(tailX, tailY, baseRadius, currentAgent2);
        eX = MathUtil.getInstance().getPosX();
        eY = MathUtil.getInstance().getPosY();
        if (null == path) {
            path = new Path();
        } else {
            path.reset();
        }
        path.moveTo(endX, endY);
        path.lineTo(bX, bY);
        path.lineTo(eX, eY);
        path.close();
        canvas.drawPath(path, getPointerPaint());

    }

    public void renderCircle(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, baseRadius, getBaseCirclePaint());
    }

    public void render(Canvas canvas) {
        calcRadius();
        calcEndXY();
        switch (getPointerStyle()) {
            case TRIANGLE:
                renderTriangle(canvas);
                if (isShowBaseCircle()) renderCircle(canvas);
                break;
            case LINE:
                renerLine(canvas);
                if (isShowBaseCircle()) renderCircle(canvas);
                break;
            default:
                break;
        }
    }
}
