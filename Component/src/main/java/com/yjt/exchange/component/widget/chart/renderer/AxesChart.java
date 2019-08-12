package com.hynet.heebit.components.widget.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.hynet.heebit.components.widget.chart.constant.AxisLocation;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.Direction;
import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.LegendType;
import com.hynet.heebit.components.widget.chart.constant.OddEven;
import com.hynet.heebit.components.widget.chart.constant.PanMode;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.renderer.axis.CategoryAxis;
import com.hynet.heebit.components.widget.chart.renderer.axis.CategoryAxisRender;
import com.hynet.heebit.components.widget.chart.renderer.axis.DataAxis;
import com.hynet.heebit.components.widget.chart.renderer.axis.DataAxisRender;
import com.hynet.heebit.components.widget.chart.renderer.info.PlotAxisTick;
import com.hynet.heebit.components.widget.chart.renderer.plot.AxisTitle;
import com.hynet.heebit.components.widget.chart.renderer.plot.AxisTitleRender;
import com.hynet.heebit.components.widget.chart.utils.IFormatterDoubleCallBack;

import java.util.ArrayList;

public class AxesChart extends EventChart {

    //数据轴
    protected DataAxisRender dataAxisRender = null;
    //标签轴
    protected CategoryAxisRender categoryAxisRender = null;
    //轴标题类
    private AxisTitleRender axisTitleRender = null;
    //格式化柱形顶上或线交叉点的标签
    private IFormatterDoubleCallBack iFormatterDoubleCallBack;
    // 确定是竖向柱形图(默认)还是横向
    protected Direction direction = Direction.VERTICAL;
    //Pan模式下移动距离
    protected float moveX = 0.0f;
    protected float moveY = 0.0f;
    //数据轴显示在左边还是右边
    private AxisLocation dataAxisLocation = AxisLocation.LEFT;
    private AxisLocation categoryAxisLocation = AxisLocation.BOTTOM;
    //是否将轴封闭
    private boolean axesClosed = false;
    // 对于轴标签滑动时的显示处理并不是很好，不过已经没精力重构了。
    // 目前可用下面的方式来限定:
    //     可通过指定下面两个参数值来限定x/y轴标签可移出的范围
    //     但注意这两个参数是同时限制上下或左右。
    private float margin = -10.0f;
    private float xMargin = -25.f;// 25.0f; //散点和气泡要注意下
    //轴刻度的位置信息
    protected ArrayList<PlotAxisTick> dataTick = null;
    protected ArrayList<PlotAxisTick> cateTick = null;

    private ClipExt mClipExt = null;


    public AxesChart() {
        if (null == dataTick) dataTick = new ArrayList<>();
        if (null == cateTick) cateTick = new ArrayList<>();
        initializeChart();
    }

    /**
     * 初始化设置
     */
    private void initializeChart() {
        //数据轴
        if (null == dataAxisRender) initializeDataAxis();
        //标签轴
        if (null == categoryAxisRender) initCategoryAxis();
        //初始化图例
        if (null != plotLegendRender) {
            plotLegendRender.show();
            plotLegendRender.setType(LegendType.ROW);
            plotLegendRender.setHorizontalAlign(HorizontalAlign.LEFT);
            plotLegendRender.setVerticalAlign(VerticalAlign.TOP);
            plotLegendRender.hideBox();
        }
    }

    /**
     * 开放数据轴绘制类
     *
     * @return 数据轴绘制类
     */
    public DataAxis getDataAxisRender() {
        //数据轴
        initializeDataAxis();
        return dataAxisRender;
    }

    /**
     * 开放标签轴绘制类
     *
     * @return 标签轴绘制类
     */
    public CategoryAxis getCategoryAxisRender() {
        //标签轴
        initCategoryAxis();
        return categoryAxisRender;
    }

    private void initCategoryAxis() {
        if (null == categoryAxisRender) categoryAxisRender = new CategoryAxisRender();
    }

    public void initializeDataAxis() {
        if (null == dataAxisRender) dataAxisRender = new DataAxisRender();
    }

