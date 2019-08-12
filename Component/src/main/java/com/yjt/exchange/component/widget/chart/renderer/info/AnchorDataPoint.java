package com.hynet.heebit.components.widget.chart.renderer.info;

import android.graphics.Color;

import com.hynet.heebit.components.widget.chart.constant.AnchorStyle;
import com.hynet.heebit.components.widget.chart.constant.DataAreaStyle;
import com.hynet.heebit.components.widget.chart.constant.LineStyle;

public class AnchorDataPoint {

    private int dataSeriesID = -1;
    private int dataChildID = -1; //Points

    private AnchorStyle anchorStyle = AnchorStyle.RECT;
    private String anchor;
    private int anchorTextSize = 22;
    private int anchorTextColor = Color.BLUE;
    private int backgroundColor = Color.BLACK;
    private int alpha = 100;
    private DataAreaStyle dataAreaStyle = DataAreaStyle.STROKE;
    private float radius = 30.f;
    private float roundRaidus = 15.f;
    private int lineWidth = -1;
    private float capRectW = 20.f;
    private float capRectH = 10.f;
    private float capRectHeight = 30.f;
    //线的风格(点或线之类)
    protected LineStyle mLineStyle = LineStyle.SOLID;

    public AnchorDataPoint() {

    }

    public AnchorDataPoint(int dataSeriesID, int dataChildID, AnchorStyle anchorStyle) {
        this.dataSeriesID = dataSeriesID;
        this.dataChildID = dataChildID;
        this.anchorStyle = anchorStyle;
    }

    public AnchorDataPoint(int dataSeriesID, AnchorStyle anchorStyle) {
        this.dataSeriesID = dataSeriesID;
        this.anchorStyle = anchorStyle;
    }


    public AnchorStyle getAnchorStyle() {
        return anchorStyle;
    }

    public void setAnchorStyle(AnchorStyle style) {
        this.anchorStyle = style;
    }


    public int getDataSeriesID() {
        return dataSeriesID;
    }


    public void setDataSeriesID(int mDataSeriesID) {
        this.dataSeriesID = mDataSeriesID;
    }


    public int getDataChildID() {
        return dataChildID;
    }


    public void setDataChildID(int mDataChildID) {
        this.dataChildID = mDataChildID;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getAnchor() {
        return anchor;
    }


    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setTextSize(int size) {
        anchorTextSize = size;
    }

    public float getTextSize() {
        return anchorTextSize;
    }

    public void setTextColor(int color) {
        anchorTextColor = color;
    }

    public int getTextColor() {
        return anchorTextColor;
    }

    public void setLineWidth(int size) {
        lineWidth = size;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setBgColor(int color) {
        backgroundColor = color;
    }

    public int getBgColor() {
        return backgroundColor;
    }

    public void setAreaStyle(DataAreaStyle style) {
        dataAreaStyle = style;
    }

    public DataAreaStyle getAreaStyle() {
        return dataAreaStyle;
    }

    /**
     * 当风格为CAPRECT时，可用此函数来设置三角形的宽/高
     *
     * @param capWidth  三角形的宽
     * @param capHeight 三角形的高
     */
    public void setCapRectAngleWH(float capWidth, float capHeight) {
        capRectW = capWidth;
        capRectH = capHeight;
    }

    /**
     * 当风格为CAPRECT时，设置Rect的高度
     *
     * @param rectHeight
     */
    public void setCapRectHeight(float rectHeight) {
        capRectHeight = rectHeight;
    }

    public float getCapRectW() {
        return capRectW;
    }

    public float getCapRectH() {
        return capRectH;
    }

    public float getCapRectHeight() {
        return capRectHeight;
    }

    /**
     * 设置角圆弧半径
     *
     * @param radius 半径
     */
    public void setRoundRadius(int radius) {
        roundRaidus = radius;
    }

    /**
     * 返回角圆弧半径
     *
     * @return 半径
     */
    public float getRoundRadius() {
        return roundRaidus;
    }

    /**
     * 设置线的风格(点或线之类)
     *
     * @param style 线的风格
     */
    public void setLineStyle(LineStyle style) {
        mLineStyle = style;
    }

    /**
     * 返回线的风格
     *
     * @return 线的风格
     */
    public LineStyle getLineStyle() {
        return mLineStyle;
    }

}
