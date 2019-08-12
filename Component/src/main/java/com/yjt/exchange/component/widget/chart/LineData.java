package com.hynet.heebit.components.widget.chart;

import com.hynet.heebit.components.widget.chart.constant.DotStyle;

import java.util.List;

public class LineData extends LnData {

    //线上每个点的值
    private List<Double> doubles;
    //标签文字旋转角度
    private float labelRotateAngle = 0.0f;

    public LineData() {
    }

    /**
     * 构成一条完整的线条
     *
     * @param key        键值
     * @param color      线条颜色
     * @param dataSeries 对应的数据集
     */
    public LineData(String key, List<Double> dataSeries, int color) {
        setLabel(key);
        setLineKey(key);
        setDataSet(dataSeries);
        setLineColor(color);
    }

    /**
     * @param key        key值
     * @param dataSeries 数据序列
     * @param color      线颜色
     * @param dotStyle   坐标点绘制类型
     */
    public LineData(String key, List<Double> dataSeries, int color, DotStyle dotStyle) {
        setLabel(key);
        setLineKey(key);
        setLineColor(color);
        setDataSet(dataSeries);
        setDotStyle(dotStyle);
    }

    /**
     * 设置绘制线的数据序列
     *
     * @param dataSeries 数据序列
     */
    public void setDataSet(List<Double> dataSeries) {
        doubles = dataSeries;
    }

    /**
     * 返回绘制线的数据序列
     *
     * @return 绘制线的数据序列
     */
    public List<Double> getLinePoint() {
        return doubles;
    }

    /**
     * 返回标签在显示时的旋转角度
     *
     * @return 旋转角度
     */
    public float getItemLabelRotateAngle() {
        return labelRotateAngle;
    }

    /**
     * 设置标签在显示时的旋转角度
     *
     * @param rotateAngle 旋转角度
     */
    public void setItemLabelRotateAngle(float rotateAngle) {
        this.labelRotateAngle = rotateAngle;
    }
}
