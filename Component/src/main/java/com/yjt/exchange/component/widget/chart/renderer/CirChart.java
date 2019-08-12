package com.hynet.heebit.components.widget.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.text.TextUtils;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.PieData;
import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.LabelBoxStyle;
import com.hynet.heebit.components.widget.chart.constant.LegendType;
import com.hynet.heebit.components.widget.chart.constant.SliceLabelStyle;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.renderer.info.PlotArcLabelInfo;
import com.hynet.heebit.components.widget.chart.renderer.plot.LabelBrokenLine;
import com.hynet.heebit.components.widget.chart.renderer.plot.LabelBrokenLineRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotLabel;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotLabelRender;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class CirChart extends EventChart {

    //半径
    private float radius = 0.0f;
    //标签注释显示位置
    private SliceLabelStyle sliceLabelStyle = SliceLabelStyle.INSIDE;
    //开放标签画笔让用户设置
    private Paint labelPaint = null;
    //初始偏移角度
    protected float offsetAngle = 0.0f;//180;
    protected float initializeOffsetAngle = 0.0f;
    //折线标签基类
    private LabelBrokenLineRender labelBrokenLineRender = null;
    //同步标签颜色
    private boolean synchronizeLabelLineColor = false;
    private boolean synchronizeLabelPointColor = false;
    private boolean synchronizeLabelColor = false;
    //用于设置标签特性
    private PlotLabelRender plotLabelRender = null;

    public CirChart() {
        //初始化图例
        if (null != plotLegendRender) {
            plotLegendRender.show();
            plotLegendRender.setType(LegendType.ROW);
            plotLegendRender.setHorizontalAlign(HorizontalAlign.CENTER);
            plotLegendRender.setVerticalAlign(VerticalAlign.BOTTOM);
            plotLegendRender.showBox();
            plotLegendRender.hideBackground();
        }
    }

    @Override
    protected void calcPlotRange() {
        super.calcPlotRange();
        this.radius = Math.min(div(this.plotAreaRender.getWidth(), 2f), div(this.plotAreaRender.getHeight(), 2f));
    }


    /**
     * 返回半径
     *
     * @return 半径
     */
    public float getRadius() {
        return radius;
    }

    /**
     * 设置饼图(pie chart)起始偏移角度
     *
     * @param Angle 偏移角度
     */
    public void setInitialAngle(float Angle) {
        initializeOffsetAngle = offsetAngle = Angle;
    }

    /**
     * 返回图的起始偏移角度
     *
     * @return 偏移角度
     */
    public float getInitialAngle() {
        return initializeOffsetAngle;
    }

    /**
     * 返回图的当前偏移角度
     *
     * @return 偏移角度
     */
    public float getOffsetAngle() {
        return offsetAngle;
    }


    /**
     * 设置标签显示在扇区的哪个位置(里面，外面，隐藏)
     *
     * @param style 显示位置
     */
    public void setLabelStyle(SliceLabelStyle style) {
        sliceLabelStyle = style;
        //INNER,OUTSIDE,HIDE
        switch (style) {
            case INSIDE:
                getLabelPaint().setTextAlign(Align.CENTER);
                break;
            case OUTSIDE:
                break;
            case HIDE:
                break;
            case BROKENLINE:
                break;
            default:
        }
    }

    /**
     * 返回标签风格设置
     *
     * @return 标签风格
     */
    public SliceLabelStyle getLabelStyle() {
        return sliceLabelStyle;
    }

    /**
     * 开放标签画笔
     *
     * @return 画笔
     */
    public Paint getLabelPaint() {
        if (null == labelPaint) {
            labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            labelPaint.setColor(Color.BLACK);
            labelPaint.setAntiAlias(true);
            labelPaint.setTextAlign(Align.CENTER);
            labelPaint.setTextSize(18);
        }
        return labelPaint;
    }

    /**
     * 开放折线标签绘制类(当标签为Line类型时有效)
     *
     * @return 折线标签绘制类
     */
    public LabelBrokenLine getLabelBrokenLine() {
        if (null == labelBrokenLineRender) labelBrokenLineRender = new LabelBrokenLineRender();
        return labelBrokenLineRender;
    }

    protected PointF renderLabelInside(Canvas canvas, String text, float itemAngle, float cirX, float cirY, float radius, float calcAngle, boolean showLabel) {
        //显示在扇形的中心
        float calcRadius = MathUtil.getInstance().sub(radius, radius / 2f);
        //计算百分比标签
        PointF point = MathUtil.getInstance().calcArcEndPointXY(
                cirX, cirY, calcRadius, calcAngle);
        //标识
        if (showLabel) {
            getPlotLabel().drawLabel(canvas, getLabelPaint(), text, point.x, point.y, itemAngle);
        }
        return (new PointF(point.x, point.y));
    }

    protected PointF renderLabelOutside(Canvas canvas, String text, float itemAngle, float cirX, float cirY, float radius, float calcAngle, boolean showLabel) {
        //显示在扇形的外部
        float calcRadius = MathUtil.getInstance().add(radius, radius / 10f);
        //计算百分比标签
        PointF point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, calcRadius, calcAngle);
        //标识
        if (showLabel) {
            getPlotLabel().drawLabel(canvas, getLabelPaint(), text, point.x, point.y, itemAngle);
        }
        return (new PointF(point.x, point.y));
    }

    //折线标签
    protected PointF renderLabelLine(Canvas canvas, PieData cData, float cirX, float cirY, float radius, float calcAngle, boolean showLabel) {
        if (null == labelBrokenLineRender) labelBrokenLineRender = new LabelBrokenLineRender();
        if (synchronizeLabelLineColor)
            labelBrokenLineRender.getLabelLinePaint().setColor(cData.getSliceColor());
        if (synchronizeLabelPointColor)
            labelBrokenLineRender.getPointPaint().setColor(cData.getSliceColor());
        return (labelBrokenLineRender.renderLabelLine(cData.getLabel(), cData.getItemLabelRotateAngle(), cirX, cirY, radius, calcAngle, canvas, getLabelPaint(), showLabel, plotLabelRender));
    }

    /**
     * 设置标签颜色与当地扇区颜色同步
     */
    public void syncLabelLineColor() {
        synchronizeLabelLineColor = true;
    }

    /**
     * 设置折线标签点颜色与当地扇区颜色同步
     */
    public void syncLabelPointColor() {
        synchronizeLabelPointColor = true;
    }

    /**
     * 设置折线标签颜色与当地扇区颜色同步
     */
    public void syncLabelColor() {
        synchronizeLabelColor = true;
    }

    /**
     * 用于设置标签显示属性
     *
     * @return 标签属性类
     */
    public PlotLabel getPlotLabel() {
        if (null == plotLabelRender) {
            plotLabelRender = new PlotLabelRender();
            plotLabelRender.setLabelBoxStyle(LabelBoxStyle.TEXT);
        }
        return plotLabelRender;
    }


    /**
     * 绘制标签
     *
     * @param canvas       画布
     * @param cData        PieData类
     * @param info         信息类
     * @param savePosition 是否保存位置
     * @param showLabel    是否显示标签
     *
     * @return 是否成功
     */
    protected boolean renderLabel(Canvas canvas, PieData cData, PlotArcLabelInfo info, boolean savePosition, boolean showLabel) {
        if (SliceLabelStyle.HIDE == sliceLabelStyle) return true;
        if (null == cData) return false;
        String lable = cData.getLabel();
        if (TextUtils.isEmpty(lable)) return true;
        float cirX = info.getX();
        float cirY = info.getY();
        float radius = info.getRadius();
        double offsetAngle = info.getOffsetAngle();
        float calcAngle = (float) MathUtil.getInstance().add(offsetAngle, info.getCurrentAngle() / 2);
        if (Float.compare(calcAngle, 0.0f) == 0 || Float.compare(calcAngle, 0.0f) == -1) {
             LogUtil.Companion.getInstance().print("计算出来的圆心角等于0.");
            return false;
        }
        PointF position;
        //标签颜色与当地扇区颜色同步
        if (synchronizeLabelColor) this.getLabelPaint().setColor(cData.getSliceColor());
        int color = getLabelPaint().getColor();
        //有定制需求
        SliceLabelStyle labelStyle = sliceLabelStyle;
        if (cData.getCustLabelStyleStatus()) {
            labelStyle = cData.getLabelStyle();
            if (SliceLabelStyle.INSIDE == labelStyle)
                getLabelPaint().setTextAlign(Align.CENTER);
            getLabelPaint().setColor(cData.getCustLabelColor());
        }
        if (SliceLabelStyle.INSIDE == labelStyle) {
            //显示在扇形的内部
            position = renderLabelInside(canvas, lable, cData.getItemLabelRotateAngle(), cirX, cirY, radius, calcAngle, showLabel);
        } else if (SliceLabelStyle.OUTSIDE == labelStyle) {
            //显示在扇形的外部
            position = renderLabelOutside(canvas, lable, cData.getItemLabelRotateAngle(), cirX, cirY, radius, calcAngle, showLabel);
        } else if (SliceLabelStyle.BROKENLINE == labelStyle) {
            //显示在扇形的外部
            //1/4处为起始点
            position = renderLabelLine(canvas, cData, cirX, cirY, radius, calcAngle, showLabel);
        } else {
             LogUtil.Companion.getInstance().print("未知的标签处理类型.");
            return false;
        }
        getLabelPaint().setColor(color);

        if (savePosition)
            info.setLabelPointF(position); //保存标签坐标位置
        return true;
    }

    @Override
    protected boolean postRender(Canvas canvas) {
        super.postRender(canvas);
        //计算主图表区范围
        calcPlotRange();
        //画Plot Area背景
        plotAreaRender.render(canvas);
        //绘制标题
        renderTitle(canvas);
        return true;
    }

    @Override
    public boolean render(Canvas canvas) {
        if (null == canvas)
            return false;
        if (getPanModeStatus()) {
            canvas.save();
            //设置原点位置
            switch (this.getPlotPanMode()) {
                case HORIZONTAL:
                    canvas.translate(translateXY[0], 0);
                    break;
                case VERTICAL:
                    canvas.translate(0, translateXY[1]);
                    break;
                default:
                    canvas.translate(translateXY[0], translateXY[1]);
                    break;
            }
            //绘制图表
            super.render(canvas);
            //还原
            canvas.restore();
        } else {
            //绘制图表
            super.render(canvas);
        }
        return true;
    }
}
