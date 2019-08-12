package com.hynet.heebit.components.widget.chart.renderer;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;

import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.Direction;
import com.hynet.heebit.components.widget.chart.constant.HorizontalAlign;
import com.hynet.heebit.components.widget.chart.constant.PanMode;
import com.hynet.heebit.components.widget.chart.constant.RectType;
import com.hynet.heebit.components.widget.chart.constant.VerticalAlign;
import com.hynet.heebit.components.widget.chart.renderer.info.AnchorDataPoint;
import com.hynet.heebit.components.widget.chart.renderer.info.AnchorRender;
import com.hynet.heebit.components.widget.chart.renderer.info.DynamicLine;
import com.hynet.heebit.components.widget.chart.renderer.info.DynamicLineRender;
import com.hynet.heebit.components.widget.chart.renderer.info.Legend;
import com.hynet.heebit.components.widget.chart.renderer.info.LegendRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.Border;
import com.hynet.heebit.components.widget.chart.renderer.plot.BorderRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotArea;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotAreaRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotGrid;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotGridRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotLegend;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotLegendRender;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotTitle;
import com.hynet.heebit.components.widget.chart.renderer.plot.PlotTitleRender;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class XChart implements IRender {

    // 开放主图表区
    protected PlotAreaRender plotAreaRender = null;
    // 开放主图表区网格
    protected PlotGridRender plotGridRender = null;
    // 标题栏
    private PlotTitleRender plotTitleRender = null;
    // 图大小范围
    private float left = 0.0f;
    private float top = 0.0f;
    private float right = 0.0f;
    private float bottom = 0.0f; //5f;
    // 图宽高
    private float width = 0.0f;
    private float height = 0.0f;
    // 图的内边距属性
    private float paddingTop = 0.f;
    private float paddingBottom = 0.f;
    private float paddingLeft = 0.f;
    private float paddingRight = 0.f;
    // 是否画背景色
    private boolean backgroundColorVisible = false;
    //坐标系原点坐标
    protected float[] translateXY = new float[2];
    //是否显示边框
    private boolean showBorder = false;
    private BorderRender borderRender = null;
    //图例类
    protected PlotLegendRender plotLegendRender = null;
    //动态图例
    private LegendRender legendRender = null;
    private boolean enableScale = true;
    private float xScale = 0.0f, mYScale = 0.0f;
    private float centerX = 0.0f, mCenterY = 0.0f;
    //是否显示十字交叉线
    private boolean dynamicLineVisible = false;
    private DynamicLineRender dynamicLineRender = null;
    //是否平移
    protected boolean enablePanMode = true;
    //平移模式下的可移动方向
    private PanMode panMode = PanMode.FREE;
    //限制图表滑动范围
    private boolean controlPanRange = true;


    public XChart() {
        initializeChart();
    }

    private void initializeChart() {
        //默认的原点坐标
        translateXY[0] = 0.0f;
        translateXY[1] = 0.0f;
        //图例
        if (null == plotLegendRender) plotLegendRender = new PlotLegendRender(this);
        // 图表
        if (null == plotAreaRender) plotAreaRender = new PlotAreaRender();
        if (null == plotGridRender) plotGridRender = new PlotGridRender();
        if (null == plotTitleRender) plotTitleRender = new PlotTitleRender();
    }

    /**
     * 返回当前绘制的是什么类型的图
     *
     * @return 类型
     */
    public ChartType getType() {
        return ChartType.NONE;
    }


    /**
     * 开放图例基类
     *
     * @return 基类
     */
    public PlotLegend getPlotLegend() {
        //图例
        if (null == plotLegendRender) plotLegendRender = new PlotLegendRender(this);
        return plotLegendRender;
    }

    /**
     * 用于指定绘图区与图范围的内边距。单位为PX值. 即用于确定plotArea范围
     *
     * @param left   绘图区与图左边的保留宽度，用于显示左边轴及轴标题之类
     * @param top    绘图区与图顶部的保留距离，用于显示标题及图例之类
     * @param right  绘图区与图右边的保留宽度，用于显示右边轴及轴标题之类
     * @param bottom 绘图区与图底部的保留距离，用于显示底轴及轴标题之类
     */
    public void setPadding(float left, float top, float right, float bottom) {
        if (top > 0)
            paddingTop = top;
        if (bottom > 0)
            paddingBottom = bottom;
        if (left > 0)
            paddingLeft = left;
        if (right > 0)
            paddingRight = right;
    }


    /**
     * 返回主图表区基类
     *
     * @return 主图表区基类
     */
    public PlotArea getPlotArea() {
        if (null == plotAreaRender) plotAreaRender = new PlotAreaRender();
        return plotAreaRender;
    }

    /**
     * 返回主图表区网格基类
     *
     * @return 网格基类
     */
    public PlotGrid getPlotGrid() {
        if (null == plotGridRender) plotGridRender = new PlotGridRender();
        return plotGridRender;
    }

    /**
     * 返回图的标题基类
     *
     * @return 标题基类
     */
    public PlotTitle getPlotTitle() {
        if (null == plotTitleRender) plotTitleRender = new PlotTitleRender();
        return plotTitleRender;
    }

    /**
     * 设置图表绘制范围.
     *
     * @param width  图表宽度
     * @param height 图表高度
     */
    public void setChartRange(float width, float height) {
        setChartRange(0.0f, 0.0f, width, height);
    }


    /**
     * 设置图表绘制范围,以指定起始点及长度方式确定图表大小.
     *
     * @param startX 图表起点X坐标
     * @param startY 图表起点Y坐标
     * @param width  图表宽度
     * @param height 图表高度
     */
    public void setChartRange(float startX, float startY, float width, float height) {
        if (startX > 0)
            left = startX;
        if (startY > 0)
            top = startY;
        right = add(startX, width);
        bottom = add(startY, height);
        if (Float.compare(width, 0.0f) > 0) this.width = width;
        if (Float.compare(height, 0.0f) > 0) this.height = height;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (null != plotTitleRender) plotTitleRender.setTitle(title);
    }

    /**
     * 设置子标题
     *
     * @param subtitle 子标题
     */
    public void addSubtitle(String subtitle) {
        if (null != plotTitleRender) plotTitleRender.setSubtitle(subtitle);
    }

    /**
     * 设置标题上下显示位置,即图上边距与绘图区间哪个位置(靠上，居中，靠下).
     *
     * @param position 显示位置
     */
    public void setTitleVerticalAlign(VerticalAlign position) {
        if (null != plotTitleRender) plotTitleRender.setVerticalAlign(position);
    }

    /**
     * 设置标题横向显示位置(靠左，居中，靠右)
     *
     * @param align 显示位置
     */
    public void setTitleAlign(HorizontalAlign align) {
        if (null != plotTitleRender) plotTitleRender.setTitleAlign(align);
    }


    /**
     * 返回图表左边X坐标
     *
     * @return 左边X坐标
     */
    public float getLeft() {
        return left;
    }

    /**
     * 返回图表上方Y坐标
     *
     * @return 上方Y坐标
     */
    public float getTop() {
        return top;
    }

    /**
     * 返回图表右边X坐标
     *
     * @return 右边X坐标
     */
    public float getRight() {
        return right;
    }

    /**
     * 返回图表底部Y坐标
     *
     * @return 底部Y坐标
     */
    public float getBottom() {
        return bottom;
    }

    /**
     * 返回图表宽度
     *
     * @return 宽度
     */
    public float getWidth() {

        return width;
    }

    /**
     * 返回图表高度
     *
     * @return 高度
     */
    public float getHeight() {
        return height;
    }

    /**
     * 返回图绘制区相对图顶部边距的高度
     *
     * @return 绘图区与图边距间的PX值
     */
    public float getPaddingTop() {
        return this.paddingTop;
    }

    /**
     * 返回图绘制区相对图底部边距的高度
     *
     * @return 绘图区与图边距间的PX值
     */
    public float getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * 图绘制区相对图左边边距的宽度
     *
     * @return 绘图区与图边距间的PX值
     */
    public float getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * 图绘制区相对图右边边距的宽度
     *
     * @return 绘图区与图边距间的PX值
     */
    public float getPaddingRight() {
        return paddingRight;
    }

    /**
     * 返回图中心点坐标
     *
     * @return 坐标
     */
    public PointF getCenterXY() {
        PointF point = new PointF();
        point.x = this.getLeft() + div(this.getWidth(), 2f);
        point.y = this.getTop() + div(this.getHeight(), 2f);
        return point;
    }


    /**
     * 设置绘画时的坐标系原点位置
     *
     * @param x 原点x位置
     * @param y 原点y位置
     */
    public void setTranslateXY(float x, float y) {
        if (!enablePanMode) return;
        if (null == translateXY) translateXY = new float[2];
        translateXY[0] = x;
        translateXY[1] = y;
    }

    /**
     * 返回坐标系原点坐标
     *
     * @return 原点坐标
     */
    public float[] getTranslateXY() {
        return translateXY;
    }

    /**
     * 计算图的显示范围,依屏幕px值来计算.
     */
    protected void calcPlotRange() {
        int borderWidth = getBorderWidth();
        if (null == plotAreaRender) return;
        plotAreaRender.setBottom(sub(this.getBottom() - borderWidth / 2, paddingBottom));
        plotAreaRender.setLeft(add(this.getLeft() + borderWidth / 2, paddingLeft));
        plotAreaRender.setRight(sub(this.getRight() - borderWidth / 2, paddingRight));
        plotAreaRender.setTop(add(this.getTop() + borderWidth / 2, paddingTop));
    }

    /**
     * 绘制标题
     */
    protected void renderTitle(Canvas canvas) {
        int borderWidth = getBorderWidth();
        if (null == plotTitleRender) return;
        this.plotTitleRender.renderTitle(left + borderWidth, right - borderWidth, top + borderWidth, width, this.plotAreaRender.getTop(), canvas);
    }


    /**
     * 绘制批注
     *
     * @param anchorDataPoints 批注集合
     * @param dataID           主数据集id
     * @param childID          子数据集id
     * @param canvas           画布
     * @param x                X坐标点
     * @param y                y坐标点
     *
     * @return 是否有绘制
     */
    protected boolean drawAnchor(List<AnchorDataPoint> anchorDataPoints, int dataID, int childID, Canvas canvas, float x, float y, float radius) {
        if (null == anchorDataPoints || -1 == dataID) return false;
        int count = anchorDataPoints.size();
        float left = getPlotArea().getLeft();
        float right = getPlotArea().getRight();
        float top = getPlotArea().getTop();
        float bottom = getPlotArea().getBottom();
        for (int i = 0; i < count; i++) {
            AnchorDataPoint anchorDataPoint = anchorDataPoints.get(i);
            if (anchorDataPoint.getDataSeriesID() == dataID) {
                if ((-1 == childID || -1 == anchorDataPoint.getDataChildID()) || (-1 != childID && anchorDataPoint.getDataChildID() == childID)) {
                    AnchorRender.getInstance().renderAnchor(canvas, anchorDataPoint, x, y, radius, left, top, right, bottom);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 设置是否绘制背景
     *
     * @param visible 是否绘制背景
     */
    public void setApplyBackgroundColor(boolean visible) {
        backgroundColorVisible = visible;
    }

    /**
     * 设置图的背景色
     *
     * @param color 背景色
     */
    public void setBackgroundColor(int color) {
        getBackgroundPaint().setColor(color);
        getPlotArea().getBackgroundPaint().setColor(color);
        if (null == borderRender) borderRender = new BorderRender();
        borderRender.getBackgroundPaint().setColor(color);
    }

    /**
     * 设置图的渐变背景色
     *
     * @param direction  渐变方向
     * @param beginColor 起始颜色
     * @param endColor   结束颜色
     */
    public void setBackgroundColor(Direction direction, int beginColor, int endColor) {
        if (beginColor == endColor) {
            getBackgroundPaint().setColor(beginColor);
        } else {
            LinearGradient linearGradient;
            if (direction == Direction.VERTICAL) {
                linearGradient = new LinearGradient(0, 0, 0, getBottom() - getTop(), beginColor, endColor, Shader.TileMode.MIRROR);
            } else {
                linearGradient = new LinearGradient(getLeft(), getBottom(), getRight(), getTop(), beginColor, endColor, Shader.TileMode.CLAMP);
            }
            getBackgroundPaint().setShader(linearGradient);
        }
        if (null == borderRender) borderRender = new BorderRender();
        borderRender.getBackgroundPaint().setColor(endColor);
    }


    /**
     * 开放背景画笔
     *
     * @return 画笔
     */
    public Paint getBackgroundPaint() {
        if (null == borderRender) borderRender = new BorderRender();
        return borderRender.getBackgroundPaint();
    }

    /**
     * 显示矩形边框
     */
    public void showBorder() {
        showBorder = true;
        if (null == borderRender) borderRender = new BorderRender();
        borderRender.setBorderRectType(RectType.RECT);
    }

    /**
     * 显示圆矩形边框
     */
    public void showRoundBorder() {
        showBorder = true;
        if (null == borderRender) borderRender = new BorderRender();
        borderRender.setBorderRectType(RectType.ROUNDRECT);
    }

    /**
     * 隐藏边框
     */
    public void hideBorder() {
        showBorder = false;
        if (null != borderRender) borderRender = null;
    }

    /**
     * 开放边框绘制类
     *
     * @return 边框绘制类
     */
    public Border getBorder() {
        if (null == borderRender) borderRender = new BorderRender();
        return borderRender;
    }

    /**
     * 是否显示边框
     *
     * @return 是否显示
     */
    public boolean isShowBorder() {
        return showBorder;
    }

    /**
     * 得到边框宽度,默认为5px
     *
     * @return 边框宽度
     */
    public int getBorderWidth() {
        int borderWidth = 0;
        if (showBorder) {
            if (null == borderRender) borderRender = new BorderRender();
            borderWidth = borderRender.getBorderWidth();
        }
        return borderWidth;
    }

    /**
     * 设置边框宽度
     *
     * @param width 边框宽度
     */
    public void setBorderWidth(int width) {
        if (0 >= width) return;
        if (null == borderRender) borderRender = new BorderRender();
        borderRender.setRoundRadius(width);
    }

    /**
     * 绘制边框
     *
     * @param canvas 画布
     */
    protected void renderBorder(Canvas canvas) {
        if (showBorder) {
            if (null == borderRender) borderRender = new BorderRender();
            borderRender.renderBorder("BORDER", canvas, left, top, right, bottom);
        }
    }

    /**
     * 绘制图的背景
     */
    protected void renderChartBackground(Canvas canvas) {
        if (this.backgroundColorVisible) {
            if (null == borderRender) borderRender = new BorderRender();
            if (showBorder) {
                borderRender.renderBorder("CHART", canvas, left, top, right, bottom);
            } else { //要清掉 border的默认间距
                int borderSpadding = borderRender.getBorderSpadding();
                borderRender.renderBorder("CHART", canvas, left - borderSpadding, top - borderSpadding, right + borderSpadding, bottom + borderSpadding);
            }
        }
    }


    /**
     * 设置缩放参数
     *
     * @param xScale  x方向缩放比例
     * @param yScale  y方向缩放比例
     * @param centerX 缩放中心点x坐标
     * @param centerY 缩放中心点y坐标
     */
    public void setScale(float xScale, float yScale, float centerX, float centerY) {
        this.xScale = xScale;
        mYScale = yScale;
        this.centerX = centerX;
        mCenterY = centerY;
    }

    protected boolean getClikedScaleStatus() {
        if (!enableScale) return true;
        if (Float.compare(xScale, 0.0f) == 0) return true;
        //如果在这范围内，则可以处理点击
        if (Float.compare(xScale, 0.95f) == 1 && Float.compare(xScale, 1.1f) == -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 缩放图表
     *
     * @param canvas 画布
     */
    private void scaleChart(Canvas canvas) {
        if (!enableScale) return;
        if (Float.compare(centerX, 0.0f) == 1 || Float.compare(mCenterY, 0.0f) == 1) {
            canvas.scale(xScale, mYScale, centerX, mCenterY);
            //}else{
            //canvas.scale(scale, scale,plotAreaRender.getCenterX(),plotAreaRender.getCenterY());					
        }
    }

    /**
     * 激活图表缩放(但注意，图表缩放后，如果有同时激活click事件，
     * 则缩放状态下，点击处理无效。
     */
    public void enableScale() {
        enableScale = true;
    }

    /**
     * 禁用图表缩放
     */
    public void disableScale() {
        enableScale = false;
    }

    /**
     * 返回图表缩放状态
     *
     * @return 缩放状态
     */
    public boolean getScaleStatus() {
        return enableScale;
    }


    /**
     * 设置手势平移模式
     *
     * @param mode 平移模式
     */
    public void setPlotPanMode(PanMode mode) {
        panMode = mode;
    }

    /**
     * 返回当前图表平移模式
     *
     * @return 平移模式
     */
    public PanMode getPlotPanMode() {
        return panMode;
    }

    /**
     * 激活平移模式
     */
    public void enablePanMode() {
        enablePanMode = true;
    }

    /**
     * 禁用平移模式
     */
    public void disablePanMode() {
        enablePanMode = false;
    }

    /**
     * 限制图表滑动范围,开启则图表不能滑动出可见范围
     */
    public void enabledCtlPanRange() {
        controlPanRange = true;
    }

    /**
     * 不限制图表滑动范围
     */
    public void disabledCtlPanRange() {
        controlPanRange = false;
    }

    /**
     * '是否有限制图表滑动范围
     *
     * @return 状态
     */
    public boolean getCtlPanRangeStatus() {
        return controlPanRange;
    }

    /**
     * 返回当前图表的平移状态
     *
     * @return 平移状态
     */
    public boolean getPanModeStatus() {
        return enablePanMode;
    }

    /**
     * 返回动态图例类，当默认的图例不合需求时，可以用来应付一些特殊格式
     *
     * @return 动态图例
     */
    public Legend getDyLegend() {
        if (null == legendRender) legendRender = new LegendRender();
        return legendRender;
    }


    /**
     * 绘制十字交叉线
     */
    public void showDyLine() {
        dynamicLineVisible = true;
    }

    /**
     * 不绘制十字交叉线
     */
    public void hideDyLine() {
        dynamicLineVisible = false;
    }

    /**
     * 返回是否显示十字交叉线
     *
     * @return 是否显示
     */
    public boolean getDynamicLineVisible() {
        return dynamicLineVisible;
    }

    /**
     * 开放十字交叉线绘制基类
     *
     * @return 交叉线绘制基类
     */
    public DynamicLine getDynamicLine() {
        if (null == dynamicLineRender) dynamicLineRender = new DynamicLineRender();
        return dynamicLineRender;
    }

    //交叉线
    private void drawDynamicLine(Canvas canvas) {
        if (!dynamicLineVisible) return;
        if (null == dynamicLineRender) dynamicLineRender = new DynamicLineRender();
        dynamicLineRender.renderLine(canvas, plotAreaRender.getLeft(), plotAreaRender.getTop(), plotAreaRender.getRight(), plotAreaRender.getBottom());
    }

    private void drawDyLegend(Canvas canvas) {
        //动态图例
        if (null != legendRender) {
            legendRender.setPlotWH(this.getWidth(), this.getHeight());
            legendRender.renderInfo(canvas);
        }
    }

    /**
     * 用于延迟绘制
     *
     * @param canvas 画布
     *
     * @return 是否成功
     *
     * @throws Exception 例外
     */
    protected boolean postRender(Canvas canvas) {
        // 绘制图背景
        renderChartBackground(canvas);
        return true;
    }


    @Override
    public boolean render(Canvas canvas) {
        boolean result = true;
        if (null == canvas)
            return false;
        canvas.save();
        //缩放图表
        scaleChart(canvas);
        //绘制图表
        result = postRender(canvas);
        //绘制边框
        renderBorder(canvas);
        //动态图例
        drawDyLegend(canvas);
        //十字交叉线
        drawDynamicLine(canvas);
        canvas.restore();
        return result;
    }


    //math计算类函数 ----------------------------------------------------------------

    /**
     * Java是无法精确计算小数后面的，激活后会
     * 忽略Java数据计算时的误差。能提高绘制性能，(但饼图类图表慎用)
     */
    public void disableHighPrecision() {
        MathUtil.getInstance().disableHighPrecision();
    }

    /**
     * 激活Java数据精确计算，考虑计算的误差。
     */
    public void enabledHighPrecision() {
        MathUtil.getInstance().enabledHighPrecision();
    }

    /**
     * 加法运算
     *
     * @param v1 参数1
     * @param v2 参数2
     *
     * @return 结果
     */
    protected float add(float v1, float v2) {
        return MathUtil.getInstance().add(v1, v2);
    }

    /**
     * 减法运算
     *
     * @param v1 参数1
     * @param v2 参数2
     *
     * @return 运算结果
     */
    protected float sub(float v1, float v2) {
        return MathUtil.getInstance().sub(v1, v2);
    }

    /**
     * 乘法运算
     *
     * @param v1 参数1
     * @param v2 参数2
     *
     * @return 运算结果
     */
    protected float mul(float v1, float v2) {
        return MathUtil.getInstance().mul(v1, v2);
    }

    /**
     * 除法运算,当除不尽时，精确到小数点后10位
     *
     * @param v1 参数1
     * @param v2 参数2
     *
     * @return 运算结果
     */
    protected float div(float v1, float v2) {
        return MathUtil.getInstance().div(v1, v2);
    }

    //math计算类函数 ----------------------------------------------------------------

}
