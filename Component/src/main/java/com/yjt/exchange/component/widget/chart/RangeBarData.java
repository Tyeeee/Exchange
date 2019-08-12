package com.hynet.heebit.components.widget.chart;

public class RangeBarData {

    private double max = 0d;
    private double min = 0d;
    private double mX = 0d;

    public RangeBarData() {}

    public RangeBarData(double x, double min, double max) {
        setX(x);
        setMax(max);
        setMin(min);
    }

    /**
     * 最大值
     *
     * @param max 最大值
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * 最小值
     *
     * @param min 最小值
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * 返回最大值
     *
     * @return 最大值
     */
    public double getMax() {
        return max;
    }

    /**
     * 返回最小值
     *
     * @return 最小值
     */
    public double getMin() {
        return min;
    }

    /**
     * 返回X值
     *
     * @return X值
     */
    public void setX(double x) {
        mX = x;
    }

    /**
     * 返回X值
     *
     * @return X值
     */
    public double getX() {
        return mX;
    }

}
