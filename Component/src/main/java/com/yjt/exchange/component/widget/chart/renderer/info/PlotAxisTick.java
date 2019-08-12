package com.hynet.heebit.components.widget.chart.renderer.info;

public class PlotAxisTick extends PlotDataInfo {

    private boolean showTickMarks = true;

    public PlotAxisTick() {}


    public PlotAxisTick(float x, float y, String label) {
        X = x;
        Y = y;
        this.label = label;
        labelX = x;
        labelY = y;
    }

    public PlotAxisTick(int id, float x, float y, String label) {
        ID = id;
        X = x;
        Y = y;
        this.label = label;

        labelX = x;
        labelY = y;
    }

    public PlotAxisTick(float x, float y, String label, float lx, float ly) {
        // ID = id;
        X = x;
        Y = y;
        this.label = label;

        labelX = lx;
        labelY = ly;
    }

    public PlotAxisTick(float x, float y, String label, float lx, float ly, boolean tickMarks) {
        // ID = id;
        X = x;
        Y = y;
        this.label = label;

        labelX = lx;
        labelY = ly;

        showTickMarks = tickMarks;
    }


    public float getLabelX() {
        return labelX;
    }

    public void setLabelX(float x) {
        labelX = x;
    }

    public float getLabelY() {
        return labelY;
    }

    public void setLabelY(float y) {
        labelY = y;
    }

    public boolean isShowTickMarks() {
        return showTickMarks;
    }

}
