package com.hynet.heebit.components.widget.chart.event.click;

public abstract class PositionRecord {

    protected int dataID = -1;
    protected int dataChildID = -1;

    public PositionRecord() {

    }

    //确认是否在范围内
    protected abstract boolean compareRange(float x, float y);

    //当前记录在数据源中行号
    public int getDataID() {
        return dataID;
    }

    //当前记录所属数据集的行号
    public int getDataChildID() {
        return dataChildID;
    }

    public int getRecordID() {
        if (-1 == dataID && -1 == dataChildID) return -1;
        int id = 0;
        if (dataID > 0) id += dataChildID;
        if (dataChildID > 0) id += dataChildID;
        return id;
    }

    //当前记录在数据源中行号
    protected void saveDataID(int num) {
        dataID = num;
    }

    //当前记录所属数据集的行号
    protected void saveDataChildID(int num) {
        dataChildID = num;
    }

}
