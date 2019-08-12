package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.AxisLocation;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.event.click.BarPosition;
import com.hynet.heebit.components.widget.chart.renderer.AxesChart;
import com.hynet.heebit.components.widget.chart.renderer.bar.Bar;
import com.hynet.heebit.components.widget.chart.renderer.bar.FlatBar;
import com.hynet.heebit.components.widget.chart.renderer.info.PlotAxisTick;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class RangeBarChart extends AxesChart {

    // 柱形基类
    private FlatBar flatBar = new FlatBar();
    // 数据源
    private List<RangeBarData> rangeBarData;
    private String key;
    private float barWidth = 50f;
    private boolean labelVisible = true;
    //分类轴的最大，最小值
    private double maxValue = 0d;
    private double minValue = 0d;

    public RangeBarChart() {
        categoryAxisDefaultSetting();
        dataAxisDefaultSetting();
    }

    @Override
    public ChartType getType() {
        return ChartType.RANGEBAR;
    }

    /**
     * 开放柱形绘制类
     *
     * @return 柱形绘制类
     */
    public Bar getBar() {
        return flatBar;
    }

    /**
     * 分类轴的数据源
     *
     * @param categories 分类集
     */
    public void setCategories(List<String> categories) {
        if (null != categoryAxisRender) categoryAxisRender.setDataBuilding(categories);
    }

    /**
     * 设置数据轴的数据源
     *
     * @param dataSeries 数据源
     */
    public void setDataSource(List<RangeBarData> dataSeries) {
        this.rangeBarData = dataSeries;
    }

    /**
     * 返回数据库的数据源
     *
     * @return 数据源
     */
    public List<RangeBarData> getDataSource() {
        return rangeBarData;
    }

    /**
     * 设置柱形宽度
     *
     * @param width 宽度
     */
    public void setBarWidth(float width) {
        barWidth = width;
    }

    /**
     * 返回柱形宽度
     *
     * @return 柱形宽度
     */
    public float getBarWidth() {
        return barWidth;
    }

    /**
     * 设置图例
     *
     * @param key 图例
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回图例
     *
     * @return 图例
     */
    public String getKey() {
        return key;
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

    @Override
    protected void categoryAxisDefaultSetting() {
        if (null == categoryAxisRender) return;
        switch (direction) {
            case HORIZONTAL:
                categoryAxisRender.setHorizontalTickAlign(Align.LEFT);
                categoryAxisRender.getTickLabelPaint().setTextAlign(Align.RIGHT);
                categoryAxisRender.setVerticalTickPosition(VerticalAlign.MIDDLE);
                setCategoryAxisLocation(AxisLocation.LEFT);
                break;
            case VERTICAL:
                categoryAxisRender.setHorizontalTickAlign(Align.CENTER);
                categoryAxisRender.getTickLabelPaint().setTextAlign(Align.CENTER);
                categoryAxisRender.setVerticalTickPosition(VerticalAlign.BOTTOM);
                setCategoryAxisLocation(AxisLocation.BOTTOM);
                break;
        }
    }

    @Override
    protected void dataAxisDefaultSetting() {
        if (null == dataAxisRender) return;
        switch (direction) {
            case HORIZONTAL:
                dataAxisRender.setHorizontalTickAlign(Align.CENTER);
                dataAxisRender.getTickLabelPaint().setTextAlign(Align.CENTER);
                dataAxisRender.setVerticalTickPosition(VerticalAlign.BOTTOM);
                setDataAxisLocation(AxisLocation.BOTTOM);
                break;
            case VERTICAL:
                dataAxisRender.setHorizontalTickAlign(Align.LEFT);
                dataAxisRender.getTickLabelPaint().setTextAlign(Align.RIGHT);
                dataAxisRender.setVerticalTickPosition(VerticalAlign.MIDDLE);
                setDataAxisLocation(AxisLocation.LEFT);
                break;
        }
    }


    private float[] cataPosition(double min, double max) {
        float[] pos = new float[2];
        float axisDataHeight = dataAxisRender.getAxisRange();
        float scrHeight = getAxisScreenHeight();
        double t = MathUtil.getInstance().sub(min, dataAxisRender.getAxisMin());
        pos[0] = mul(scrHeight, div((float) (t), axisDataHeight));
        t = MathUtil.getInstance().sub(max, dataAxisRender.getAxisMin());
        pos[1] = mul(scrHeight, div((float) (t), axisDataHeight));
        return pos;
    }

    /**
     * 显示数据的数据轴最大值
     *
     * @param value 数据轴最大值
     */
    public void setcategoryAxisRenderMax(double value) {
        maxValue = value;
    }

    /**
     * 设置分类轴最小值
     *
     * @param value 最小值
     */
    public void setcategoryAxisRenderMin(double value) {
        minValue = value;
    }


    /**
     * 绘制竖向柱形图
     */
    protected boolean renderVerticalBar(Canvas canvas) {
        //检查是否有设置分类轴的最大最小值
        if (maxValue == minValue && 0 == maxValue) {
             LogUtil.Companion.getInstance().print("请检查是否有设置分类轴的最大最小值。");
            return false;
        }
        if (null == rangeBarData) {
             LogUtil.Companion.getInstance().print("数据轴数据源为空");
            return false;
        }
        // 得到分类轴数据集
        List<String> dataSet = categoryAxisRender.getDataSet();
        if (null == dataSet) {
             LogUtil.Companion.getInstance().print("分类轴数据集为空.");
            return false;
        }
        if (null == rangeBarData) return false;
        // 得到分类轴数据集
        List<String> cateDataSet = categoryAxisRender.getDataSet();
        if (null == cateDataSet) return false;
        float currentX = 0.0f, barMaxPos = 0.0f, barMinPos = 0.0f;
        float barWidthHalf = barWidth / 2;
        float axisScreenWidth = getPlotScreenWidth();
        float fontHeight = DrawUtil.getInstance().getPaintFontHeight(flatBar.getItemLabelPaint());
        // X 轴 即分类轴
        int dataSetSize = rangeBarData.size();
        for (int i = 0; i < dataSetSize; i++) {
            // 得到分类对应的值数据集
            RangeBarData bd = rangeBarData.get(i);
            currentX = (float) (axisScreenWidth * ((bd.getX() - minValue) / (maxValue - minValue)));
            currentX = add(plotAreaRender.getLeft(), currentX);
            float[] pos = cataPosition(bd.getMin(), bd.getMax());
            barMaxPos = sub(plotAreaRender.getBottom(), pos[0]);
            barMinPos = sub(plotAreaRender.getBottom(), pos[1]);
            flatBar.renderBar(currentX - barWidthHalf, barMaxPos, currentX + barWidthHalf, barMinPos, canvas);
            //保存位置
            saveBarRectFRecord(i, 0, currentX - barWidthHalf + moveX, barMaxPos + moveY, currentX + barWidthHalf + moveX, barMinPos + moveY);
            //显示焦点框
            drawFocusRect(canvas, i, 0, currentX - barWidthHalf, barMaxPos, currentX + barWidthHalf, barMinPos);
            if (getLabelVisible()) {
                //柱形标签
                flatBar.renderBarItemLabel(getFormatterItemLabel(bd.getMax()), currentX, barMinPos - fontHeight / 2, canvas);
                //柱形标签
                flatBar.renderBarItemLabel(getFormatterItemLabel(bd.getMin()), currentX, barMaxPos + fontHeight + fontHeight / 2, canvas);
            }
        }
        return true;
    }

    /**
     * 返回当前点击点的信息
     *
     * @param x 点击点X坐标
     * @param y 点击点Y坐标
     *
     * @return 返回对应的位置记录
     */
    public BarPosition getPositionRecord(float x, float y) {
        return getBarRecord(x, y);
    }

    @Override
    protected void drawClipDataAxisGridlines(Canvas canvas) {
        // 与柱形图不同，无须多弄一个
        float XSteps = 0.0f, YSteps = 0.0f;
        // 数据轴数据刻度总个数
        int tickCount = dataAxisRender.getAixTickCount();
        int labeltickCount = tickCount;
        if (0 == tickCount) {
             LogUtil.Companion.getInstance().print("数据库数据源为0!");
            return;
        } else if (1 == tickCount)  //label仅一个时右移
            labeltickCount = tickCount - 1;
        // 标签轴(X 轴)		
        float axisX = 0.0f, axisY = 0.0f, currentX = 0.0f, currentY = 0.0f;
        // 标签
        double currentTickLabel = 0d;
        // 轴位置
        AxisLocation pos = getDataAxisLocation();
        //步长
        switch (pos) {
            case LEFT: //Y
            case RIGHT:
            case VERTICAL_CENTER:
                YSteps = getVerticalYSteps(labeltickCount);
                currentX = axisX = getAxisXPos(pos);
                currentY = axisY = plotAreaRender.getBottom();
                break;
            case TOP: //X
            case BOTTOM:
            case HORIZONTAL_CENTER:
                XSteps = getVerticalXSteps(labeltickCount);
                currentY = axisY = getAxisYPos(pos);
                currentX = axisX = plotAreaRender.getLeft();
                break;
        }
        dataTick.clear();
        //绘制
        for (int i = 0; i < tickCount; i++) {
            switch (pos) {
                case LEFT: //Y
                case RIGHT:
                case VERTICAL_CENTER:
                    // 依起始数据坐标与数据刻度间距算出上移高度
                    currentY = sub(plotAreaRender.getBottom(), mul(i, YSteps));
                    // 从左到右的横向网格线
                    drawHorizontalGridLines(canvas, plotAreaRender.getLeft(), plotAreaRender.getRight(), i, tickCount, YSteps, currentY);
                    // 标签
                    currentTickLabel = MathUtil.getInstance().add(dataAxisRender.getAxisMin(), i * dataAxisRender.getAxisSteps());
                    dataTick.add(new PlotAxisTick(i, axisX, currentY, Double.toString(currentTickLabel)));
                    break;
                case TOP: //X
                case BOTTOM:
                case HORIZONTAL_CENTER:
                    //bar
                    // 依起始数据坐标与数据刻度间距算出上移高度
                    currentX = add(axisX, mul(i, XSteps));
                    //绘制竖向网格线
                    drawVerticalGridLines(canvas, plotAreaRender.getTop(), plotAreaRender.getBottom(), i, tickCount, XSteps, currentX);
                    // 画上标签/刻度线	
                    currentTickLabel = MathUtil.getInstance().add(dataAxisRender.getAxisMin(), i * dataAxisRender.getAxisSteps());
                    dataTick.add(new PlotAxisTick(i, currentX, axisY, Double.toString(currentTickLabel)));
                    break;
            }
        }
    }


    /**
     * 绘制底部标签轴
     */
    @Override
    protected void drawClipCategoryAxisGridlines(Canvas canvas) {
        // 得到标签轴数据集
        List<String> dataSet = categoryAxisRender.getDataSet();
        // 与柱形图不同，无须多弄一个
        float XSteps = 0.0f, YSteps = 0.0f;

        int tickCount = dataSet.size();
        int labeltickCount = tickCount + 1;
        if (0 == tickCount) {
             LogUtil.Companion.getInstance().print("分类轴数据源为0!");
            return;
        }
        // 标签轴(X 轴)
        float axisX = 0.0f, axisY = 0.0f, currentX = 0.0f, currentY = 0.0f;
        AxisLocation pos = getCategoryAxisLocation();
        if (AxisLocation.LEFT == pos || AxisLocation.RIGHT == pos || AxisLocation.VERTICAL_CENTER == pos) {
            //line
            YSteps = getVerticalYSteps(labeltickCount);
            currentX = axisX = getAxisXPos(pos);
            currentY = axisY = plotAreaRender.getBottom();
        } else {
            // 依传入的分类个数与轴总宽度算出要画的分类间距数是多少
            // 总宽度 / 分类个数 = 间距长度    //getAxisScreenWidth()
            XSteps = getVerticalXSteps(labeltickCount);
            currentY = axisY = getAxisYPos(pos);
            currentX = axisX = plotAreaRender.getLeft();
        }
        cateTick.clear();
        //绘制
        for (int i = 0; i < tickCount; i++) {
            switch (pos) {
                case LEFT: //Y
                case RIGHT:
                case VERTICAL_CENTER:
                    // 依初超始Y坐标与分类间距算出当前刻度的Y坐标
                    currentY = sub(axisY, mul((i + 1), YSteps));
                    // 从左到右的横向网格线
                    drawHorizontalGridLines(canvas, plotAreaRender.getLeft(), plotAreaRender.getRight(), i, tickCount, YSteps, currentY);
                    if (!categoryAxisRender.isShowAxisLabels()) continue;
                    // 分类
                    cateTick.add(new PlotAxisTick(axisX, currentY, categoryAxisRender.getDataSet().get(i)));
                    break;
                case TOP: //X
                case BOTTOM:
                case HORIZONTAL_CENTER:
                    // 依初超始X坐标与分类间距算出当前刻度的X坐标
                    currentX = add(plotAreaRender.getLeft(), mul((i + 1), XSteps));
                    //绘制竖向网格线
                    drawVerticalGridLines(canvas, plotAreaRender.getTop(), plotAreaRender.getBottom(), i, tickCount, XSteps, currentX);
                    if (!categoryAxisRender.isShowAxisLabels()) continue;
                    cateTick.add(new PlotAxisTick(currentX, axisY, dataSet.get(i)));
                    break;
            }
        }
    }

    @Override
    protected void drawClipPlot(Canvas canvas) {
        switch (direction) {
            case HORIZONTAL:
                //renderHorizontalBar(canvas);
                break;
            case VERTICAL:
                renderVerticalBar(canvas);
                break;
        }
    }

    @Override
    protected void drawClipLegend(Canvas canvas) {
        // 绘制柱形图例
        plotLegendRender.renderRangeBarKey(canvas, getKey(), flatBar.getBarPaint().getColor());
    }
}
