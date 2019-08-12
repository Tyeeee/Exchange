package com.hynet.heebit.components.widget.chart.event.click;

import android.graphics.PointF;

public class PlotArcPosition extends ArcPosition {

    public PlotArcPosition() {
    }

    public void saveAngle(float radius, float offsetAngle, float currentAngle, float selectedOffset) {
        this.radius = radius;
        this.offsetAngle = offsetAngle;
        this.currentAngle = currentAngle;
        this.selectedOffset = selectedOffset;
    }

    //当前记录在数据源中行号
    public void savePlotDataID(int num) {
        saveDataID(num);
    }

    //当前记录所属数据集的行号
    public void savePlotDataChildID(int num) {
        saveDataChildID(num);
    }


    public void savePlotCirXY(float x, float y) {
        if (null == circleXY)
            circleXY = new PointF();
        circleXY.x = x;
        circleXY.y = y;
    }

    public boolean compareF(float x, float y) {
        return compareRange(x, y);
    }

}
