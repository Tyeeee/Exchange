package com.hynet.heebit.components.widget.chart;

import android.graphics.Paint;

import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;

import java.util.List;

public class ScatterData {

    //标签轴用的到值
    private String label;
    //是否在点上显示标签
    private boolean labelVisible = false;
    //线上每个点的值
    private List<PointD> pointDS;
    //标签画笔
    private Paint labelPaint = null;
    //点画笔
    private PlotDot plotDot = null;
    //标签文字旋转角度
    private float labelRotateAngle = 0.0f;

    public ScatterData() {}

    /**
     * 构成一条完整的线条
     *
     * @param key        对应的键值
     * @param dataSeries 对应的数据序列
     * @param color      线条颜色
     */
    public ScatterData(String key, List<PointD> dataSeries, int color, DotStyle dotStyle) {
        setKey(key);
        setDataSet(dataSeries);
        getPlotDot().setColor(color);
        getPlotDot().setDotStyle(dotStyle);
    }

    /**
     * 设置绘制线的数据序列,由x与y坐标构建
     *
     * @param dataSeries <X坐标值，Y坐标值>
     */
    public void setDataSet(List<PointD> dataSeries) {
        pointDS = dataSeries;
    }

    /**
     * 返回绘制线的数据序列
     *
     * @return 线的数据序列<X坐标值       ，       Y坐标值>
     */
    public List<PointD> getDataSet() {
        return pointDS;
    }

    /**
     * 设置是否在线上显示标签
     *
     * @param visible 是否显示
     */
    public void setLabelVisible(boolean visible) {
        labelVisible = visible;
    }

    /**
     * 返回是否在线上显示标签
     *
     * @return 是否显示
     */
    public boolean getLabelVisible() {
        return labelVisible;
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
     * 设置当前记录的Key值
     *
     * @param value Key值
     */
    public void setKey(String value) {
        label = value;
    }

    /**
     * 返回Key值
     *
     * @return Key值
     */
    public String getKey() {
        return label;
    }

    /**
     * 开放标签画笔
     *
     * @return 画笔
     */
    public Paint getDotLabelPaint() {
        if (null == labelPaint)
            labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        return labelPaint;
    }

    /**
     * 点的绘制类
     */
    public PlotDot getPlotDot() {
        if (null == plotDot) {
            plotDot = new PlotDot();
            plotDot.setDotStyle(DotStyle.DOT);
        }
        return plotDot;
    }


    /**
     * 设置点的显示风格
     *
     * @param style 显示风格
     */
    public void setDotStyle(DotStyle style) {
        getPlotDot().setDotStyle(style);
    }

    /**
     * 返回点的显示风格
     *
     * @return 显示风格
     */
    public DotStyle getDotStyle() {
        return getPlotDot().getDotStyle();
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
