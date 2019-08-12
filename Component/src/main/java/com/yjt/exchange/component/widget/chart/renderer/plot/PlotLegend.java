package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Color;
import android.graphics.Paint;

import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.LegendType;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;

public class PlotLegend {

    //数据集的说明描述与图这间的空白间距
    protected float margin = 10f;
    //数据集的说明描述画笔
    private Paint keyPaint = null;
    //是否显示图例
    private boolean visible = false;
    //图例起始偏移多少距离
    protected float offsetX = 0.0f;
    protected float offsetY = 0.0f;
    //行间距
    protected float rowSpan = 10.0f;
    protected float colSpan = 10.0f;
    //图例方向
    private LegendType legendType = LegendType.ROW;
    private HorizontalAlign horizontalAlign = HorizontalAlign.LEFT;
    private VerticalAlign verticalAlign = VerticalAlign.TOP;
    //box
    protected BorderRender borderRender = new BorderRender();
    protected boolean showBox = true;
    protected boolean showBoxBorder = true;
    protected boolean showBackground = true;

    public PlotLegend() {}

    /**
     * 在图的上方显示图例
     *
     */
    public void show() {
        visible = true;
    }

    /**
     * 在图的上方不显示图例
     */
    public void hide() {
        visible = false;
        if (null != keyPaint) keyPaint = null;
    }

    /**
     * 是否需绘制图的图例
     * @return 是否显示
     */
    public boolean isShow() {
        return visible;
    }

    /**
     * 不显示图例框
     */
    public void hideBox() {
        showBox = false;
    }

    /**
     * 不显示图例边框
     */
    public void hideBorder() {
        showBoxBorder = false;
    }

    /**
     * 不显示图例背景
     */
    public void hideBackground() {
        showBackground = false;
    }

    /**
     * 显示图例框
     */
    public void showBox() {
        showBox = true;
        showBorder();
        showBackground();
    }

    /**
     * 显示图例边框
     */
    public void showBorder() {
        showBoxBorder = true;
    }

    /**
     * 显示图背景
     */
    public void showBackground() {
        showBackground = true;
    }


    /**
     * 开放图例绘制画笔
     * @return 画笔
     */
    public Paint getPaint() {
        if (null == keyPaint) {
            keyPaint = new Paint();
            keyPaint.setColor(Color.BLACK);
            keyPaint.setAntiAlias(true);
            keyPaint.setTextSize(15);
        }
        return keyPaint;
    }

    /**
     * 设置图例间距
     * @param margin Key间距
     */
    public void setLabelMargin(float margin) {
        this.margin = margin;
    }


    /**
     * 设置行间距
     * @param span 间距
     */
    public void setRowSpan(float span) {
        rowSpan = span;
    }

    public void setColSpan(float span) {
        colSpan = span;
    }

    /**
     * 返回图例间距
     * @return Key间距
     */
    public float getLabelMargin() {
        return margin;
    }

    /**
     * 图例起始向X方向偏移多少距离
     * @param offset 偏移值
     */
    public void setOffsetX(float offset) {
        offsetX = offset;
    }

    /**
     * 图例起始向Y方向偏移多少距离
     * @param offset 偏移值
     */
    public void setOffsetY(float offset) {
        offsetY = offset;
    }

    /**
     * 图例显示类型:使用行类型横向显示，或使用列类型竖向显示 
     * @param type 显示类型
     */
    public void setType(LegendType type) {
        legendType = type;
    }

    /**
     * 返回图例显示类型
     * @return 显示类型
     */
    public LegendType getType() {
        return legendType;
    }

    /**
     * 设置横向显示方式位置
     * @param align 位置
     */
    public void setHorizontalAlign(HorizontalAlign align) {
        horizontalAlign = align;
    }

    /**
     * 返回横向显示方式位置
     * @return 位置
     */
    public HorizontalAlign getHorizontalAlign() {
        return horizontalAlign;
    }

    /**
     * 设置竖向显示方式位置
     * @param align 位置
     */
    public void setVerticalAlign(VerticalAlign align) {
        verticalAlign = align;
    }

    /**
     * 设置竖向显示方式位置
     * @return 位置
     */
    public VerticalAlign getVerticalAlign() {
        return verticalAlign;
    }


    /**
     * 开放图例框绘制基类
     * @return 框绘制类
     */
    public Border getBox() {
        return borderRender;
    }

}
