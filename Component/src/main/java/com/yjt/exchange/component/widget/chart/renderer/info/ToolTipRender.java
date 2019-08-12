package com.hynet.heebit.components.widget.chart.renderer.info;

import android.graphics.Canvas;

public class ToolTipRender extends ToolTip {

    public ToolTipRender() {

    }

    public void renderInfo(Canvas canvas) {
        drawInfo(canvas);
        clear();
    }

}