    protected void drawCategoryAxisLabels(Canvas canvas, ArrayList<PlotAxisTick> lstLabels) {
        if (null == lstLabels) return;
        boolean showTicks = true;
        for (int i = 0; i < lstLabels.size(); i++) {
            PlotAxisTick t = lstLabels.get(i);
            switch (categoryAxisLocation) {
                case LEFT: //Y
                case RIGHT:
                case VERTICAL_CENTER:
                    if (!t.isShowTickMarks() || !isDrawYAxisTickMarks(t.Y, moveY))
                        showTicks = false;
                    categoryAxisRender.renderAxisHorizontalTick(
                            this.getLeft(),
                            this.getPlotArea().getLeft(),
                            canvas, t.X, t.Y,
                            t.label, t.labelX, t.labelY,
                            showTicks);
                    break;
                case TOP: //X
                case BOTTOM:
                case HORIZONTAL_CENTER:
                    OddEven oddEven = (i % 2 != 0) ? OddEven.ODD : OddEven.EVEN;
                    if (!t.isShowTickMarks() || !isDrawXAxisTickMarks(t.X, moveX))
                        showTicks = false;
                    categoryAxisRender.renderAxisVerticalTick(canvas, t.X, t.Y, t.label, t.labelX, t.labelY, showTicks, oddEven);
                    break;

            }
            showTicks = true;
        }
    }

    protected void drawDataAxisLabels(Canvas canvas, ArrayList<PlotAxisTick> plotAxisTicks) {
        if (null == plotAxisTicks) return;
        for (int i = 0; i < plotAxisTicks.size(); i++) {
            PlotAxisTick t = plotAxisTicks.get(i);
            OddEven oddEven = (i % 2 != 0) ? OddEven.ODD : OddEven.EVEN;
            dataAxisRender.setAxisTickCurrentID(t.ID);
            switch (dataAxisLocation) {
                case LEFT: //Y
                case RIGHT:
                case VERTICAL_CENTER:
                    dataAxisRender.renderAxisHorizontalTick(this.getLeft(), this.getPlotArea().getLeft(), canvas, t.X, t.Y, t.label, isDrawYAxisTickMarks(t.Y, moveY));
                    break;
                case TOP: //X
                case BOTTOM:
                case HORIZONTAL_CENTER:
                    dataAxisRender.renderAxisVerticalTick(canvas, t.X, t.Y, t.label, isDrawXAxisTickMarks(t.X, moveX), oddEven);
                    break;
            }
        }
    }

    /**
     * 开放轴标题绘制类
     *
     * @return 图例绘制类
     */
    public AxisTitle getAxisTitleRender() {
        if (null == axisTitleRender) axisTitleRender = new AxisTitleRender();
        return axisTitleRender;
    }

    /**
     * 轴所占的屏幕宽度
     *
     * @return 屏幕宽度
     */
    protected float getAxisScreenWidth() {
        if (null == plotAreaRender) return 0.0f;
        return (Math.abs(plotAreaRender.getRight() - plotAreaRender.getLeft()));
    }

    protected float getPlotScreenWidth() {
        if (null == plotAreaRender) return 0.0f;
        return (Math.abs(plotAreaRender.getPlotRight() - plotAreaRender.getPlotLeft()));
    }

    protected float getPlotScreenHeight() {
        if (null == plotAreaRender) return 0.0f;
        return (Math.abs(plotAreaRender.getPlotBottom() - plotAreaRender.getPlotTop()));
    }


    /**
     * 轴所占的屏幕高度
     *
     * @return 屏幕高度
     */
    protected float getAxisScreenHeight() {
        if (null == plotAreaRender) return 0.0f;
        return (Math.abs(plotAreaRender.getBottom() - plotAreaRender.getTop()));
    }

    /**
     * 竖向柱形图
     * Y轴的屏幕高度/数据轴的刻度标记总数 = 步长
     *
     * @return Y轴步长
     */
    protected float getVerticalYSteps(int tickCount) {
        return (div(getPlotScreenHeight(), tickCount));
    }

