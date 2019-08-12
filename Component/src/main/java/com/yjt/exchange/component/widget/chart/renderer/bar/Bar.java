package com.hynet.heebit.components.widget.chart.renderer.bar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.BarStyle;
import com.hynet.heebit.components.widget.chart.constant.ColumnLabelStyle;
import com.hynet.heebit.components.widget.chart.constant.Direction;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class Bar {

    //确定是横向柱形还是竖向柱形图
    private Direction direction = Direction.VERTICAL;
    private ColumnLabelStyle columnLabelStyle = ColumnLabelStyle.NORMAL;
    //柱形画笔
    private Paint barPaint = null;
    //内部柱形
    private Paint outlineBarPaint = null;
    //文字画笔
    private Paint labelPaint = null;
    //柱形顶上文字偏移量
    private int labelAnchorOffset = 5;
    //柱形顶上文字旋转角度
    private float labelRotateAngle = 0.0f;
    //是否显示柱形顶上文字标签
    private boolean showLabel = false;
    //柱形间距所占比例
    private double barInnerMargin = 0.2f;
    //FlatBar类的有效，3D柱形无效
    private BarStyle barStyle = BarStyle.GRADIENT;
    //柱形所占总比例
    private float barTickSpacePercent = 0.7f;
    //柱子最大宽度
    private float barMaxPxWidth = 0.0f;
    //柱子最大高度
    private float barMaxPxHeight = 0.0f;
    //圆柱形的半径
    protected float roundRaidus = 15.0f;
    protected int outlineAlpha = 150;
    protected int borderWidth = 0;

    public Bar() {

    }

    /**
     * 设置角圆弧半径
     *
     * @param radius 半径
     */
    public void setBarRoundRadius(int radius) {
        roundRaidus = radius;
    }

    /**
     * 返回角圆弧半径
     *
     * @return 半径
     */
    public float getBarRoundRadius() {
        return roundRaidus;
    }

    /**
     * 返回柱形的显示方向
     *
     * @return 显示方向
     */
    public Direction getBarDirection() {
        return direction;
    }

    /**
     * 设置柱形的显示方向
     *
     * @param direction 显示方向
     */
    public void setBarDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * 设置柱形标签显示位置
     *
     * @param style 内/外/中间
     */
    public void setItemLabelStyle(ColumnLabelStyle style) {
        this.columnLabelStyle = style;
    }

    /**
     * 返回柱形标签显示位置
     *
     * @return
     */
    public ColumnLabelStyle getItemLabelStyle() {
        return this.columnLabelStyle;
    }

    /**
     * 开放柱形画笔
     *
     * @return 画笔
     */
    public Paint getBarPaint() {
        if (null == barPaint) {
            barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            barPaint.setColor(Color.rgb(252, 210, 9));
            barPaint.setStyle(Style.FILL);
        }
        return barPaint;
    }

    /**
     * 柱形风格为outline时,内部柱形的画笔
     *
     * @return 画笔
     */
    public Paint getBarOutlinePaint() {
        if (null == outlineBarPaint) {
            outlineBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            outlineBarPaint.setStyle(Style.FILL);
        }
        return outlineBarPaint;
    }

    /**
     * 柱形风格为outline时,内部柱形颜色相对于外围柱形的透明度
     *
     * @param alpha 透明度
     */
    public void setOutlineAlpha(int alpha) {
        outlineAlpha = alpha;
    }

    /**
     * 柱形风格为outline时,外部柱形的宽度
     *
     * @param width 宽度
     */
    public void setBorderWidth(int width) {
        borderWidth = width;
    }

    /**
     * 开放柱形顶部标签画笔
     *
     * @return 画笔
     */
    public Paint getItemLabelPaint() {
        //柱形顶上的文字标签
        if (null == labelPaint) {
            labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            labelPaint.setTextSize(12);
            labelPaint.setColor(Color.BLACK);
            labelPaint.setTextAlign(Align.CENTER);
        }
        return labelPaint;
    }

    /**
     * 返回柱形顶部标签在显示时的偏移距离
     *
     * @return 偏移距离
     */
    public int getItemLabelAnchorOffset() {
        return labelAnchorOffset;
    }

    /**
     * 设置柱形顶部标签在显示时的偏移距离
     *
     * @param offset 偏移距离
     */
    public void setItemLabelAnchorOffset(int offset) {
        this.labelAnchorOffset = offset;
    }

    /**
     * 返回柱形顶部标签在显示时的旋转角度
     *
     * @return 旋转角度
     */
    public float getItemLabelRotateAngle() {
        return labelRotateAngle;
    }

    /**
     * 设置柱形顶部标签在显示时的旋转角度
     *
     * @param rotateAngle 旋转角度
     */
    public void setItemLabelRotateAngle(float rotateAngle) {
        this.labelRotateAngle = rotateAngle;
    }

    /**
     * 设置是否显示柱形顶部标签
     *
     * @param visible 是否显示
     */
    public void setItemLabelVisible(boolean visible) {
        this.showLabel = visible;
    }

    /**
     * 设置所有柱形间占刻度间总空间的百分比(默认为0.7即70%)
     *
     * @param percent 百分比
     */
    public boolean setBarTickSpacePercent(float percent) {
        if (Float.compare(percent, 0.f) == -1) {
             LogUtil.Companion.getInstance().print("此比例不能为负数噢!");
            return false;
        }
        if (Float.compare(percent, 0.f) == 0) {
             LogUtil.Companion.getInstance().print("此比例不能等于0!");
            return false;
        } else {
            barTickSpacePercent = percent;
        }
        return true;
    }


    /**
     * 设置柱形间空白所占的百分比
     *
     * @param percent 百分比
     */
    public boolean setBarInnerMargin(float percent) {
        if (Float.compare(percent, 0.f) == -1) {
             LogUtil.Companion.getInstance().print("此比例不能为负数噢!");
            return false;
        }
        if (Float.compare(percent, 0.9f) == 1 || Float.compare(percent, 0.9f) == 0) {
             LogUtil.Companion.getInstance().print("此比例不能大于等于0.9,要给柱形留下点显示空间!");
            return false;
        } else {
            this.barInnerMargin = percent;
        }
        return true;
    }

    /**
     * 得到柱形间空白所占的百分比
     *
     * @return 百分比
     */
    public double getInnerMargin() {
        return barInnerMargin;
    }


    /**
     * 返回是否显示柱形顶部标签
     *
     * @return 是否显示
     */
    public boolean getItemLabelsVisible() {
        return showLabel;
    }

    /**
     * 设置柱子的最大宽度范围，仅在竖向柱图中有效
     *
     * @param width 最大宽度
     */
    public void setBarMaxPxWidth(float width) {
        barMaxPxWidth = width;
    }

    /**
     * 返回柱子的最大宽度范围，仅在竖向柱图中有效
     *
     * @return 最大宽度
     */
    public float getBarMaxPxWidth() {
        return barMaxPxWidth;
    }

    /**
     * 设置柱子的最大高度范围，仅在横向柱图中有效
     *
     * @param height 最大高度
     */
    public void setBarMaxPxHeight(float height) {
        barMaxPxHeight = height;
    }

    /**
     * 返回柱子的最大高度范围，仅在横向柱图中有效
     *
     * @return 最大高度范围
     */
    public float getBarMaxPxHeight() {
        return barMaxPxHeight;
    }


    /**
     * 计算同标签多柱形时的Y分隔
     *
     * @param YSteps    Y轴步长
     * @param barNumber 柱形个数
     *
     * @return 返回单个柱形的高度及间距
     */
    protected float[] calcBarHeightAndMargin(float YSteps, int barNumber) {
        if (0 == barNumber) {
             LogUtil.Companion.getInstance().print("柱形个数为零.");
            return null;
        }
        float labelBarTotalHeight = MathUtil.getInstance().mul(YSteps, barTickSpacePercent);
        float barTotalInnerMargin = MathUtil.getInstance().mul(labelBarTotalHeight, (float) barInnerMargin);
        float barInnerMargin = MathUtil.getInstance().div(barTotalInnerMargin, barNumber);
        float barHeight = MathUtil.getInstance().div(MathUtil.getInstance().sub(labelBarTotalHeight, barTotalInnerMargin), barNumber);
        float[] floats = new float[2];
        if (Float.compare(barMaxPxHeight, 0.0f) == 1 && Float.compare(barHeight, barMaxPxHeight) == 1) {
            barHeight = barMaxPxHeight;
        }
        floats[0] = barHeight;
        floats[1] = barInnerMargin;
        return floats;
    }


    /**
     * 计算同标签多柱形时的X分隔
     *
     * @param XSteps    X轴步长
     * @param barNumber 柱形个数
     *
     * @return 返回单个柱形的宽度及间距
     */
    protected float[] calcBarWidthAndMargin(float XSteps, int barNumber) {
        if (0 == barNumber) {
             LogUtil.Companion.getInstance().print("柱形个数为零.");
            return null;
        }
        float labelBarTotalWidth = MathUtil.getInstance().mul(XSteps, barTickSpacePercent);
        float barTotalInnerMargin = MathUtil.getInstance().mul(labelBarTotalWidth, (float) barInnerMargin);
        float barTotalWidth = MathUtil.getInstance().sub(labelBarTotalWidth, barTotalInnerMargin);
        float barInnerMargin = MathUtil.getInstance().div(barTotalInnerMargin, barNumber);
        float barWidth = MathUtil.getInstance().div(barTotalWidth, barNumber);
        float[] floats = new float[2];
        if (Float.compare(barMaxPxWidth, 0.0f) == 1 && Float.compare(barWidth, barMaxPxWidth) == 1) {
            barWidth = barMaxPxWidth;
        }
        floats[0] = barWidth;
        floats[1] = barInnerMargin;
        return floats;
    }

    /**
     * 绘制柱形顶部标签
     *
     * @param text   内容
     * @param x      x坐标
     * @param y      y坐标
     * @param canvas 画布
     */
    protected void drawBarItemLabel(String text, float x, float y, Canvas canvas) {
        //在柱形的顶端显示上柱形的当前值			
        if (getItemLabelsVisible() && text.length() > 0) {
            //要依横向还是竖向
            //如果是背向式的，还要看是向上还是向下
            float cx = x;
            float cy = y;
            switch (direction) {
                case VERTICAL:
                    float textHeight = DrawUtil.getInstance().getPaintFontHeight(getItemLabelPaint());
                    // NORMAL,INNER,OUTER
                    switch (columnLabelStyle) {
                        case OUTER:
                            cy -= this.labelAnchorOffset;
                            cy -= textHeight;
                            break;
                        case INNER:
                            cy += this.labelAnchorOffset;
                            cy += textHeight;
                            break;
                        default:
                            cy -= this.labelAnchorOffset;
                    }
                    break;
                case HORIZONTAL:
                    float textWidth = DrawUtil.getInstance().getTextWidth(getItemLabelPaint(), text);
                    switch (columnLabelStyle) {
                        case OUTER:
                            cx += this.labelAnchorOffset;
                            cx += textWidth;
                            break;
                        case INNER:
                            cx -= this.labelAnchorOffset;
                            cx -= textWidth;
                            break;
                        default:
                            cx += this.labelAnchorOffset;
                    }
                    break;
                default:
                    break;
            }
            DrawUtil.getInstance().drawRotateText(text, cx, cy, getItemLabelRotateAngle(), canvas, getItemLabelPaint());
        }
    }

    /**
     * 设置柱形的显示风格，对3D柱形无效
     *
     * @param style 显示风格
     */
    public void setBarStyle(BarStyle style) {
        this.barStyle = style;
    }

    /**
     * 返回当前柱形的显示风格,对3D柱形无效
     *
     * @return 显示风格
     */
    public BarStyle getBarStyle() {
        return this.barStyle;
    }

}
