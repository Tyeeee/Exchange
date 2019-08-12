package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Canvas;

public class PlotQuadrantRender extends PlotQuadrant {

    public PlotQuadrantRender() {}

    public void drawQuadrant(Canvas canvas, float centerX, float centerY, float left, float top, float right, float bottom) {
        if (showBackgroundColor) {    //绘制background
            getBgColorPaint().setColor(firstColor);
            canvas.drawRect(centerX, top, right, centerY, getBgColorPaint());
            getBgColorPaint().setColor(secondColor);
            canvas.drawRect(centerX, centerY, right, bottom, getBgColorPaint());
            getBgColorPaint().setColor(thirdColor);
            canvas.drawRect(left, centerY, centerX, bottom, getBgColorPaint());
            getBgColorPaint().setColor(fourthColor);
            canvas.drawRect(left, top, centerX, centerY, getBgColorPaint());
        }
        if (showVerticalLine) {
            canvas.drawLine(centerX, top, centerX, bottom, getVerticalLinePaint());
        }
        if (showHorizontalLine) {
            canvas.drawLine(left, centerY, right, centerY, getVerticalLinePaint());
        }
    }
}