    /**
     * 竖向柱形图
     * 得到X轴的步长
     * X轴的屏幕宽度 / 刻度标记总数  = 步长
     *
     * @param tickCount 刻度标记总数
     *
     * @return X轴步长
     */
    public float getVerticalXSteps(int tickCount) {
        //柱形图为了让柱形显示在tick的中间，会多出一个步长即(dataSet.size()+1)	
        return (div(getPlotScreenWidth(), tickCount));
    }

    /**
     * 设置标签显示格式
     *
     * @param callBack 回调函数
     */
    public void setItemLabelFormatter(IFormatterDoubleCallBack callBack) {
        this.iFormatterDoubleCallBack = callBack;
    }

    /**
     * 返回标签显示格式
     *
     * @param value 传入当前值
     *
     * @return 显示格式
     */
    protected String getFormatterItemLabel(double value) {
        return iFormatterDoubleCallBack.doubleFormatter(value);
    }


    /**
     * 检查Y轴的刻度是否显示
     *
     * @param currentY y坐标
     * @param moveY    y坐标平移值
     *
     * @return 是否绘制
     */

    protected boolean isDrawYAxisTickMarks(float currentY, float moveY) {
        if (Float.compare(currentY, plotAreaRender.getTop() - moveY) == -1 || Float.compare(currentY, plotAreaRender.getBottom() - moveY) == 1) {
            return false;
        }
        return true;
    }


    /**
     * 检查X轴的刻度是否显示
     *
     * @param currentX x坐标
     * @param moveX    x坐标平移值
     *
     * @return 是否绘制
     */

    protected boolean isDrawXAxisTickMarks(float currentX, float moveX) {
        if (Float.compare(currentX + moveX, plotAreaRender.getLeft()) == -1) return false;
        if (Float.compare(currentX + moveX, plotAreaRender.getRight()) == 1) return false;
        return true;
    }

    //横向网格线
    protected void drawHorizontalGridLines(Canvas canvas, float plotLeft, float plotRight, int tickID, int tickCount, float YSteps, float currentY) {
        if (tickID < 0) return; //tickID <= 0
        // 从左到右的横向网格线
        if (tickID > 0) {
            if (tickID % 2 != 0) {
                plotGridRender.renderOddRowsFill(canvas, plotLeft, add(currentY, YSteps), plotRight, currentY);
            } else {
                plotGridRender.renderEvenRowsFill(canvas, plotLeft, add(currentY, YSteps), plotRight, currentY);
            }
        }
        if (tickID < tickCount) {
            plotGridRender.setPrimaryTickLine(dataAxisRender.isPrimaryTick(tickID));
            plotGridRender.renderGridLinesHorizontal(canvas, plotLeft, currentY, plotRight, currentY);
        }
    }

    //绘制竖向网格线
    protected void drawVerticalGridLines(Canvas canvas, float plotTop, float plotBottom, int tickID, int tickCount, float XSteps, float currentX) {
        // 绘制竖向网格线
        if (plotGridRender.isShowVerticalLines()) {
            //if (i > 0 && i + 1 < tickCount) //line
            plotGridRender.renderGridLinesVertical(canvas, currentX, plotBottom, currentX, plotTop);
        }
    }


    /**
     * 设置数据轴显示在哪边,默认是左边
     *
     * @param location 显示位置
     */
    public void setDataAxisLocation(AxisLocation location) {
        dataAxisLocation = location;
    }

    /**
     * 返回数据轴显示在哪边
     *
     * @return 显示位置
     */
    public AxisLocation getDataAxisLocation() {
        return dataAxisLocation;
    }

    /**
     * 设置分类轴显示在哪边,默认是底部
     *
     * @param location 显示位置
     */
    public void setCategoryAxisLocation(AxisLocation location) {
        categoryAxisLocation = location;
    }

    /**
     * 返回分类轴显示在哪边
     *
     * @return 显示位置
     */
    public AxisLocation getCategoryAxisLocation() {
        return categoryAxisLocation;
    }

