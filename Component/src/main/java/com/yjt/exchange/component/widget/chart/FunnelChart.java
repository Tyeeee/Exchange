package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextUtils;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.SortType;
import com.hynet.heebit.components.widget.chart.renderer.EventChart;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;

import java.util.Collections;
import java.util.List;

public class FunnelChart extends EventChart {

    private List<FunnelData> funnelData;
    private SortType sortType = SortType.DESC;
    //图的初始宽度
    private float plotWidthPercent = 100.f;
    private Paint paint = null;
    private Paint funnelLinePaint = null;
    private boolean funnelLineVisible = true;
    private Paint labelPaint = null;
    private Paint labelLinePaint = null;
    //同步标签颜色
    private boolean synchronizeLabelLineColor = false;
    private boolean synchronizeLabelColor = false;
    private boolean showLabelLine = false;
    private HorizontalAlign horizontalAlign = HorizontalAlign.CENTER;
    private boolean labelVisible = true;

    public FunnelChart() {

    }

    @Override
    public ChartType getType() {
        return ChartType.FUNNEL;
    }

    /**
     * 区域画笔
     *
     * @return 画笔
     */
    public Paint getPaint() {
        if (null == paint) paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        return paint;
    }

    /**
     * 各区域间的间隔线画笔
     *
     * @return 画笔
     */
    public Paint getFunnelLinePaint() {
        if (null == funnelLinePaint) funnelLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        funnelLinePaint.setStrokeWidth(5);
        return funnelLinePaint;
    }

    /**
     * 开放标签画笔
     *
     * @return 画笔
     */
    public Paint getLabelPaint() {
        if (null == labelPaint) labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        return labelPaint;
    }

    /**
     * 开放标签连接线画笔
     *
     * @return 画笔
     */
    public Paint getLabelLinePaint() {
        if (null == labelLinePaint) labelLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        return labelLinePaint;
    }

    /**
     * 设置用于绘图的宽度比例
     *
     * @param percent 比例
     */
    public void setPlotWidthPercent(float percent) {
        plotWidthPercent = percent;
    }

    /**
     * 设置报表的数据排序及显示方式
     *
     * @param type 显示方式
     */
    public void setSortType(SortType type) {
        sortType = type;
    }

    /**
     * 不显示标签连接线
     */
    public void hideLabelLine() {
        showLabelLine = false;
    }

    /**
     * 显示标签连接线
     */
    public void showLabelLine() {
        showLabelLine = true;
    }

    /**
     * 标签连接线显示状态
     *
     * @return 状态
     */
    public boolean isShowLabelLine() {
        return showLabelLine;
    }

    /**
     * 设置标签颜色与当地扇区颜色同步
     */
    public void syncLabelLineColor() {
        synchronizeLabelLineColor = true;
    }

    /**
     * 设置折线标签颜色与当地扇区颜色同步
     */
    public void syncLabelColor() {
        synchronizeLabelColor = true;
    }

    /**
     * 设置是否显示区域间隔线
     *
     * @param visible 是否显示
     */
    public void setFunnelLineVisible(boolean visible) {
        funnelLineVisible = visible;
    }

