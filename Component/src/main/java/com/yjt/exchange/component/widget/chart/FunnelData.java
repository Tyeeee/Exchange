package com.hynet.heebit.components.widget.chart;

public class FunnelData implements Comparable<FunnelData> {

    private String label;
    private float data;
    private int color;
    //透明度
    private int alpha = -1;

    public FunnelData() {

    }

    public FunnelData(String label, float percent, int color) {
        setLabel(label);
        setData(percent);
        setColor(color);
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getAlpha() {
        return alpha;
    }

    /**
     * 设置数据源
     *
     * @param percent 百分比
     */
    public void setData(float percent) {
        data = percent;
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
     * 返回数据集合序列
     *
     * @return 集合序列
     */
    public float getData() { //List<float>
        return data;
    }

    @Override
    public int compareTo(FunnelData arg0) {
        return Float.compare(this.getData(), arg0.getData());
    }

}