    protected float getAxisXPos(AxisLocation location) {
        if (AxisLocation.RIGHT == location) {    //显示在右边
            return plotAreaRender.getRight();
        } else if (AxisLocation.LEFT == location) {
            //显示在左边
            return plotAreaRender.getLeft();
        } else if (AxisLocation.VERTICAL_CENTER == location) {
            //显示在中间
            return plotAreaRender.getCenterX();
        }
        return 0;
    }

    protected float getAxisYPos(AxisLocation location) {
        if (AxisLocation.TOP == location) {
            return plotAreaRender.getTop();
        } else if (AxisLocation.BOTTOM == location) {
            return plotAreaRender.getBottom();
        } else if (AxisLocation.HORIZONTAL_CENTER == location) {
            return plotAreaRender.getCenterY();
        }
        return 0;
    }

    protected void categoryAxisDefaultSetting() {
        if (null == dataAxisLocation) return;
        if (null == categoryAxisRender) return;
        if (!categoryAxisRender.isShow()) return;
        if (null != direction) {
            switch (direction) {
                case HORIZONTAL: {
                    this.setCategoryAxisLocation(AxisLocation.LEFT);
                    break;
                }
                case VERTICAL: {
                    this.setCategoryAxisLocation(AxisLocation.BOTTOM);
                    break;
                }
            }
        }
        if (AxisLocation.LEFT == dataAxisLocation) {
            categoryAxisRender.setHorizontalTickAlign(Align.CENTER);
        }
        categoryAxisRender.getAxisPaint().setStrokeWidth(2);
        categoryAxisRender.getTickMarksPaint().setStrokeWidth(2);
    }

    protected void dataAxisDefaultSetting() {
        if (null == dataAxisLocation) return;
        if (null == dataAxisRender) return;
        if (!dataAxisRender.isShow()) return;
        if (null != direction) {
            switch (direction) {
                case HORIZONTAL: {
                    this.setDataAxisLocation(AxisLocation.BOTTOM);
                    break;
                }
                case VERTICAL: {
                    this.setDataAxisLocation(AxisLocation.LEFT);
                    break;
                }
            }
        }
        if (AxisLocation.LEFT == dataAxisLocation) {
            dataAxisRender.setHorizontalTickAlign(Align.LEFT);
        } else {
            dataAxisRender.setHorizontalTickAlign(Align.RIGHT);
            if (dataAxisRender.isShowAxisLabels())
                dataAxisRender.getTickLabelPaint().setTextAlign(Align.LEFT);
        }
        if (dataAxisRender.isShowAxisLine())
            dataAxisRender.getAxisPaint().setStrokeWidth(2);
        if (dataAxisRender.isShowTickMarks())
            dataAxisRender.getTickMarksPaint().setStrokeWidth(2);
    }

    /**
     * 封闭轴
     *
     * @param status 状态
     */
    public void setAxesClosed(boolean status) {
        axesClosed = status;
    }

    /**
     * 是否封闭轴
     *
     * @return 状态
     */
    public boolean getAxesClosedStatus() {
        return axesClosed;
    }

    protected void initializeMoveXY() {
        moveX = moveY = 0.0f;
        switch (this.getPlotPanMode()) {
            case HORIZONTAL:
                moveX = translateXY[0];
                break;
            case VERTICAL:
                moveY = translateXY[1];
                break;
            default:
                moveX = translateXY[0];
                moveY = translateXY[1];
                break;
        }
    }

    protected void drawClipCategoryAxisGridlines(Canvas canvas) {
    }

    protected void drawClipDataAxisGridlines(Canvas canvas) {
    }

    protected void drawClipPlot(Canvas canvas) {
    }

