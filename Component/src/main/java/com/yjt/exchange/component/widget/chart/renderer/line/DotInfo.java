package com.hynet.heebit.components.widget.chart.renderer.line;

public class DotInfo {

    public Double value = 0d;
    public Double xValue = 0d;
    public Double yValue = 0d;
    public float x = 0.f;
    public float y = 0.f;

    public DotInfo() {}

    public DotInfo(Double value, float x, float y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public DotInfo(Double xValue, Double yValue, float x, float y) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.x = x;
        this.y = y;
    }

    public String getLabel() {
        return Double.toString(xValue) + "," + Double.toString(yValue);
    }

}
