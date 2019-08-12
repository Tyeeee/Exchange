package com.hynet.heebit.components.widget.chart.renderer.axis;

public class DataAxis extends XYAxis {

    //轴数据来源
    private double dataAxisMin = 0d;
    private double dataAxisMax = 0d;
    private double dataAxisSteps = 0d;
    private double detailModeSteps = 0d;
    //是否显示第一个序号的标签文本
    protected boolean showFirstTick = true;
    private float dataAxisStd = 0.0f;
    private boolean axisStdStatus = false;

    public DataAxis() {
    }

    /**
     * 激活正负标准值处理，激活后，数据与标准值比较后，依大小向各自方向绘制
     */
    public void enabledAxisStd() {
        axisStdStatus = true;
    }

    /**
     * 禁掉正负标准值处理
     */
    public void disableddAxisStd() {
        axisStdStatus = false;
    }

    /**
     * 设置具体的标准值
     *
     * @param std 标准值
     */
    public void setAxisStd(float std) {
        dataAxisStd = std;
    }

    /**
     * 返回正负标准值处理状态
     *
     * @return 状态
     */
    public boolean getAxisStdStatus() {
        return axisStdStatus;
    }

    /**
     * 返回当前正负标准值，如没设则默认为轴的最小值
     *
     * @return 标准值
     */
    public float getAxisStd() {
        if (axisStdStatus) {
            return dataAxisStd;
        } else {
            return (float) dataAxisMin;
        }
    }

    /**
     * 设置数据轴最小值,默认为0
     *
     * @param min 最小值
     */
    public void setAxisMin(double min) {
        dataAxisMin = min;
    }

    /**
     * 设置数据轴最大值
     *
     * @param max 最大值
     */
    public void setAxisMax(double max) {
        dataAxisMax = max;
    }

    /**
     * 设置数据轴步长
     *
     * @param steps 步长
     */
    public void setAxisSteps(double steps) {
        dataAxisSteps = steps;
    }


    /**
     * 设置后，会启用为明细模式，轴刻度线会分长短,背景线会分粗细
     *
     * @param steps 步长
     */
    public void setDetailModeSteps(double steps) {
        detailModeSteps = steps;
    }


    /**
     * 返回数据轴最小值
     *
     * @return 最小值
     */
    public float getAxisMin() {
        return (float) dataAxisMin;
    }

    /**
     * 返回数据轴最大值
     *
     * @return 最大值
     */
    public float getAxisMax() {
        return (float) dataAxisMax;
    }

    /**
     * 返回数据轴步长
     *
     * @return 步长
     */
    public double getAxisSteps() {
        return dataAxisSteps;
    }

    /**
     * 返回区分刻度线明细的步长
     *
     * @return 步长
     */
    public double getDetailModeSteps() {
        return detailModeSteps;
    }

    /**
     * 返回是否启用明细模式
     *
     * @return 是否有启用
     */
    public boolean isDetailMode() {
        return (Double.compare(detailModeSteps, 0d) != 0);
    }

    /**
     * 显示数据轴第一个序号的值，默认显示
     */
    public void showFirstTick() {
        showFirstTick = true;
    }

    /**
     * 不显示数据轴第一个序号的值，默认显示
     */
    public void hideFirstTick() {
        showFirstTick = false;
    }

}
