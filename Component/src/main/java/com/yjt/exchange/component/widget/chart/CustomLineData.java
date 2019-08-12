package com.hynet.heebit.components.widget.chart;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.constant.LineStyle;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;

public class CustomLineData {

    private String label;
    private Double desireValue = 0d;
    private int color = Color.BLACK;
    private int lineStroke = 0;
    //文字旋转角度
    private float labelRotateAngle = 0.0f; //-45f;
    //设置Label显示位置(左，中，右)
    private Align labelAlign = Align.RIGHT;
    //设置Label显示位置(上，中，下)
    private VerticalAlign verticalAlign = VerticalAlign.TOP;
    //线的风格(点或线之类)
    private LineStyle lineStyle = LineStyle.SOLID;
    //设置线箭头 (三角，方形，棱形....)  
    private DotStyle dotStyle = DotStyle.HIDE;
    //标签偏移距离,注意，如是显示在中间，则会上移动此距离。
    private int labelOffset = 0;
    //定制线画笔
    private Paint paintCustomLine = null;
    private Paint paintLineLabel = null;
    //是否显示线
    private boolean lineVisible = true;

    public CustomLineData() {

    }

    /**
     * 定制线
     *
     * @param value 区分值
     * @param color 线颜色
     */
    public CustomLineData(Double value, int color) {
        setValue(value);
        setColor(color);
    }

    /**
     * 定制线
     *
     * @param label  标签
     * @param value  用区分的值
     * @param color  线颜色
     * @param stroke 线粗细
     */
    public CustomLineData(String label, Double value, int color, int stroke) {
        setLabel(label);
        setValue(value);
        setColor(color);
        setLineStroke(stroke);
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
     * 设置标签
     *
     * @param label 标签
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 取得当前区分值
     *
     * @return 区分值
     */
    public Double getValue() {
        return desireValue;
    }

    /**
     * 设置区分值
     *
     * @param value 区分值
     */
    public void setValue(Double value) {
        this.desireValue = value;
    }

    /**
     * 返回颜色
     *
     * @return 颜色
     */
    public int getColor() {
        return color;
    }

    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * 得到当前线粗细
     *
     * @return 线粗细
     */
    public int getLineStroke() {
        return lineStroke;
    }

    /**
     * 设置线的粗细
     *
     * @param stroke 粗细
     */
    public void setLineStroke(int stroke) {
        this.lineStroke = stroke;
    }

    /**
     * 返回是否有手工指定线的 粗细
     *
     * @return 是否有指定
     */
    public boolean isSetLineStroke() {
        return (0 != lineStroke);
    }


    // 设置线箭头 (三角，方形，棱形....)  
    public void setCustomLineCap(DotStyle style) {
        this.dotStyle = style;
    }

    public DotStyle getCustomeLineCap() {
        return dotStyle;
    }


    /**
     * 设置标签显示位置(左，中，右),适合于竖向图，在横向图下设置无效.
     *
     * @param align 位置
     */
    public void setLabelHorizontalPostion(Align align) //LabelAlign
    {
        labelAlign = align;
    }

    /**
     * 返回标签显示在左，中，右哪个位置
     *
     * @return 位置
     */
    public Align getLabelHorizontalPostion() {
        return labelAlign;
    }

    /**
     * 设置标签显示位置(上，中，下),适合于横向图，在竖向图下设置无效.
     *
     * @param postion 显示位置
     */
    public void setLabelVerticalAlign(VerticalAlign postion) {
        verticalAlign = postion;
    }

    /**
     * 返回标签显示在上，中，下哪个位置
     *
     * @return 位置
     */
    public VerticalAlign getLabelVerticalAlign() {
        return verticalAlign;
    }

    /**
     * 设置线的风格(点或线之类)
     *
     * @param style 线的风格
     */
    public void setLineStyle(LineStyle style) {
        lineStyle = style;
    }

    /**
     * 返回线的风格
     *
     * @return 线的风格
     */
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    /**
     * 返回轴标签文字旋转角度
     *
     * @return 旋转角度
     */
    public float getLabelRotateAngle() {
        return labelRotateAngle;
    }

    /**
     * 设置轴标签文字旋转角度
     *
     * @param rotateAngle 旋转角度
     */
    public void setLabelRotateAngle(float rotateAngle) {
        this.labelRotateAngle = rotateAngle;
    }

    /**
     * 设置标签的偏移距离
     *
     * @param offset 偏移距离
     */
    public void setLabelOffset(int offset) {
        labelOffset = offset;
    }

    /**
     * 返回标签的偏移距离
     *
     * @return 偏移距离
     */
    public int getLabelOffset() {
        return labelOffset;
    }

    /**
     * 开放定制线画笔
     *
     * @return 画笔
     */
    public Paint getCustomLinePaint() {
        if (null == paintCustomLine) {
            paintCustomLine = new Paint();
            paintCustomLine.setAntiAlias(true);
            paintCustomLine.setStrokeWidth(3);
            paintCustomLine.setTextSize(18);
            paintCustomLine.setTextAlign(Align.LEFT);
        }
        return paintCustomLine;
    }


    /**
     * 开放定制线标签画笔
     *
     * @return 画笔
     */
    public Paint getLineLabelPaint() {
        if (null == paintLineLabel) {
            paintLineLabel = new Paint();
            paintLineLabel.setAntiAlias(true);
            paintLineLabel.setStrokeWidth(3);
            paintLineLabel.setTextSize(18);
            paintLineLabel.setTextAlign(Align.LEFT);
        }
        return paintLineLabel;
    }

    /**
     * 是否隐藏线不显示
     *
     * @return 是否显示
     */
    public boolean isShowLine() {
        return lineVisible;
    }

    /**
     * 隐藏线让其不显示
     */
    public void hideLine() {
        lineVisible = false;
    }

    /**
     * 显示线
     */
    public void showLine() {
        lineVisible = true;
    }


}
