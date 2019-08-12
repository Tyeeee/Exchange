package com.hynet.heebit.components.widget.chart.renderer.axis;


import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.widget.chart.constant.OddEven;

import java.util.List;

public class CategoryAxisRender extends CategoryAxis {


    public CategoryAxisRender() {
        getTickLabelPaint().setTextAlign(Align.CENTER);
    }

    /**
     * 返回数据源
     *
     * @return 数据源
     */
    public List<String> getDataSet() {
        return this.data;
    }

    /**
     * 绘制横向刻度标识
     *
     * @param chatLeft
     * @param plotLeft
     * @param canvas        画布
     * @param centerX       点X坐标
     * @param centerY       点Y坐标
     * @param text          内容
     * @param labelX
     * @param labelY
     * @param isTickVisible
     */
    public void renderAxisHorizontalTick(float chatLeft, float plotLeft, Canvas canvas, float centerX, float centerY, String text, float labelX, float labelY, boolean isTickVisible) {
        renderHorizontalTick(chatLeft, plotLeft, canvas, centerX, centerY, text, labelX, labelY, isTickVisible);
    }

    /**
     * 绘制竖向刻度标识
     *
     * @param centerX 点X坐标
     * @param centerY 点Y坐标
     * @param text    内容
     */
    public void renderAxisVerticalTick(Canvas canvas, float centerX, float centerY, String text, float labelX, float labelY, boolean isTickVisible, OddEven oddEven) {
        renderVerticalTick(canvas, centerX, centerY, text, labelX, labelY, isTickVisible, oddEven);
    }

    /**
     * 绘制轴线
     *
     * @param startX 起始点X坐标
     * @param startY 起始点Y坐标
     * @param stopX  终止点X坐标
     * @param stopY  终止点Y坐标
     */
    public void renderAxis(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        if (isShow() && isShowAxisLine()) {
            drawAxisLine(canvas, startX, startY, stopX, stopY);
        }
    }

    public void renderAxisLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        drawAxisLine(canvas, startX, startY, stopX, stopY);
    }


    /**
     * 设置分类轴数据源
     *
     * @param data 数据源
     */
    public void setDataBuilding(List<String> data) {
        this.data = data;
    }

}
