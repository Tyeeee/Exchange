package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.AxisLocation;
import com.hynet.heebit.components.widget.chart.constant.BarCenterStyle;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.ColumnLabelStyle;
import com.hynet.heebit.components.widget.chart.constant.Direction;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.event.click.BarPosition;
import com.hynet.heebit.components.widget.chart.renderer.AxesChart;
import com.hynet.heebit.components.widget.chart.renderer.bar.Bar;
import com.hynet.heebit.components.widget.chart.renderer.bar.FlatBar;
import com.hynet.heebit.components.widget.chart.renderer.info.AnchorDataPoint;
import com.hynet.heebit.components.widget.chart.renderer.info.PlotAxisTick;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotCustomLine;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class BarChart extends AxesChart {

    // 柱形基类
    private FlatBar flatBar = new FlatBar();
    // 数据源
    private List<BarData> barDatas;
    //用于绘制定制线(分界线)
    private PlotCustomLine plotCustomLine = null;
    //批注
    private List<AnchorDataPoint> anchorDataPoints;
    //值与轴最小值相等
    protected boolean equalAxisMin = false;
    //标签和对象依哪种风格居中显示
    private BarCenterStyle barCenterStyle = BarCenterStyle.SPACE;


    public BarChart() {
        //默认为竖向设置
        defaultAxisSetting();
    }

    @Override
    public ChartType getType() {
        return ChartType.BAR;
    }

    /**
     * 设置柱形居中位置,依刻度线居中或依刻度中间点居中。
     *
     * @param style 居中风格
     */
    public void setBarCenterStyle(BarCenterStyle style) {
        barCenterStyle = style;
    }

    /**
     * 返回柱形居中位置,依刻度线居中或依刻度中间点居中。
     *
     * @return 居中风格
     */
    public BarCenterStyle getBarCenterStyle() {
        return barCenterStyle;
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
     * 当值与轴最小值相等时，不显示柱形及标签
     */
    public void hideBarEqualAxisMin() {
        equalAxisMin = false;
    }

    /**
     * 当值与轴最小值相等时，正常显示柱形及标签
     */
    public void showBarEqualAxisMin() {
        equalAxisMin = true;
    }

    /**
     * 设置定制线值
     *
     * @param lineSet 定制线数据集合
     */
    public void setCustomLines(List<CustomLineData> lineSet) {
        if (null == plotCustomLine) plotCustomLine = new PlotCustomLine();
        plotCustomLine.setCustomLines(lineSet);
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
     * 设置批注
     *
     * @param anchor 批注
     */
    public void setAnchorDataPoint(List<AnchorDataPoint> anchor) {
        anchorDataPoints = anchor;
    }

    /**
     * 返回批注
     *
     * @return 批注
     */
    public List<AnchorDataPoint> getAnchorDataPoint() {
        return anchorDataPoints;
    }

    /**
     * 设置数据轴的数据源
     *
     * @param dataSeries 数据源
     */
    public void setDataSource(List<BarData> dataSeries) {
        this.barDatas = dataSeries;
    }

    /**
     * 返回数据库的数据源
     *
     * @return 数据源
     */
    public List<BarData> getDataSource() {
        return barDatas;
    }

    /**
     * 设置图的显示方向,即横向还是竖向显示柱形
     *
     * @param direction 横向/竖向
     */
    public void setChartDirection(Direction direction) {
        this.direction = direction;
        defaultAxisSetting();
    }

    /**
     * 返回图的显示方向,即横向还是竖向显示柱形
     *
     * @return 横向/竖向
     */
    public Direction getChartDirection() {
        return direction;
    }

    /**
     * 图为横向或竖向时，轴和Bar的默认显示风格
     */
    protected void defaultAxisSetting() {
        if (null == direction) return;
        categoryAxisDefaultSetting();
        dataAxisDefaultSetting();
        if (null != getBar()) {
            switch (direction) {
                case HORIZONTAL:
                    getBar().getItemLabelPaint().setTextAlign(Align.LEFT);
                    getBar().setBarDirection(Direction.HORIZONTAL);
                    break;
                case VERTICAL:
                    getBar().setBarDirection(Direction.VERTICAL);
                    break;
            }
        }
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


    /**
     * 比较传入的各个数据集，找出最大数据个数
     *
     * @return 最大数据个数
     */
    protected int getDataAxisDetailSetMaxSize() {
        if (barDatas == null) return 0;
        // 得到最大size个数
        int dsetMaxSize = 0;
        int size = barDatas.size();
        for (int i = 0; i < size; i++) {
            if (dsetMaxSize < barDatas.get(i).getDataSet().size())
                dsetMaxSize = barDatas.get(i).getDataSet().size();
        }
        return dsetMaxSize;
    }


    protected int getDataTickCount() {
        int tickCount = dataAxisRender.getAixTickCount();
        return tickCount + 1;
    }

    protected int getCateTickCount() {
        int count = categoryAxisRender.getDataSet().size();
        if (BarCenterStyle.SPACE != barCenterStyle)
            count += 1;
        return count;
    }

    @Override
    protected void drawClipDataAxisGridlines(Canvas canvas) {
        // 与柱形图不同，无须多弄一个
        float XSteps = 0.0f, YSteps = 0.0f;
        // 数据轴数据刻度总个数
        int tickCount = dataAxisRender.getAixTickCount();
        int labeltickCount = tickCount + 1;
        if (0 == tickCount) {
             LogUtil.Companion.getInstance().print("数据轴数据源为0!");
            return;
        }
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
                YSteps = getVerticalYSteps(tickCount);
                currentX = axisX = getAxisXPos(pos);
                currentY = axisY = plotAreaRender.getBottom();
                break;
            case TOP: //X
            case BOTTOM:
            case HORIZONTAL_CENTER:
                XSteps = getVerticalXSteps(tickCount);

                currentY = axisY = getAxisYPos(pos);
                currentX = axisX = plotAreaRender.getLeft();
                break;
        }
        dataTick.clear();
        //绘制
        for (int i = 0; i < labeltickCount; i++) {
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
                    //sub(axisX ,get3DOffsetX())
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
        int labeltickCount = getCateTickCount();
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
        } else { //TOP BOTTOM
            // 依传入的分类个数与轴总宽度算出要画的分类间距数是多少
            // 总宽度 / 分类个数 = 间距长度    //getAxisScreenWidth()
            XSteps = getVerticalXSteps(labeltickCount);
            currentY = axisY = getAxisYPos(pos);
            currentX = axisX = plotAreaRender.getLeft();
        }
        cateTick.clear();
        float labelX, labelY;
        boolean showTicks = true;
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
                    labelY = currentY;
                    labelX = currentX = sub(axisX, get3DOffsetX());
                    if (BarCenterStyle.SPACE == barCenterStyle) {
                        //if(i == tickCount - 1)continue;
                        if (i == tickCount - 1) showTicks = false;
                        labelY = add(currentY, div(YSteps, 2));
                    }
                    // 分类
                    cateTick.add(new PlotAxisTick(currentX, currentY, categoryAxisRender.getDataSet().get(i), labelX, labelY, showTicks));
                    break;
                case TOP: //X
                case BOTTOM:
                case HORIZONTAL_CENTER:
                    // 依初超始X坐标与分类间距算出当前刻度的X坐标
                    currentX = add(plotAreaRender.getLeft(), mul((i + 1), XSteps));
                    //绘制竖向网格线
                    drawVerticalGridLines(canvas, plotAreaRender.getTop(), plotAreaRender.getBottom(), i, tickCount, XSteps, currentX);
                    if (!categoryAxisRender.isShowAxisLabels()) continue;
                    float currentY2 = add(axisY, get3DBaseOffsetY());
                    currentX = sub(currentX, get3DBaseOffsetX());
                    labelX = currentX;
                    labelY = currentY2;
                    if (BarCenterStyle.SPACE == barCenterStyle) {
                        if (i == tickCount - 1) {
                            showTicks = false;
                        }
                        labelX = sub(currentX, div(XSteps, 2));
                    }
                    cateTick.add(new PlotAxisTick(currentX, currentY2, dataSet.get(i), labelX, labelY, showTicks));
                    break;
            }
        }
    }

    protected float get3DOffsetX() {
        return 0.0f;
    }

    // 分类
    protected float get3DBaseOffsetX() {
        return 0.0f;
    }

    // 分类
    protected float get3DBaseOffsetY() {
        return 0.0f;
    }

    /**
     * 绘制横向柱形图
     *
     * @throws InterruptedException 例外
     */
    protected boolean renderHorizontalBar(Canvas canvas) {
        if (null == barDatas || barDatas.size() == 0) return false;
        // 得到Y 轴分类横向间距高度
        float YSteps = getVerticalYSteps(getCateTickCount());
        float barInitX = plotAreaRender.getLeft();
        float barInitY = plotAreaRender.getBottom();
        // 依柱形宽度，多柱形间的偏移值 与当前数据集的总数据个数得到当前分类柱形要占的高度
        int barNumber = getDatasetSize(barDatas);
        if (barNumber <= 0) return false;
        int currNumber = 0;

        float[] ret = flatBar.getBarHeightAndMargin(YSteps, barNumber);
        if (null == ret || ret.length != 2) {
             LogUtil.Companion.getInstance().print("分隔间距计算失败.");
            return false;
        }
        float barHeight = ret[0];
        float barInnerMargin = ret[1];
        float labelBarUseHeight = add(mul(barNumber, barHeight), mul(sub(barNumber, 1), barInnerMargin));
        Double bv = 0d;
        float dataAxisStd = getHPDataAxisStdX();
        float itemLabelWidth = 0.f;
        float barLeft = 0.0f, barBottom = 0.0f, barTop = 0.f, barRight = 0.f;
        float labelLeftX, labelLeftY, currLabelY, drawBarButtomY, rightX;
        for (int i = 0; i < barNumber; i++) {
            // 得到分类对应的值数据集
            BarData bd = barDatas.get(i);
            List<Double> barValues = bd.getDataSet();
            if (null == barValues) continue;
            List<Integer> barDataColor = bd.getDataColor();
            // 设置成对应的颜色
            flatBar.getBarPaint().setColor(bd.getColor());
            // 画同分类下的所有柱形
            int vSize = barValues.size();
            for (int j = 0; j < vSize; j++) {
                bv = barValues.get(j);
                setBarDataColor(flatBar.getBarPaint(), barDataColor, j, bd.getColor());
                currLabelY = sub(barInitY, mul((j + 1), YSteps));
                if (BarCenterStyle.SPACE == barCenterStyle) {
                    drawBarButtomY = add(add(currLabelY, div(YSteps, 2)), labelBarUseHeight / 2);
                } else {
                    drawBarButtomY = add(currLabelY, labelBarUseHeight / 2);
                }
                drawBarButtomY = sub(drawBarButtomY, add(barHeight, barInnerMargin) * currNumber);
                labelLeftX = rightX = getHPValPosition(bv);
                String label = getFormatterItemLabel(bv);
                if (flatBar.getItemLabelsVisible())
                    itemLabelWidth = DrawUtil.getInstance().getTextWidth(flatBar.getItemLabelPaint(), label);
                if (dataAxisRender.getAxisStdStatus()) {
                    //反向
                    if (bv < dataAxisRender.getAxisStd()) {
                        barLeft = rightX;
                        barTop = sub(drawBarButtomY, barHeight);
                        barRight = dataAxisStd;
                        barBottom = drawBarButtomY;
                        labelLeftX = rightX - itemLabelWidth;
                    } else {
                        barLeft = dataAxisStd;
                        barTop = sub(drawBarButtomY, barHeight);
                        barRight = rightX;
                        barBottom = drawBarButtomY;
                    }
                } else {
                    barLeft = barInitX;
                    barTop = sub(drawBarButtomY, barHeight);
                    barRight = rightX;
                    barBottom = drawBarButtomY;
                }
                // 画出柱形
                flatBar.renderBar(barLeft, barBottom, barRight, barTop, canvas);
                //保存位置
                saveBarRectFRecord(i, j, barLeft + moveX, barTop + moveY, barRight + moveX, barBottom + moveY);
                labelLeftY = sub(barBottom, barHeight / 2);
                //在柱形的顶端显示上柱形的批注形状
                drawAnchor(this.anchorDataPoints, i, j, canvas, labelLeftX, labelLeftY, 0.0f);
                // 柱形顶端标识
                if (!equalAxisMin && Double.compare(dataAxisRender.getAxisMin(), bv) == 0) {
                } else {
                    if (flatBar.getItemLabelStyle() == ColumnLabelStyle.BOTTOM) {
                        flatBar.renderBarItemLabel(label, barLeft, labelLeftY, canvas);
                    } else {
                        flatBar.renderBarItemLabel(label, labelLeftX, labelLeftY, canvas);
                    }

                }
                //显示焦点框
                drawFocusRect(canvas, i, j, barLeft, barTop, barRight, barBottom);
            }
            currNumber++;
        }
        //画横向柱形图，竖向的定制线
        if (null != plotCustomLine) {
            plotCustomLine.setHorizontalPlot(dataAxisRender, plotAreaRender, this.getAxisScreenWidth());
            plotCustomLine.renderHorizontalCustomlinesDataAxis(canvas);
        }
        return true;
    }

    /**
     * 返回指定数据在图中的横向坐标位置
     *
     * @param bv 数据
     *
     * @return 坐标位置
     */
    public float getHPValPosition(double bv) {
        double vaxlen = MathUtil.getInstance().sub(bv, dataAxisRender.getAxisMin());
        float valuePostion = mul(getPlotScreenWidth(), div((float) (vaxlen), dataAxisRender.getAxisRange()));
        return (add(plotAreaRender.getLeft(), valuePostion));
    }

    public float getHPDataAxisStdX() {
        if (dataAxisRender.getAxisStdStatus()) {
            return getHPValPosition(dataAxisRender.getAxisStd());
        } else {
            return plotAreaRender.getLeft();
        }
    }

    public float getVPDataAxisStdY() {
        if (dataAxisRender.getAxisStdStatus()) {
            return getVPValPosition(dataAxisRender.getAxisStd());
        } else {
            return plotAreaRender.getBottom();
        }
    }

    /**
     * 返回指定数据在图中的竖向坐标位置
     *
     * @param bv 数据
     *
     * @return 坐标位置
     */
    public float getVPValPosition(double bv) {
        float vaxlen = (float) MathUtil.getInstance().sub(bv, dataAxisRender.getAxisMin());
        float valuePostion = mul(getPlotScreenHeight(), div(vaxlen, dataAxisRender.getAxisRange()));
        return (sub(plotAreaRender.getBottom(), valuePostion));
    }

    @Override
    protected float getAxisXPos(AxisLocation location) {
        if (Direction.HORIZONTAL == direction &&
                dataAxisRender.getAxisStdStatus() && categoryAxisRender.getAxisBuildStdStatus()) {
            return getHPDataAxisStdX();
        } else {
            return super.getAxisXPos(location);
        }
    }

    @Override
    protected float getAxisYPos(AxisLocation location) {
        if (Direction.VERTICAL == direction &&
                dataAxisRender.getAxisStdStatus() && categoryAxisRender.getAxisBuildStdStatus()) {
            return getVPDataAxisStdY();
        } else {
            return super.getAxisYPos(location);
        }
    }

    @Override
    protected void drawClipCategoryAxisLine(Canvas canvas) {
        if (Direction.VERTICAL == direction && dataAxisRender.getAxisStdStatus() && categoryAxisRender.getAxisBuildStdStatus()) {
            float y = getVPDataAxisStdY();
            categoryAxisRender.renderAxis(canvas, plotAreaRender.getLeft(), y, plotAreaRender.getRight(), y);
        } else if (Direction.HORIZONTAL == direction && dataAxisRender.getAxisStdStatus() && categoryAxisRender.getAxisBuildStdStatus()) {
            float x = getHPDataAxisStdX();
            categoryAxisRender.renderAxis(canvas, x, plotAreaRender.getTop(), x, plotAreaRender.getBottom());
        } else {
            super.drawClipCategoryAxisLine(canvas);
        }
    }

    /**
     * 绘制竖向柱形图
     */
    protected boolean renderVerticalBar(Canvas canvas) {
        if (null == barDatas || barDatas.isEmpty()) return false;
        // 得到分类轴数据集
        List<String> dataSet = categoryAxisRender.getDataSet();
        if (null == dataSet) return false;
        float XSteps = getVerticalXSteps(getCateTickCount());
        float dataAxisStd = getVPDataAxisStdY();
        float itemFontHeight = 0.f;
        if (flatBar.getItemLabelsVisible())
            itemFontHeight = DrawUtil.getInstance().getPaintFontHeight(flatBar.getItemLabelPaint());
        int barNumber = getDatasetSize(barDatas);
        if (barNumber <= 0) return false;
        int currNumber = 0;
        float[] ret = flatBar.getBarWidthAndMargin(XSteps, barNumber);
        if (null == ret || ret.length != 2) {
             LogUtil.Companion.getInstance().print("分隔间距计算失败.");
            return false;
        }
        float barWidth = ret[0];
        float barInnerMargin = ret[1];
        float labelBarUseWidth = add(mul(barNumber, barWidth), mul(sub(barNumber, 1), barInnerMargin));
        float barLeft = 0.0f, barBottom = 0.0f, barTop = 0.f, barRight = 0.f;
        float currLabelX, drawBarStartX, topY, labelTopX, labelTopY;
        // X 轴 即分类轴
        int size = barDatas.size();
        for (int i = 0; i < size; i++) {
            // 得到分类对应的值数据集
            BarData bd = barDatas.get(i);
            List<Double> barValues = bd.getDataSet();
            if (null == barValues) continue;
            //用于处理单独针对某些柱子指定颜色的情况
            List<Integer> barDataColor = bd.getDataColor();
            // 设成对应的颜色
            flatBar.getBarPaint().setColor(bd.getColor());
            // 画出分类对应的所有柱形
            int countChild = barValues.size();
            for (int j = 0; j < countChild; j++) {
                Double bv = barValues.get(j);
                setBarDataColor(flatBar.getBarPaint(), barDataColor, j, bd.getColor());
                currLabelX = add(plotAreaRender.getLeft(), mul((j + 1), XSteps));
                if (BarCenterStyle.SPACE == barCenterStyle) {
                    drawBarStartX = sub(currLabelX, div(XSteps, 2));
                    drawBarStartX = sub(drawBarStartX, labelBarUseWidth / 2);
                } else {
                    drawBarStartX = sub(currLabelX, labelBarUseWidth / 2);
                }
                // 计算同分类多柱 形时，新柱形的起始X坐标
                drawBarStartX = add(drawBarStartX, add(barWidth, barInnerMargin) * currNumber);
                labelTopY = topY = getVPValPosition(bv);
                if (dataAxisRender.getAxisStdStatus()) {
                    //反向
                    if (bv < dataAxisRender.getAxisStd()) {
                        barLeft = drawBarStartX;
                        barTop = dataAxisStd;
                        barRight = add(drawBarStartX, barWidth);
                        barBottom = topY;
                        labelTopY = labelTopY + itemFontHeight;
                    } else {
                        barLeft = drawBarStartX;
                        barTop = topY;
                        barRight = add(drawBarStartX, barWidth);
                        barBottom = dataAxisStd;
                    }
                } else {
                    barLeft = drawBarStartX;
                    barTop = topY;
                    barRight = add(drawBarStartX, barWidth);
                    barBottom = plotAreaRender.getBottom();
                }
                // 画出柱形
                flatBar.renderBar(barLeft, barBottom, barRight, barTop, canvas);
                //保存位置
                saveBarRectFRecord(i, j, barLeft + moveX, barTop + moveY, barRight + moveX, barBottom + moveY);
                //显示焦点框
                drawFocusRect(canvas, i, j, barLeft, barTop, barRight, barBottom);
                labelTopX = add(drawBarStartX, barWidth / 2);
                //在柱形的顶端显示批注
                drawAnchor(this.anchorDataPoints, i, j, canvas, labelTopX, labelTopY, 0.0f);
                // 在柱形的顶端显示上柱形当前值
                if (!equalAxisMin && Double.compare(dataAxisRender.getAxisMin(), bv) == 0) {
                } else {
                    if (flatBar.getItemLabelStyle() == ColumnLabelStyle.BOTTOM) {
                        flatBar.renderBarItemLabel(getFormatterItemLabel(bv), labelTopX, barBottom, canvas);
                    } else {
                        flatBar.renderBarItemLabel(getFormatterItemLabel(bv), labelTopX, labelTopY, canvas);
                    }
                }
            }
            currNumber++;
        }
        //画竖向柱形图的定制线
        if (null != plotCustomLine) {
            plotCustomLine.setVerticalPlot(dataAxisRender, plotAreaRender, getAxisScreenHeight());
            plotCustomLine.renderVerticalCustomlinesDataAxis(canvas);
        }
        return true;
    }

    @Override
    protected void drawClipPlot(Canvas canvas) {
        switch (direction) {
            case HORIZONTAL:
                renderHorizontalBar(canvas);
                break;
            case VERTICAL:
                renderVerticalBar(canvas);
                break;
        }
    }

    @Override
    protected void drawClipLegend(Canvas canvas) {
        plotLegendRender.renderBarKey(canvas, this.barDatas);
    }

    protected int getDatasetSize(List<BarData> dataSource) {
        if (null == dataSource) return 0;
        int result = dataSource.size();
        int count = result;
        // X 轴 即分类轴
        for (int i = 0; i < count; i++) {
            BarData bd = dataSource.get(i);
            List<Double> barValues = bd.getDataSet();
            if (barValues.size() == 1) {
                if (Double.compare(barValues.get(0), dataAxisRender.getAxisMin()) == 0)
                    result--;
            }
        }
        return result;
    }


    /**
     * 对于有为单个柱形设置颜色的情况，以这个函数来为画笔设置相应的颜色
     *
     * @param paint        柱形画笔
     * @param lstDataColor 数据颜色集
     * @param currNumber   当前序号
     * @param defaultColor 默认的柱形颜色
     */
    protected void setBarDataColor(Paint paint, List<Integer> lstDataColor, int currNumber, int defaultColor) {
        if (null != lstDataColor) {
            if (lstDataColor.size() > currNumber) {
                paint.setColor(lstDataColor.get(currNumber));
            } else {
                paint.setColor(defaultColor);
            }
        }
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

}
