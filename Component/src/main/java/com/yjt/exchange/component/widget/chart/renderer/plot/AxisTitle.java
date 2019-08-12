package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.widget.chart.constant.AxisTitleStyle;

public class AxisTitle {

    //图例文字画笔
    private Paint leftAxisTitlePaint = null;
    private Paint lowerAxisTitlePaint = null;
    private Paint rightAxisTitlePaint = null;
    //图例文字说明
    private String leftAxisTitle;
    private String lowerAxisTitle;
    private String rightAxisTitle;
    protected AxisTitleStyle axisTitleStyle = AxisTitleStyle.NORMAL;
    protected String crossPointTitle;
    //偏移距离
    protected float mLeftAxisTitleOffsetX = 0.f;
    protected float mRightAxisTitleOffsetX = 0.f;
    protected float mLowerAxisTitleOffsetY = 0.f;

    public AxisTitle() {

    }

    private void initLeftAxisTitlePaint() {
        if (null == leftAxisTitlePaint) {
            leftAxisTitlePaint = new Paint();
            leftAxisTitlePaint.setTextAlign(Align.CENTER);
            leftAxisTitlePaint.setAntiAlias(true);
            leftAxisTitlePaint.setTextSize(26);
            leftAxisTitlePaint.setColor(Color.rgb(255, 153, 204));
        }
    }

    private void initLowerAxisTitlePaint() {
        if (null == lowerAxisTitlePaint) {
            lowerAxisTitlePaint = new Paint();
            lowerAxisTitlePaint.setTextAlign(Align.CENTER);
            lowerAxisTitlePaint.setAntiAlias(true);
            lowerAxisTitlePaint.setTextSize(26);
            lowerAxisTitlePaint.setColor(Color.rgb(58, 65, 83));
        }
    }

    private void initRightAxisTitlePaint() {
        if (null == rightAxisTitlePaint) {
            rightAxisTitlePaint = new Paint();
            rightAxisTitlePaint.setTextAlign(Align.CENTER);
            rightAxisTitlePaint.setAntiAlias(true);
            rightAxisTitlePaint.setTextSize(26);
            rightAxisTitlePaint.setColor(Color.rgb(51, 204, 204));
        }
    }

    /**
     * 开放左边图例画笔
     *
     * @return 画笔
     */
    public Paint getLeftTitlePaint() {
        initLeftAxisTitlePaint();
        return leftAxisTitlePaint;
    }

    /**
     * 开放底部图例画笔
     *
     * @return 画笔
     */
    public Paint getLowerTitlePaint() {
        initLowerAxisTitlePaint();
        return lowerAxisTitlePaint;
    }

    /**
     * 开放右边图例画笔
     *
     * @return 画笔
     */
    public Paint getRightTitlePaint() {
        initRightAxisTitlePaint();
        return rightAxisTitlePaint;
    }

    /**
     * 设置左边图例内容
     *
     * @param title 图例内容
     */
    public void setLeftTitle(String title) {
        this.leftAxisTitle = title;
    }

    /**
     * 设置底部图例内容
     *
     * @param title 图例内容
     */
    public void setLowerTitle(String title) {
        this.lowerAxisTitle = title;
    }


    /**
     * 设置右边图例内容
     *
     * @param title 图例内容
     */
    public void setRightTitle(String title) {
        this.rightAxisTitle = title;
    }

    /**
     * 返回左边图例内容
     *
     * @return 图例内容
     */
    public String getLeftTitle() {
        return leftAxisTitle;
    }

    /**
     * 返回底部图例内容
     *
     * @return 图例内容
     */
    public String getLowerTitle() {
        return lowerAxisTitle;
    }

    /**
     * 返回右边图例内容
     *
     * @return 图例内容
     */
    public String getRightTitle() {
        return rightAxisTitle;
    }

    /**
     * 设置轴标题显示风格
     *
     * @param style
     */
    public void setTitleStyle(AxisTitleStyle style) {
        axisTitleStyle = style;
    }

    /**
     * 设置 交叉点轴标题
     *
     * @param title 标题
     */
    public void setCrossPointTitle(String title) {
        this.crossPointTitle = title;
    }

    /**
     * 左边轴标题偏移距离
     *
     * @param offset 偏移距离
     */
    public void setLeftAxisTitleOffsetX(float offset) {
        mLeftAxisTitleOffsetX = offset;
    }

    /**
     * 右边轴标题偏移距离
     *
     * @param offset 偏移距离
     */
    public void setRightAxisTitleOffsetX(float offset) {
        mRightAxisTitleOffsetX = offset;
    }

    /**
     * 底部轴标题偏移距离
     *
     * @param offset 偏移距离
     */
    public void setLowerAxisTitleOffsetY(float offset) {
        mLowerAxisTitleOffsetY = offset;
    }

}
