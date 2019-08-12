package com.hynet.heebit.components.widget.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.LegendType;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.event.click.PointPosition;
import com.hynet.heebit.components.widget.chart.utils.IFormatterDoubleCallBack;

public class RdChart extends EventChart {

    //半径
    private float mRadius = 0.0f;
    //初始偏移角度
    private int mOffsetAngle = 0;//180;
    //格式化线中点的标签显示
    private IFormatterDoubleCallBack iFormatterDoubleCallBack;
    //开放标签和线画笔让用户设置
    private Paint mPaintLabel = null;
    private Paint mPaintLine = null;

    public RdChart() {
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
        this.mRadius = Math.min(div(this.plotAreaRender.getWidth(), 2f), div(this.plotAreaRender.getHeight(), 2f));
    }

    /**
     * 返回当前点击点的信息
     *
     * @param x 点击点X坐标
     * @param y 点击点Y坐标
     *
     * @return 返回对应的位置记录
     */
    public PointPosition getPositionRecord(float x, float y) {
        return getPointRecord(x, y);
    }


    /**
     * 返回半径
     *
     * @return 半径
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * 设置图起始偏移角度
     *
     * @param angle 偏移角度
     */
    public void setInitialAngle(final int angle) {
        if (angle < 0 || angle > 360) {
             LogUtil.Companion.getInstance().print("起始偏移角度不能小于0或大于360");
        } else
            mOffsetAngle = angle;
    }


    /**
     * 返回图的起始偏移角度
     *
     * @return 偏移角度
     */
    public int getInitialAngle() {
        return mOffsetAngle;
    }


    /**
     * 设置线上点标签显示格式
     *
     * @param callBack 回调函数
     */
    public void setDotLabelFormatter(IFormatterDoubleCallBack callBack) {
        this.iFormatterDoubleCallBack = callBack;
    }

    /**
     * 返回线上点标签显示格式
     *
     * @param value 传入当前值
     *
     * @return 显示格式
     */
    protected String getFormatterDotLabel(double value) {
        String itemLabel = "";
        try {
            itemLabel = iFormatterDoubleCallBack.doubleFormatter(value);
        } catch (Exception ex) {
            itemLabel = Double.toString(value);
            // DecimalFormat df=new DecimalFormat("#0");
            // itemLabel = df.format(value).toString();
        }
        return itemLabel;
    }

    /**
     * 开放标签画笔
     *
     * @return 画笔
     */
    public Paint getLabelPaint() {
        if (null == mPaintLabel) {
            mPaintLabel = new Paint();
            mPaintLabel.setColor(Color.BLACK);
            mPaintLabel.setTextSize(18);
            mPaintLabel.setAntiAlias(true);
            mPaintLabel.setTextAlign(Align.CENTER);
        }
        return mPaintLabel;
    }

    /**
     * 开放画网线的画笔
     *
     * @return 画笔
     */
    public Paint getLinePaint() {
        if (null == mPaintLine) {
            mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaintLine.setColor(Color.rgb(180, 205, 230));
            mPaintLine.setStyle(Style.STROKE);
            mPaintLine.setStrokeWidth(3);
        }
        return mPaintLine;
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
