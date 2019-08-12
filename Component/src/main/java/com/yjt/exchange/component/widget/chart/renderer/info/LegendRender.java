package com.hynet.heebit.components.widget.chart.renderer.info;

import android.graphics.Canvas;

public class LegendRender extends Legend {

    public LegendRender() {

    }

    public void setPlotWH(float width, float height) {
        setCenterXY(width * xPercentage, height * yPercentage);
    }

    public void renderInfo(Canvas canvas) {
        drawInfo(canvas);
    }

}