    /**
     * 返回是否显示区域间隔线
     *
     * @return 是否显示
     */
    public boolean getFunnelLineVisible() {
        return funnelLineVisible;
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
     * 显示标签显示位置
     *
     * @param align 位置
     */
    public void setLabelAlign(HorizontalAlign align) {
        horizontalAlign = align;
        switch (horizontalAlign) {
            case LEFT:
                getLabelPaint().setTextAlign(Align.LEFT);
                showLabelLine();
                break;
            case CENTER:
                getLabelPaint().setTextAlign(Align.CENTER);
                break;
            case RIGHT:
                getLabelPaint().setTextAlign(Align.RIGHT);
                showLabelLine();
                break;
            default:
                getLabelPaint().setTextAlign(Align.CENTER);
        }
    }

    /**
     * 返回标签显示位置
     *
     * @return 位置
     */
    public HorizontalAlign getLabelAlign() {
        return horizontalAlign;
    }

    /**
     * 返回图的数据源
     *
     * @return 数据源
     */
    public List<FunnelData> getDataSource() {
        return funnelData;
    }

    /**
     * 设置数据源
     *
     * @param dataSet 数据集
     */
    public void setDataSource(List<FunnelData> dataSet) {
        funnelData = dataSet;
    }

    private boolean sortDataSet() {
        if (null == funnelData) {
             LogUtil.Companion.getInstance().print("数据源为空!");
            return false;
        }
        for (int i = funnelData.size() - 1; i >= 0; i--) {
            FunnelData d = funnelData.get(i);
            if (Float.compare(d.getData(), 0.0f) == -1 || Float.compare(d.getData(), 0.0f) == 0) {
                funnelData.remove(i);
            }
        }
        if (funnelData.size() == 0) return false;
        if (SortType.NORMAL != sortType) Collections.sort(funnelData);
        return true;
    }


    private void drawTriangle(Canvas canvas, float cx, PointF start, PointF stop) {
        Path path = new Path();
        path.moveTo(start.x, start.y);
        path.lineTo(stop.x, stop.y);
        switch (sortType) {
            case DESC:
                path.lineTo(cx, plotAreaRender.getBottom());
                break;
            case ASC:
            case NORMAL:
            default:
                path.lineTo(cx, plotAreaRender.getTop());
        }
        path.close();
        getPaint().setColor(funnelData.get(0).getColor());
        canvas.drawPath(path, getPaint());
    }

    private float getHalfWidth(float funnelWidth, float data) {
        return funnelWidth * (data / 100) / 2;
    }

    protected void renderPlotDesc(Canvas canvas, float cx, float funnelWidth, float funnelHeight) {
        int count = funnelData.size();
        float halfWidth = 0.f;
        float bottomY = 0.f;
        PointF pStart = new PointF();
        PointF pStop = new PointF();
        pStart.x = cx - plotAreaRender.getPlotWidth() / 2;
        pStop.x = cx + plotAreaRender.getPlotWidth() / 2;
        pStart.y = pStop.y = plotAreaRender.getBottom();
        float labelY = 0.f;
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            FunnelData funnelData = this.funnelData.get(i);
            path.reset();
            if (i == 0) {
                path.moveTo(cx, plotAreaRender.getBottom());
            } else {
                path.moveTo(pStart.x, pStart.y);
                path.lineTo(pStop.x, pStop.y);
            }
            halfWidth = getHalfWidth(funnelWidth, funnelData.getData());
            bottomY = sub(plotAreaRender.getBottom(), i * funnelHeight);
            labelY = bottomY - funnelHeight / 2;
            pStart.x = cx - halfWidth;
            pStart.y = bottomY - funnelHeight;
            pStop.x = cx + halfWidth;
            pStop.y = bottomY - funnelHeight;
            path.lineTo(pStop.x, pStop.y);
            path.lineTo(pStart.x, pStart.y);
            this.getPaint().setColor(funnelData.getColor());
            path.close();
            if (funnelData.getAlpha() != -1) getPaint().setAlpha(funnelData.getAlpha());
            canvas.drawPath(path, this.getPaint());
            if (funnelData.getAlpha() != -1) getPaint().setAlpha(255);
            if (i != count - 1 && funnelLineVisible) {
                canvas.drawLine(pStart.x, pStart.y, pStop.x, pStop.y, this.getFunnelLinePaint());
            }
            renderLabels(canvas, funnelData.getLabel(), cx, labelY, funnelData.getColor());
        }
    }

