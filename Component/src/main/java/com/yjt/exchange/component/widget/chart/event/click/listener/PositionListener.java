package com.hynet.heebit.components.widget.chart.event.click.listener;

import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.event.click.PositionRecord;

public interface PositionListener {

    public void onClick(PointF point, PositionRecord positionRecord);

}
