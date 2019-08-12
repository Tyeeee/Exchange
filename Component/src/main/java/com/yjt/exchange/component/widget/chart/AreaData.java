package com.hynet.heebit.components.widget.chart;

import android.graphics.Color;
import android.graphics.Shader;

import com.hynet.heebit.components.widget.chart.constant.Direction;
import com.hynet.heebit.components.widget.chart.constant.DotStyle;

import java.util.List;

public class AreaData extends LineData {

    private int areaFillColor = -999; // 255;
    //是否应用渲染模式
    private boolean applayGradient = false;
    private int areaBeginColor = Color.WHITE;
    private int areaEndColor = Color.WHITE;
    private Shader.TileMode tileMode = Shader.TileMode.MIRROR;
    private Direction direction = Direction.VERTICAL;

    public AreaData() {
        super();
    }

    /**
     *
     * @param key        key值
     * @param linecolor 线颜色
     * @param dataSeries    数据序列
     */
    public AreaData(String key, int linecolor, List<Double> dataSeries) {
        super();
    }

    /**
     *
     * @param key        key值
     * @param dataSeries    数据序列
     * @param lineColor    线颜色
     * @param areaColor    区域填充颜色
     */
    public AreaData(String key, List<Double> dataSeries, int lineColor, int areaColor) {
        setLabel(key);
        setDataSet(dataSeries);
        setLineColor(lineColor);
        setAreaFillColor(areaColor);
        setAreaBeginColor(areaColor);
        setAreaEndColor(Color.WHITE);

    }

    public AreaData(String key, List<Double> dataSeries, int lineColor,                    int areaBeginColor, int areaEndColor) {
        setLabel(key);
        setDataSet(dataSeries);
        setLineColor(lineColor);
        setAreaFillColor(areaBeginColor);
        setApplayGradient(true);
        setAreaBeginColor(areaBeginColor);
        setAreaEndColor(areaEndColor);
    }


    /**
     *
     * @param key        key值
     * @param dataSeries    数据序列
     * @param color        线颜色
     * @param dotStyle    坐标点绘制类型
     */
    public AreaData(String key, List<Double> dataSeries, int color, DotStyle dotStyle) {
        setLabel(key);
        setLineColor(color);
        setDataSet(dataSeries);
        setDotStyle(dotStyle);
        setAreaFillColor(color);
        setAreaBeginColor(color);
        setAreaEndColor(Color.WHITE);
    }


    /**
     * 设置区域填充颜色
     * @param color 区域填充颜色
     */
    public void setAreaFillColor(int color) {
        areaFillColor = color;
    }

    /**
     * 返回区域填充颜色
     * @return 区域填充颜色
     */
    public int getAreaFillColor() {
        return areaFillColor;
    }

    /**
     * 设置 是否应用渲染模式
     */
    public void setApplayGradient(boolean status) {
        applayGradient = status;
    }

    /**
     * 返回是否应用渲染模式
     * @return 状态
     */
    public boolean getApplayGradient() {
        return applayGradient;
    }

    /**
     * 设置渐变渲染方向	
     * @param direction 方向
     */
    public void setGradientDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * 返回渐变渲染方向	
     * @return 方向
     */
    public Direction getGradientDirection() {
        return direction;
    }

    /**
     * 设置渲染模式
     * @param tm    渲染模式
     */
    public void setGradientMode(Shader.TileMode tm) {
        tileMode = tm;
    }

    /**
     * 返回渲染模式
     * @return 渲染模式
     */
    public Shader.TileMode getGradientMode() {
        return tileMode;
    }

    /**
     * 设置起始颜色
     * @param color    颜色
     */
    public void setAreaBeginColor(int color) {
        areaBeginColor = color;
    }

    /**
     * 设置结束颜色
     * @param color    颜色
     */
    public void setAreaEndColor(int color) {
        areaEndColor = color;
    }


    /**
     * 返回起始颜色
     * @return 颜色
     */
    public int getAreaBeginColor() {
        return areaBeginColor;
    }

    /**
     * 返回结束颜色
     * @return 颜色
     */
    public int getAreaEndColor() {
        return areaEndColor;
    }

}
