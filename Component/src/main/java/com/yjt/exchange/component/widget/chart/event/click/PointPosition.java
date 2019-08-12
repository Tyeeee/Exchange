package com.hynet.heebit.components.widget.chart.event.click;

import android.graphics.PointF;

public class PointPosition extends RectPosition {

    protected PointF pointF = null;

    public PointPosition() {
    }

    public PointF getPosition() {
        return pointF;
    }

    public String getPointInfo() {
        if (null == pointF) return "";
        return "x:" + Float.toString(pointF.x) + " y:" + Float.toString(pointF.y);
    }

}
