package com.hynet.heebit.components.widget.chart.renderer.info;

import android.graphics.Paint;

import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;

public class Legend extends DynamicBorder {

    protected float xPercentage = 0.0f;
    protected float yPercentage = 0.0f;


    public Legend() {

    }

    /**
     * 设置显示位置
     *
     * @param xPercentage 占绘图区的竖向百分比位置
     * @param yPercentage 占绘图区的横向百分比位置
     */
    public void setPosition(float xPercentage, float yPercentage) {
        this.xPercentage = xPercentage;
        this.yPercentage = yPercentage;
    }

    /**
     * 增加动态图例
     *
     * @param text  文本
     * @param paint 画笔
     */
    public void addLegend(String text, Paint paint) {
        addInfo(text, paint);
    }

    /**
     * 增加动态图例
     *
     * @param dotStyle 图案风格
     * @param text     文本
     * @param paint    画笔
     */
    public void addLegend(PlotDot dotStyle, String text, Paint paint) {
        addInfo(dotStyle, text, paint);
    }


}
