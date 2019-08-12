package com.hynet.heebit.components.widget.chart.event.click.listener;

import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.event.click.PointPosition;

public interface ChartPointListener {

    public void onClick(PointF point, PointPosition pointPosition);

}
