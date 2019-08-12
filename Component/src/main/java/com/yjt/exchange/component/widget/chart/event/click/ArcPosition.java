package com.hynet.heebit.components.widget.chart.event.click;

import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class ArcPosition extends PositionRecord {

    protected float offsetAngle = 0.0f;
    protected float currentAngle = 0.0f;
    protected float radius = 0.0f;
    protected float selectedOffset = 0.0f;
    //初始偏移角度
    protected float initializeAngle = 0.0f;//180;
    protected PointF circleXY = null;

    public ArcPosition() {
    }

    public float getAngle() {
        return MathUtil.getInstance().add(offsetAngle, currentAngle);
        //return offsetAngle;
    }

    /**
     * 饼图(pie chart)起始偏移角度
     *
     * @param Angle 偏移角度
     */
    public void saveInitialAngle(float Angle) {
        initializeAngle = Angle;
    }


    public float getRadius() {
        return radius;
    }

    public PointF getPointF() {
        return circleXY;
    }

    public float getStartAngle() {
        return MathUtil.getInstance().add(offsetAngle, this.initializeAngle);
    }

    public float getSweepAngle() {
        return currentAngle;
    }

    public float getSelectedOffset() {
        return selectedOffset;
    }

    @Override
    protected boolean compareRange(float x, float y) {
        if (null == circleXY) return false;
        return compareRadius(x, y);
    }

    private boolean compareRadius(float x, float y) {
        double distance = MathUtil.getInstance().getDistance(circleXY.x, circleXY.y, x, y);
        if (Double.compare(distance, radius) == 0 || Double.compare(distance, radius) == -1) {
            float Angle1 = (float) MathUtil.getInstance().getDegree(circleXY.x, circleXY.y, x, y);
            float currAngle = getAngle();
            //??? 如果有设初始角度(initializeAngle)，则初始角度范围内的那个扇区点击判断会有问题，原因还没查出来
            if (Float.compare(currAngle, Angle1) == 1 || Float.compare(currAngle, Angle1) == 0) {
                return true;
            }
        }
        return false;
    }


}
