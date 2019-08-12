package com.hynet.heebit.components.widget.chart.event.click.listener;

import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.event.click.BarPosition;

public interface ChartBarListener {

    public void onClick(PointF point, BarPosition barPosition);

}