    protected void drawClipAxisClosed(Canvas canvas) {
        if (!getAxesClosedStatus()) return;
        float plotLeft = plotAreaRender.getLeft();
        float plotTop = plotAreaRender.getTop();
        float plotRight = plotAreaRender.getRight();
        float plotBottom = plotAreaRender.getBottom();
        switch (dataAxisLocation) {
            case LEFT:
            case RIGHT:
            case VERTICAL_CENTER:
                dataAxisRender.renderAxisLine(canvas, plotLeft, plotTop, plotLeft, plotBottom);
                dataAxisRender.renderAxisLine(canvas, plotRight, plotTop, plotRight, plotBottom);
                break;
            case TOP:
            case BOTTOM:
            case HORIZONTAL_CENTER:
                dataAxisRender.renderAxisLine(canvas, plotLeft, plotTop, plotRight, plotTop);
                dataAxisRender.renderAxisLine(canvas, plotLeft, plotBottom, plotRight, plotBottom);
                break;
        }
        switch (categoryAxisLocation) {
            case LEFT:
            case RIGHT:
            case VERTICAL_CENTER:
                categoryAxisRender.renderAxisLine(canvas, plotLeft, plotBottom, plotLeft, plotTop);
                categoryAxisRender.renderAxisLine(canvas, plotRight, plotBottom, plotRight, plotTop);
                break;
            case TOP:
            case BOTTOM:
            case HORIZONTAL_CENTER:
                categoryAxisRender.renderAxisLine(canvas, plotLeft, plotTop, plotRight, plotTop);
                categoryAxisRender.renderAxisLine(canvas, plotLeft, plotBottom, plotRight, plotBottom);
                break;
        }
    }

    protected void drawClipDataAxisLine(Canvas canvas) {
        float plotLeft = plotAreaRender.getLeft();
        float plotTop = plotAreaRender.getTop();
        float plotRight = plotAreaRender.getRight();
        float plotBottom = plotAreaRender.getBottom();
        float vcX = plotLeft + (plotRight - plotLeft) / 2;
        float hcY = plotTop + (plotBottom - plotTop) / 2;
        switch (dataAxisLocation) {
            case LEFT:
                dataAxisRender.renderAxis(canvas, plotLeft, plotBottom, plotLeft, plotTop);
                break;
            case RIGHT:
                dataAxisRender.renderAxis(canvas, plotRight, plotTop, plotRight, plotBottom);
                break;
            case VERTICAL_CENTER:
                dataAxisRender.renderAxis(canvas, vcX, plotTop, vcX, plotBottom);
                break;
            case TOP:
                dataAxisRender.renderAxis(canvas, plotLeft, plotTop, plotRight, plotTop);
                break;
            case BOTTOM:
                dataAxisRender.renderAxis(canvas, plotLeft, plotBottom, plotRight, plotBottom);
                break;
            case HORIZONTAL_CENTER:
                dataAxisRender.renderAxis(canvas, plotLeft, hcY, plotRight, hcY);
                break;
        }
    }

    protected void drawClipCategoryAxisLine(Canvas canvas) {
        float plotLeft = plotAreaRender.getLeft();
        float plotTop = plotAreaRender.getTop();
        float plotRight = plotAreaRender.getRight();
        float plotBottom = plotAreaRender.getBottom();
        float vcX = plotLeft + (plotRight - plotLeft) / 2;
        float hcY = plotTop + (plotBottom - plotTop) / 2;
        switch (categoryAxisLocation) {
            case LEFT:
                categoryAxisRender.renderAxis(canvas, plotLeft, plotBottom, plotLeft, plotTop);
                break;
            case RIGHT:
                categoryAxisRender.renderAxis(canvas, plotRight, plotTop, plotRight, plotBottom);
                break;
            case VERTICAL_CENTER:
                categoryAxisRender.renderAxis(canvas, vcX, plotTop, vcX, plotBottom);
                break;
            case TOP:
                categoryAxisRender.renderAxis(canvas, plotLeft, plotTop, plotRight, plotTop);
                break;
            case BOTTOM:
                categoryAxisRender.renderAxis(canvas, plotLeft, plotBottom, plotRight, plotBottom);
                break;
            case HORIZONTAL_CENTER:
                categoryAxisRender.renderAxis(canvas, plotLeft, hcY, plotRight, hcY);
                break;
        }
    }

    protected void drawClipAxisLine(Canvas canvas) {
        drawClipDataAxisLine(canvas);
        drawClipCategoryAxisLine(canvas);
    }

    protected void drawClipDataAxisTickMarks(Canvas canvas) {
        drawDataAxisLabels(canvas, dataTick);
        dataTick.clear();
    }

