package com.hynet.heebit.components.widget.chart;

public class Funnel2Data implements Comparable<Funnel2Data> {

    private String label;
    private float baseData, percentData;
    private int color;
    //透明度
    private int alpha = -1;

    public Funnel2Data() {

    }

    public Funnel2Data(String label, float base, float percent, int color) {
        setLabel(label);
        //setKey(key);
        setBaseData(base);
        setPercentData(percent);
        setColor(color);
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getAlpha() {
        return alpha;
    }


    /**
     * 设置标签
     *
     * @param value 标签内容
     */
    public void setLabel(String value) {
        label = value;
    }

    /**
     * 返回标签
     *
     * @return 标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 设置Key值
     * @param value Key值
     */
    //public void setKey(String value) 
    //{
    //	mKey = value;
    //}

    /**
     * 设置颜色
     *
     * @param value 颜色
     */
    public void setColor(int value) {
        color = value;
    }

    public int getColor() {
        return color;
    }

    /**
     * 设置基数数据源
     *
     * @param data 初始值
     */
    public void setBaseData(float data) {
        baseData = data;
    }

    /**
     * 设置百分比数据源,即基数在最后所占的百分比
     *
     * @param percent 所占百分比(0-1)
     */
    public void setPercentData(float percent) {
        percentData = percent;
    }


    /**
     * 返回基数集合
     *
     * @return 基数
     */
    public float getBaseData() {
        return baseData;
    }

    /**
     * 返回百分比数据
     *
     * @return 所占比数据(0 - 1)
     */
    public float getPercentData() {
        return percentData;
    }


    @Override
    public int compareTo(Funnel2Data arg0) {
        return Float.compare(this.getBaseData(), arg0.getBaseData());
    }

}
