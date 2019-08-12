package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.LegendType;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.renderer.XChart;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotAttrInfo;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotAttrInfoRender;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class ArcLineChart extends XChart {

    //初始偏移角度
    private static final int OFFSET_ANGLE = 270;
    //开放标签和线画笔让用户设置
    private Paint labelPaint = null;
    private Paint linePaint = null;
    //数据源
    private List<ArcLineData> arcLineDatas;
    //柱形间距所占比例
    private float barInnerMargin = 0.5f;
    //标签偏移
    private float labelOffsetX = 0.0f;
    //内环填充颜色
    private Paint paintFill = null;
    //半径
    private float radius = 0.0f;
    //内环半径	
    private float innerRaius = 0.6f;
    //附加信息类
    private PlotAttrInfoRender plotAttrInfoRender = null;

    public ArcLineChart() {
        int fillColor = Color.BLACK;
        if (null != plotAreaRender)
            fillColor = this.plotAreaRender.getBackgroundPaint().getColor();
        if (null == paintFill) {
            paintFill = new Paint();
            paintFill.setColor(fillColor);
            paintFill.setAntiAlias(true);
        }
        if (null == plotAttrInfoRender) plotAttrInfoRender = new PlotAttrInfoRender();
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
    public ChartType getType() {
        return ChartType.ARCLINE;
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
     * 设置标签向左偏移位置多长
     *
     * @param offset 横移多少位
     */
    public void setLabelOffsetX(float offset) {
        labelOffsetX = offset;
    }

    /**
     * 开放标签画笔
     *
     * @return 画笔
     */
    public Paint getLabelPaint() {
        if (null == labelPaint) {
            labelPaint = new Paint();
            labelPaint.setColor(Color.BLACK);
            labelPaint.setTextSize(18);
            labelPaint.setAntiAlias(true);
            labelPaint.setTextAlign(Align.RIGHT);
        }
        return labelPaint;
    }

    /**
     * 开放画网线的画笔
     *
     * @return 画笔
     */
    public Paint getLinePaint() {
        if (null == linePaint) {
            linePaint = new Paint();
            linePaint.setColor(Color.rgb(180, 205, 230));
            linePaint.setAntiAlias(true);
            //linePaint.setStyle(Style.STROKE);
            linePaint.setStrokeWidth(3);
            linePaint.setStyle(Style.FILL);
        }
        return linePaint;
    }

    /**
     * 环内部填充画笔
     *
     * @return 画笔
     */
    public Paint getInnerPaint() {
        return paintFill;
    }


    /**
     * 设置图表的数据源
     *
     * @param arcLineDatas 来源数据集
     */
    public void setDataSource(List<ArcLineData> arcLineDatas) {
        this.arcLineDatas = arcLineDatas;
    }

    /**
     * 返回数据轴的数据源
     *
     * @return 数据源
     */
    public List<ArcLineData> getDataSource() {
        return arcLineDatas;
    }


    /**
     * 附加信息绘制处理类
     *
     * @return 信息基类
     */
    public PlotAttrInfo getPlotAttrInfo() {
        return plotAttrInfoRender;
    }

    /**
     * 设置柱形间空白所占的百分比
     *
     * @param percentage 百分比
     */
    public boolean setBarInnerMargin(float percentage) {
        if (Double.compare(percentage, 0d) == -1) {
             LogUtil.Companion.getInstance().print("此比例不能为负数噢!");
            return false;
        }
        if (Double.compare(percentage, 0.9d) == 1 || Double.compare(percentage, 0.9d) == 0) {
             LogUtil.Companion.getInstance().print("此比例不能大于等于0.9,要给柱形留下点显示空间!");
            return false;
        } else {
            this.barInnerMargin = percentage;
        }
        return true;
    }

    /**
     * 得到柱形间空白所占的百分比
     *
     * @return 百分比
     */
    public float getInnerMargin() {
        return barInnerMargin;
    }

    /**
     * 设置环内部填充相对于环所占的比例
     *
     * @param precentage 环所占比例
     */
    public void setInnerRaius(float precentage) {
        innerRaius = precentage;
    }

    /**
     * 检查角度的合法性
     *
     * @param Angle 角度
     *
     * @return 是否正常
     */
    protected boolean validateAngle(float Angle) {
        if (Float.compare(Angle, 0.0f) == 0 || Float.compare(Angle, 0.0f) == -1) {
             LogUtil.Companion.getInstance().print("扇区圆心角小于等于0度. 当前圆心角为:" + Float.toString(Angle));
            return false;
        }
        return true;
    }


    private boolean renderCap(Canvas canvas, float radius, PointF[] arrCapPoint, int[] arrCapColor) {
        getLinePaint().setColor(Color.RED);
        for (int i = 0; i < arrCapPoint.length; i++) {
            getLinePaint().setColor(arrCapColor[i]);
            canvas.drawCircle(arrCapPoint[i].x, arrCapPoint[i].y, radius, getLinePaint());
        }
        return true;
    }


    private boolean renderLabels(Canvas canvas, float radius, PointF[] arrPoint) {
        int i = 0;
        float currentAngle = 0.0f;
        float txtHeight = DrawUtil.getInstance().getPaintFontHeight(getLabelPaint()) / 3;

        for (ArcLineData cData : arcLineDatas) {
            currentAngle = cData.getSliceAngle();
            if (!validateAngle(currentAngle)) continue;
            getLabelPaint().setColor(cData.getBarColor());
            //标识
            canvas.drawText(cData.getLabel(), arrPoint[i].x - labelOffsetX, arrPoint[i].y + txtHeight, getLabelPaint());
            i++;
        }

        return true;
    }


    private boolean renderPlot(Canvas canvas) {
        if (null == arcLineDatas) {
             LogUtil.Companion.getInstance().print("数据源为空.");
            return false;
        }
        //中心点坐标
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        float radius = getRadius();
        //用于存放当前百分比的圆心角度
        float currentAngle = 0.0f;
        float offsetAngle = OFFSET_ANGLE;
        int i = 0;
        int dataCount = arcLineDatas.size();
        float barTotalSize = sub(radius, mul(radius, innerRaius));
        float mulBarSize = div(barTotalSize, dataCount);
        float spaceSize = mul(mulBarSize, barInnerMargin);
        float barSize = sub(mulBarSize, spaceSize);
        //标签
        float labelRadius = 0.0f;
        PointF[] arrLabPoint = new PointF[dataCount];
        //箭头
        PointF[] arrCapPoint = new PointF[dataCount];
        int[] arrCapColor = new int[dataCount];
        //绘制底盘
        canvas.drawCircle(cirX, cirY, radius, paintFill);
        //绘制柱形
        for (ArcLineData cData : arcLineDatas) {
            currentAngle = cData.getSliceAngle();
            if (!validateAngle(currentAngle)) continue;
            getLinePaint().setColor(cData.getBarColor());
            DrawUtil.getInstance().drawPercent(canvas, getLinePaint(), cirX, cirY, radius, offsetAngle, currentAngle, true);
            //箭头
            PointF point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, radius - barSize / 2, add(offsetAngle, currentAngle));
            arrCapPoint[i] = new PointF(point.x, point.y);
            arrCapColor[i] = cData.getBarColor();
            //标签
            labelRadius = radius - barSize / 2;
            point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, labelRadius, add(offsetAngle, 0));
            arrLabPoint[i] = new PointF(point.x, point.y);
            //底
            canvas.drawCircle(cirX, cirY, radius - barSize, paintFill);
            radius = sub(radius, mulBarSize);
            i++;
        }
        renderCap(canvas, barSize * 0.8f, arrCapPoint, arrCapColor);
        //绘制Label
        renderLabels(canvas, radius, arrLabPoint);
        //图KEY
        plotLegendRender.renderRoundBarKey(canvas, this.arcLineDatas);
        arrLabPoint = null;
        arrCapPoint = null;
        arrCapColor = null;
        return true;
    }

    @Override
    protected boolean postRender(Canvas canvas) {
        super.postRender(canvas);
        calcPlotRange();
        //绘制图表
        if (renderPlot(canvas)) {
            //绘制附加信息
            plotAttrInfoRender.renderAttrInfo(canvas, plotAreaRender.getCenterX(), plotAreaRender.getCenterY(), this.getRadius());
            //绘制标题
            renderTitle(canvas);
            return true;
        } else {
            return false;
        }
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