    protected void drawClipCategoryAxisTickMarks(Canvas canvas) {
        drawCategoryAxisLabels(canvas, cateTick);
        cateTick.clear();
    }

    protected void drawClipLegend(Canvas canvas) {

    }

    protected boolean drawFixedPlot(Canvas canvas) {
        this.moveX = this.moveY = 0.0f;
        //绘制Y轴tick和marks
        drawClipDataAxisGridlines(canvas);
        //绘制X轴tick和marks
        drawClipCategoryAxisGridlines(canvas);
        //绘图
        drawClipPlot(canvas);
        //轴线
        drawClipAxisClosed(canvas);
        drawClipAxisLine(canvas);
        //轴刻度
        drawClipDataAxisTickMarks(canvas);
        drawClipCategoryAxisTickMarks(canvas);
        //图例
        drawClipLegend(canvas);
        return true;
    }

    /**
     * X方向的轴刻度平移扩展间距,即控制刻度标签在移绘图区后可多显示范围。
     *
     * @param margin 间距
     */
    public void setXTickMarksOffsetMargin(float margin) {
        xMargin = margin;
    }

    /**
     * Y方向的轴刻度平移扩展间距,即控制刻度标签在移绘图区后可多显示范围。
     *
     * @param margin 间距
     */
    public void setYTickMarksOffsetMargin(float margin) {
        this.margin = margin;
    }


    protected float getClipYMargin() {
        return (this.margin + this.getBorderWidth());
    }

    protected float getClipXMargin() {
        return (this.xMargin + this.getBorderWidth());
    }


    public ClipExt getClipExt() {
        if (null == mClipExt) mClipExt = new ClipExt();
        return mClipExt;
    }


    protected boolean drawClipVerticalPlot(Canvas canvas) {
        //显示绘图区rect
        float offsetX = translateXY[0];
        float offsetY = translateXY[1];
        initializeMoveXY();
        float yMargin = getClipYMargin();
        float xMargin = getClipXMargin();
        float gWidth = 0.0f;
        drawClipAxisClosed(canvas);
        //设置图显示范围
        canvas.save();
        canvas.clipRect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        if (PanMode.VERTICAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            if (getPlotGrid().isShowVerticalLines())
                gWidth = this.getPlotGrid().getVerticalLinePaint().getStrokeWidth();
            //绘制Y轴tick和marks
            canvas.save();
            canvas.clipRect(plotAreaRender.getLeft() - gWidth, plotAreaRender.getTop() - gWidth, plotAreaRender.getRight() + gWidth, plotAreaRender.getBottom() + gWidth);
            canvas.translate(0, offsetY);
            drawClipDataAxisGridlines(canvas);
            canvas.restore();
        } else {
            drawClipDataAxisGridlines(canvas);
        }
        if (PanMode.HORIZONTAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            if (getPlotGrid().isShowHorizontalLines())
                gWidth = this.getPlotGrid().getHorizontalLinePaint().getStrokeWidth();
            //绘制X轴tick和marks
            canvas.save();
            canvas.clipRect(plotAreaRender.getLeft() - gWidth, plotAreaRender.getTop() - gWidth, plotAreaRender.getRight() + gWidth, plotAreaRender.getBottom() + gWidth);
            canvas.translate(offsetX, 0);
            drawClipCategoryAxisGridlines(canvas);
            canvas.restore();
        } else {
            drawClipCategoryAxisGridlines(canvas);
        }
        //设置绘图区显示范围
        canvas.save();

        getClipExt().calc(getType());
        canvas.clipRect(plotAreaRender.getLeft() - getClipExt().getExtLeft(), plotAreaRender.getTop() - getClipExt().getExtTop(), plotAreaRender.getRight() + getClipExt().getExtRight(), plotAreaRender.getBottom() + getClipExt().getExtBottom());
        canvas.save();
        canvas.translate(moveX, moveY);
        //绘图
        drawClipPlot(canvas);
        canvas.restore();
        canvas.restore();
        //还原绘图区绘制
        canvas.restore(); //clip
        //轴线
        drawClipAxisLine(canvas);
        //轴刻度
        if (PanMode.VERTICAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            //绘制Y轴tick和marks
            canvas.save();
            canvas.clipRect(this.getLeft(), this.getTop() + yMargin, this.getRight(), this.getBottom() - yMargin);
            canvas.translate(0, offsetY);
            drawClipDataAxisTickMarks(canvas);
            canvas.restore();
        } else {
            drawClipDataAxisTickMarks(canvas);
        }
        if (PanMode.HORIZONTAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            //绘制X轴tick和marks
            canvas.save();
            canvas.clipRect(this.getLeft() + xMargin, this.getTop(), this.getRight() - xMargin, this.getBottom()); //this.getRight() + xMargin
            canvas.translate(offsetX, 0);
            drawClipCategoryAxisTickMarks(canvas);
            canvas.restore();
        } else {
            drawClipCategoryAxisTickMarks(canvas);
        }
        //图例
        drawClipLegend(canvas);
        return true;
    }

