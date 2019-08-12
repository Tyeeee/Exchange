package com.hynet.heebit.components.widget.chart.renderer.info;

public class PlotDataInfo {

    //坐标
    public float X = 0.0f;
    public float Y = 0.0f;
    //标签
    public String label;
    //将当前为第几个tick传递轴，用以区分是否为主明tick
    public int ID = -1;
    public float labelX = 0.0f;
    public float labelY = 0.0f;

    public PlotDataInfo() {}

    public PlotDataInfo(float x, float y, String label) {
        this.X = x;
        this.Y = y;
        this.label = label;
        this.labelX = x;
        this.labelY = y;
    }

    public PlotDataInfo(int id, float x, float y, String label) {
        this.ID = id;
        this.X = x;
        this.Y = y;
        this.label = label;
        this.labelX = x;
        this.labelY = y;
    }

    public PlotDataInfo(int id, float x, float y, String label, float lx, float ly) {
        this.ID = id;
        this.X = x;
        this.Y = y;
        this.label = label;
        this.labelX = lx;
        this.labelY = ly;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

}
