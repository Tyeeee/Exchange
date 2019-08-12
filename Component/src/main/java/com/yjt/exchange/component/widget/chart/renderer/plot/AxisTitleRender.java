package com.hynet.heebit.components.widget.chart.renderer.plot;


import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.text.TextUtils;

import com.hynet.heebit.components.widget.chart.constant.AxisTitleStyle;
import com.hynet.heebit.components.widget.chart.renderer.IRender;
import com.hynet.heebit.components.widget.chart.renderer.XChart;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class AxisTitleRender extends AxisTitle implements IRender {

    private XChart xChart = null;

    public AxisTitleRender() {

    }

    /**
     * 传入chart给轴标题类
     *
     * @param chart 图基类
     */
    public void setRange(XChart chart) {
        xChart = chart;
    }

    @Override
    public boolean render(Canvas canvas) {
        if (null == xChart) return false;
        float left = 0.0f, top = 0.0f, right = 0.0f, bottom = 0.0f;
        if (axisTitleStyle == AxisTitleStyle.ENDPOINT) {
            left = xChart.getLeft();
            top = xChart.getPlotArea().getTop();
            right = xChart.getPlotArea().getRight();
            bottom = xChart.getPlotArea().getBottom();
        } else {
            left = xChart.getLeft();
            top = xChart.getTop();
            right = xChart.getRight();
            bottom = xChart.getBottom();
        }
        if (this.getLeftTitle().length() > 0) {
            drawLeftAxisTitle(canvas, getLeftTitle(), left, top, right, bottom);
        }
        if (this.getLowerTitle().length() > 0) {
            drawLowerAxisTitle(canvas, getLowerTitle(), left, top, right, bottom);
        }
        if (this.getRightTitle().length() > 0) {
            drawRightAxisTitle(canvas, getRightTitle(), left, top, right, bottom);
        }
        return true;
    }

    /**
     * 绘制左边轴标题
     *
     * @param axisTitle 内容
     * @param left      左边X坐标
     * @param top       上方Y坐标
     * @param right     右边X坐标
     * @param bottom    下方Y坐标
     */
    public void drawLeftAxisTitle(Canvas canvas, String axisTitle, double left, double top, double right, double bottom) {
        if (null == canvas) return;
        //是否需要绘制轴标题
        if (TextUtils.isEmpty(axisTitle)) return;
        //计算图列宽度
        double axisTitleTextHeight = DrawUtil.getInstance().getTextWidth(getLeftTitlePaint(), axisTitle);
        //画布与图表1/3的地方显示
        float axisTitleTextStartX = Math.round(left + mLeftAxisTitleOffsetX + getLeftTitlePaint().getTextSize());
        //轴标题Y坐标
        float axisTitleTextStartY = 0.0f;
        if (axisTitleStyle == AxisTitleStyle.ENDPOINT) {
            axisTitleTextStartY = Math.round(top + axisTitleTextHeight);
        } else {
            axisTitleTextStartY = Math.round(top + (bottom - top) / 2 + axisTitleTextHeight / 2);
        }
        // float axisTitleTextStartY = Math.round(top + (bottom - top ) /2 + axisTitleTextHeight/2);
        //得到单个轴标题文字高度
        double axisTitleCharHeight = 0d;
        for (int i = 0; i < axisTitle.length(); i++) {
            axisTitleCharHeight = DrawUtil.getInstance().getTextWidth(getLeftTitlePaint(), axisTitle.substring(i, i + 1));
            //绘制文字，旋转-90得到横向效果
            DrawUtil.getInstance().drawRotateText(axisTitle.substring(i, i + 1), axisTitleTextStartX, axisTitleTextStartY, -90, canvas, getLeftTitlePaint());
            axisTitleTextStartY -= axisTitleCharHeight;
        }
    }

    /**
     * 绘制底部轴标题
     *
     * @param axisTitle 内容
     * @param left      左边X坐标
     * @param top       上方Y坐标
     * @param right     右边X坐标
     * @param bottom    下方Y坐标
     */
    public void drawLowerAxisTitle(Canvas canvas, String axisTitle, double left, double top, double right, double bottom) {
        if (null == canvas) return;
        //是否需要绘制轴标题
        if (TextUtils.isEmpty(axisTitle)) return;
        //计算轴标题文字宽度
        double axisTitleTextHeight = DrawUtil.getInstance().getPaintFontHeight(getLowerTitlePaint());
        float axisTitleTextStartX = 0.0f;
        float axisTitleY = (float) MathUtil.getInstance().sub(xChart.getBottom(), axisTitleTextHeight / 2);
        if (axisTitleStyle == AxisTitleStyle.ENDPOINT) {
            axisTitleTextStartX = (float) right;
            //左下角交叉点绘制
            if (crossPointTitle.length() > 0) {
                getLowerTitlePaint().setTextAlign(Align.LEFT);
                DrawUtil.getInstance().drawRotateText(crossPointTitle, (float) left, axisTitleY, 0, canvas, getLowerTitlePaint());
            }
            getLowerTitlePaint().setTextAlign(Align.RIGHT);
        } else {
            axisTitleTextStartX = Math.round(left + (right - left) / 2);
        }
        DrawUtil.getInstance().drawRotateText(axisTitle, axisTitleTextStartX - mLowerAxisTitleOffsetY, axisTitleY, 0, canvas, getLowerTitlePaint());
    }

    /**
     * 绘制右边轴标题
     *
     * @param axisTitle 内容
     * @param left      左边X坐标
     * @param top       上方Y坐标
     * @param right     右边X坐标
     * @param bottom    下方Y坐标
     */
    public void drawRightAxisTitle(Canvas canvas, String axisTitle, double left, double top, double right, double bottom) {
        if (null == canvas) return;
        //是否需要绘制轴标题
        if (TextUtils.isEmpty(axisTitle)) return;
        //计算图列高度，超过最大高度要用...表示,这个后面再加
        float axisTitleTextHeight = DrawUtil.getInstance().getTextWidth(getRightTitlePaint(), axisTitle);
        //画布与图表1/3的地方显示
        float axisTitleTextStartX = Math.round(right - mRightAxisTitleOffsetX - getRightTitlePaint().getTextSize());
        //轴标题Y坐标
        float axisTitleTextStartY = Math.round(top + (bottom - top - axisTitleTextHeight) / 2);
        //得到单个轴标题文字高度
        float axisTitleCharHeight = 0.0f;
        for (int i = 0; i < axisTitle.length(); i++) {
            axisTitleCharHeight = DrawUtil.getInstance().getTextWidth(getRightTitlePaint(), axisTitle.substring(i, i + 1));
            //绘制文字，旋转-90得到横向效果
            DrawUtil.getInstance().drawRotateText(axisTitle.substring(i, i + 1), axisTitleTextStartX, axisTitleTextStartY, 90, canvas, getRightTitlePaint());
            axisTitleTextStartY += axisTitleCharHeight;
        }
    }
}