    protected boolean drawClipHorizontalPlot(Canvas canvas) {
        //显示绘图区rect
        float offsetX = translateXY[0];
        float offsetY = translateXY[1];
        initializeMoveXY();
        float yMargin = getClipYMargin();
        float xMargin = getClipXMargin();
        float gWidth = 0.0f;
        drawClipAxisClosed(canvas);
        //设置图显示范围
        canvas.save();
        canvas.clipRect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        if (PanMode.VERTICAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            if (getPlotGrid().isShowVerticalLines())
                gWidth = this.getPlotGrid().getVerticalLinePaint().getStrokeWidth();
            //绘制Y轴tick和marks
            canvas.save();
            canvas.clipRect(plotAreaRender.getLeft() - gWidth, plotAreaRender.getTop() - gWidth, plotAreaRender.getRight() + gWidth, plotAreaRender.getBottom() + gWidth);
            canvas.translate(0, offsetY);
            drawClipCategoryAxisGridlines(canvas);
            canvas.restore();
        } else {
            drawClipCategoryAxisGridlines(canvas);
        }
        if (PanMode.HORIZONTAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            if (getPlotGrid().isShowHorizontalLines())
                gWidth = this.getPlotGrid().getHorizontalLinePaint().getStrokeWidth();
            //绘制X轴tick和marks
            canvas.save();
            canvas.clipRect(plotAreaRender.getLeft() - gWidth, plotAreaRender.getTop() - gWidth, plotAreaRender.getRight() + gWidth, plotAreaRender.getBottom() + gWidth);
            canvas.translate(offsetX, 0);
            drawClipDataAxisGridlines(canvas);
            canvas.restore();
        } else {
            drawClipDataAxisGridlines(canvas);
        }
        //设置绘图区显示范围
        canvas.save();
        getClipExt().calc(getType());
        canvas.clipRect(plotAreaRender.getLeft() - getClipExt().getExtLeft(), plotAreaRender.getTop() - getClipExt().getExtTop(), plotAreaRender.getRight() + getClipExt().getExtRight(), plotAreaRender.getBottom() + getClipExt().getExtBottom());
        canvas.save();
        canvas.translate(moveX, moveY);
        //绘图
        drawClipPlot(canvas);
        canvas.restore();
        canvas.restore();
        //还原绘图区绘制
        canvas.restore(); //clip
        //轴线
        drawClipAxisLine(canvas);
        if (PanMode.HORIZONTAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            //绘制X轴tick和marks
            canvas.save();
            //放开，排除掉border的宽度
            canvas.clipRect(this.getLeft() + xMargin, this.getTop(), this.getRight() - xMargin, this.getBottom());
            canvas.translate(offsetX, 0);
            drawClipDataAxisTickMarks(canvas);
            canvas.restore();
        } else {
            drawClipDataAxisTickMarks(canvas);
        }
        if (PanMode.VERTICAL == this.getPlotPanMode() || PanMode.FREE == this.getPlotPanMode()) {
            //绘制Y轴tick和marks
            canvas.save();
            canvas.clipRect(this.getLeft(), this.getTop() + yMargin, this.getRight(), this.getBottom() - yMargin);
            canvas.translate(0, offsetY);
            drawClipCategoryAxisTickMarks(canvas);
            canvas.restore();
        } else {
            drawClipCategoryAxisTickMarks(canvas);
        }
        //图例
        drawClipLegend(canvas);
        return true;
    }