    protected void renderPlotAsc(Canvas canvas, float cx, float funnelWidth, float funnelHeight) {
        int count = funnelData.size();
        float halfWidth = 0.f;
        float bottomY = 0.f;
        PointF pStart = new PointF();
        PointF pStop = new PointF();
        pStart.x = cx - plotAreaRender.getPlotWidth() / 2;
        pStop.x = cx + plotAreaRender.getPlotWidth() / 2;
        pStart.y = pStop.y = plotAreaRender.getBottom();
        float labelY = 0.f;
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            FunnelData d = funnelData.get(i);
            path.reset();
            if (i == 0) {//三角
                path.moveTo(cx, plotAreaRender.getTop());
            } else {
                path.moveTo(pStart.x, pStart.y);
                path.lineTo(pStop.x, pStop.y);
            }
            halfWidth = getHalfWidth(funnelWidth, d.getData());
            bottomY = add(plotAreaRender.getTop(), i * funnelHeight);
            labelY = bottomY + funnelHeight / 2;
            pStart.x = cx - halfWidth;
            pStart.y = bottomY + funnelHeight;
            pStop.x = cx + halfWidth;
            pStop.y = bottomY + funnelHeight;
            path.lineTo(pStop.x, pStop.y);
            path.lineTo(pStart.x, pStart.y);
            path.close();
            this.getPaint().setColor(d.getColor());
            if (d.getAlpha() != -1) getPaint().setAlpha(d.getAlpha());
            canvas.drawPath(path, this.getPaint());
            if (d.getAlpha() != -1) getPaint().setAlpha(255);
            if (i != count - 1 && funnelLineVisible) {
                canvas.drawLine(pStart.x, pStart.y, pStop.x, pStop.y, this.getFunnelLinePaint());
            }
            renderLabels(canvas, d.getLabel(), cx, labelY, d.getColor());
        }
    }

    private void renderPlotOne(Canvas canvas, float cx, float funnelWidth, float funnelHeight) {
        FunnelData funnelData = this.funnelData.get(0);
        float halfWidth = getHalfWidth(funnelWidth, funnelData.getData());
        PointF pStart = new PointF();
        PointF pStop = new PointF();
        pStart.x = cx - halfWidth;
        pStop.x = cx + halfWidth;
        if (SortType.DESC == sortType) {
            pStart.y = pStop.y = plotAreaRender.getTop();
        } else {
            pStart.y = pStop.y = plotAreaRender.getBottom();
        }
        if (funnelData.getAlpha() != -1) getPaint().setAlpha(funnelData.getAlpha());
        drawTriangle(canvas, cx, pStart, pStop);
        if (funnelData.getAlpha() != -1) getPaint().setAlpha(255);
        float labelY = plotAreaRender.getBottom() - plotAreaRender.getHeight() / 2;
        renderLabels(canvas, funnelData.getLabel(), cx, labelY, funnelData.getColor());
        return;
    }

    protected void renderLabels(Canvas canvas, String label, float cx, float y, int color) {
        if (!getLabelVisible()) return;
        if (TextUtils.isEmpty(label)) return;
        if (synchronizeLabelLineColor) {
            getLabelLinePaint().setColor(color);
            getLabelPaint().setColor(color);
        } else if (synchronizeLabelColor) {
            getLabelPaint().setColor(color);
        }
        if (isShowLabelLine()) {
            float labelWidth = DrawUtil.getInstance().getTextWidth(getLabelPaint(), label);
            switch (getLabelAlign()) {
                case LEFT:
                    canvas.drawLine(cx, y, plotAreaRender.getLeft() + labelWidth, y, getLabelLinePaint());
                    break;
                case CENTER:
                    break;
                case RIGHT:
                    canvas.drawLine(cx, y, plotAreaRender.getRight() - labelWidth, y, getLabelLinePaint());
                    break;
                default:
                    break;
            }
        }
        float labelX = 0.f, labelY = 0.f;
        switch (getLabelAlign()) {
            case LEFT:
                labelX = plotAreaRender.getLeft();
                break;
            case CENTER:
                labelX = cx;
                break;
            case RIGHT:
                labelX = plotAreaRender.getRight();
                break;
            default:
                labelX = cx;
        }
        labelY = y + (DrawUtil.getInstance().getPaintFontHeight(getLabelPaint()) / 3);
        canvas.drawText(label, labelX, labelY, getLabelPaint());
    }

    protected void renderPlot(Canvas canvas) {
        if (!sortDataSet()) return;
        int count = funnelData.size();
        float funnelWidth = plotAreaRender.getPlotWidth() * (plotWidthPercent / 100);
        float funnelHeight = this.plotAreaRender.getHeight() / count;
        float cx = plotAreaRender.getCenterX();
        if (1 == count) {
            renderPlotOne(canvas, cx, funnelWidth, funnelHeight);
        }
        if (SortType.DESC == sortType) {
            renderPlotDesc(canvas, cx, funnelWidth, funnelHeight);
        } else {
            renderPlotAsc(canvas, cx, funnelWidth, funnelHeight);
        }
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
        //绘制图表
        renderPlot(canvas);
        //显示焦点
        renderFocusShape(canvas);
        //响应提示
        renderToolTip(canvas);
        return true;
    }
}
