package com.hynet.heebit.components.widget.chart.event.click.listener;

import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.event.click.ArcPosition;

public interface ChartArcListener {
    
    public void onClick(PointF point, ArcPosition arcRecord);

}