    @Override
    protected boolean postRender(Canvas canvas) {
        super.postRender(canvas);
        boolean result = true;
        //计算主图表区范围
        calcPlotRange();
        //画Plot Area背景
        plotAreaRender.render(canvas);
        //绘制图表
        if (getPanModeStatus()) {
            switch (direction) {
                case HORIZONTAL:
                    result = drawClipHorizontalPlot(canvas);
                    break;
                case VERTICAL:
                    result = drawClipVerticalPlot(canvas);
                    break;
            }
        } else {
            result = drawFixedPlot(canvas);
        }
        if (!result) return result;

        //绘制标题
        renderTitle(canvas);
        //绘制轴标题
        if (null != axisTitleRender) {
            axisTitleRender.setRange(this);
            axisTitleRender.render(canvas);
        }
        //显示焦点
        renderFocusShape(canvas);
        //响应提示
        renderToolTip(canvas);
        return result;
    }

    public class ClipExt {
        //用于扩展clip绘图区范围
        private float mClipExtLeft = -1f;
        private float mClipExtTop = -1f;
        private float mClipExtRight = -1f;
        private float mClipExtBottom = -1f;
        private float clipExtLeft = 0.5f;
        private float clipExtTop = 0.5f;
        private float clipExtRight = 0.5f;
        private float clipExtBottom = 0.5f;

        public ClipExt() {}

        /**
         * 指定绘图区clip时向左扩展范围
         *
         * @param value 范围值
         */
        public void setExtLeft(float value) {
            mClipExtLeft = value;
        }

        /**
         * 指定绘图区clip时向上扩展范围
         *
         * @param value 范围值
         */
        public void setExtTop(float value) {
            mClipExtTop = value;
        }

        /**
         * 指定绘图区clip时向右扩展范围
         *
         * @param value 范围值
         */
        public void setExtRight(float value) {
            mClipExtRight = value;
        }

        /**
         * 指定绘图区clip时向下扩展范围
         *
         * @param value 范围值
         */
        public void setExtBottom(float value) {
            mClipExtBottom = value;
        }

        /**
         * 用于计算实际扩展值
         *
         * @param type 图类型
         */
        public void calc(ChartType type) {
            switch (type) {
                case LINE:
                case SPLINE:
                case AREA:
                    if (Float.compare(mClipExtLeft, -1f) == 0) {
                        clipExtLeft = 10.f;
                    } else {
                        clipExtLeft = mClipExtLeft;
                    }
                    if (Float.compare(mClipExtTop, -1f) == 0) {
                        clipExtTop = 0.5f;
                    } else {
                        clipExtTop = mClipExtTop;
                    }
                    if (Float.compare(mClipExtRight, -1f) == 0) {
                        clipExtRight = 10.f;
                    } else {
                        clipExtRight = mClipExtRight;
                    }
                    if (Float.compare(mClipExtBottom, -1f) == 0) {
                        clipExtBottom = 10.f;
                    } else {
                        clipExtBottom = mClipExtBottom;
                    }
                    break;
                default:
                    break;
            }
        }

        /**
         * 返回绘图区clip时实际向左扩展范围
         *
         * @return 扩展值
         */
        public float getExtLeft() {
            return clipExtLeft;
        }

        /**
         * 返回绘图区clip时实际向上扩展范围
         *
         * @return 扩展值
         */
        public float getExtTop() {
            return clipExtTop;
        }

        /**
         * 返回绘图区clip时实际向右扩展范围
         *
         * @return 扩展值
         */
        public float getExtRight() {
            return clipExtRight;
        }

        /**
         * 返回绘图区clip时实际向下扩展范围
         *
         * @return 扩展值
         */
        public float getExtBottom() {
            return clipExtBottom;
        }

    }
}
