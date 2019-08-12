package com.hynet.heebit.components.widget.chart.renderer.info;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.constant.DynamicBorderStyle;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDotRender;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;

import java.util.ArrayList;

public class DynamicBorder {

    private Paint borderPaint = null;
    private Paint backgroundPaintBackground = null;
    private RectF rectF = new RectF();
    private float rowSpan = 5.0f;
    private float colSpan = 10.0f;
    private float margin = 5.f;

    private DynamicBorderStyle mStyle = DynamicBorderStyle.ROUNDRECT; //0 rect, 1 roundRect
    private float roundRectX = 5.f;
    private float roundRectY = 5.f;

    private ArrayList<PlotDot> plotDots = null;
    private ArrayList<String> clickedText = null;
    private ArrayList<Paint> clickedPaint = null;
    protected PointF centerXY = null;
    protected Align align = Align.RIGHT;
    private float rectWidth = 0.0f;
    private float rectHeight = 0.0f;
    //带箭头的框中，箭头的高度
    protected float scale = 0.2f;
    //圆框半径
    protected float radius = 0.f;
    protected boolean showBoxBorder = true;
    protected boolean showBackground = true;

    public DynamicBorder() {

    }

    public Paint getBorderPaint() {
        if (null == borderPaint) {
            borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Style.STROKE);
        }
        return borderPaint;
    }

    /**
     * 开放背景画笔
     *
     * @return 画笔
     */
    public Paint getBackgroundPaint() {
        if (null == backgroundPaintBackground) {
            backgroundPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaintBackground.setAlpha(100);
            backgroundPaintBackground.setColor(Color.YELLOW);
        }
        return backgroundPaintBackground;
    }

    private boolean validateParams() {
        if (null == centerXY) {
             LogUtil.Companion.getInstance().print("没有传入点击坐标.");
            return false;
        }
        if (null == clickedPaint) {
             LogUtil.Companion.getInstance().print("没有传入画笔.");
            return false;
        }
        return true;
    }

    private void getContentRect() {
        int countDots = (null != plotDots) ? plotDots.size() : 0;
        int countPaint = (null != clickedPaint) ? clickedPaint.size() : 0;
        int countText = (null != clickedPaint) ? clickedPaint.size() : 0;
        //if(0 == countText && 0 == countDots ) return;
        float textWidth = 0.0f, textHeight = 0.0f;
        float maxWidth = 0.0f;
        float maxHeight = 0.0f;
        float rowWidth = 0.0f;
        Paint paint = null;
        String text;
        for (int i = 0; i < countText; i++) {
            if (countPaint > i) paint = clickedPaint.get(i);
            if (null == paint) break;
            text = clickedText.get(i);
            textHeight = DrawUtil.getInstance().getPaintFontHeight(paint);
            textWidth = DrawUtil.getInstance().getTextWidth(paint, text);

            rowWidth = textWidth;
            if (countDots > i) {
                PlotDot plot = plotDots.get(i);
                if (plot.getDotStyle() != DotStyle.HIDE) {
                    rowWidth += textHeight + colSpan;
                }
            }
            if (Float.compare(rowWidth, maxWidth) == 1) {
                maxWidth = rowWidth;
            }
            maxHeight += textHeight;
        }
        //paint.reset();
        maxHeight += 2 * margin + countText * rowSpan;
        maxWidth += 2 * margin;
        rectWidth = maxWidth;
        rectHeight = maxHeight;
        getInfoRect();
    }

    /**
     * 设置行间距
     *
     * @param span 间距
     */
    public void setRowSpan(float span) {
        rowSpan = span;
    }

    public void setColSpan(float span) {
        colSpan = span;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }

    public void setRoundRectX(float x) {
        roundRectX = x;
    }

    public void setRoundRectY(float y) {
        roundRectY = y;
    }

    protected void setCenterXY(float x, float y) {
        if (null == centerXY) centerXY = new PointF();
        centerXY.x = x;
        centerXY.y = y;
    }

    public void setStyle(DynamicBorderStyle style) {
        mStyle = style;
    }

    protected void addInfo(String text, Paint paint) {
        PlotDot dot = new PlotDot();
        dot.setDotStyle(DotStyle.HIDE);
        addInfo(dot, text, paint);
    }

    protected void addInfo(PlotDot dotStyle, String text, Paint paint) {
        if (null == plotDots) plotDots = new ArrayList<>();
        if (null == clickedText) clickedText = new ArrayList<>();
        if (null == clickedPaint) clickedPaint = new ArrayList<>();
        plotDots.add(dotStyle);
        clickedText.add(text);
        clickedPaint.add(paint);
    }

    //hint  ToolTips
    protected void drawInfo(Canvas canvas) {
        if (!validateParams()) return;
        int countDots = (null != plotDots) ? plotDots.size() : 0;
        int countPaint = (null != clickedPaint) ? clickedPaint.size() : 0;
        int countText = (null != clickedPaint) ? clickedPaint.size() : 0;
        if (0 == countText && 0 == countDots) return;
        getContentRect();
        if (null == rectF) return;
        if (DynamicBorderStyle.RECT == mStyle) {
            if (showBackground) canvas.drawRect(rectF, this.getBackgroundPaint());
            if (showBoxBorder) canvas.drawRect(rectF, this.getBorderPaint());
        } else if (DynamicBorderStyle.CAPRECT == mStyle) {
            renderCapRect(canvas, rectF);
        } else if (DynamicBorderStyle.CAPROUNDRECT == mStyle) {
            renderCapRound(canvas, rectF);
        } else if (DynamicBorderStyle.CIRCLE == mStyle) {
            renderCircle(canvas, rectF);
        } else {
            if (showBackground)
                canvas.drawRoundRect(rectF, roundRectX, roundRectY, this.getBackgroundPaint());
            if (showBoxBorder)
                canvas.drawRoundRect(rectF, roundRectX, roundRectY, this.getBorderPaint());
        }
        float currDotsX = rectF.left + margin;
        float currRowY = rectF.top + margin;
        float textHeight = 0.0f;
        float currTextX = currDotsX;
        int j = 0;
        for (int i = 0; i < countText; i++) {
            if (countPaint > i) j = i;
            if (null == clickedPaint.get(j)) break;
            textHeight = DrawUtil.getInstance().getPaintFontHeight(clickedPaint.get(j));
            if (countDots > i) {
                PlotDot plot = plotDots.get(i);
                //画dot
                if (plot.getDotStyle() != DotStyle.HIDE) {
                    PlotDotRender.getInstance().renderDot(canvas, plot, currDotsX + (textHeight / 2), currRowY + (textHeight / 2), clickedPaint.get(j));
                    currTextX = currDotsX + textHeight + colSpan;
                }
            }
            if (countText > i)
                DrawUtil.getInstance().drawText(canvas, clickedPaint.get(j), clickedText.get(i), currTextX, currRowY + textHeight);
            currRowY += textHeight + rowSpan;
            currTextX = currDotsX;
        }
    }

    /**
     * 带箭头的标识框中，其箭头的高度(占整宽度的比例)
     *
     * @param scale 比例
     */
    public void setCapBoxAngleHeight(float scale) {
        this.scale = scale;
    }

    private void renderCapRect(Canvas canvas, RectF rect) {
        if (!showBackground && !showBoxBorder) return;
        float AngleH = rect.width() * scale; //0.2f ;
        rect.top -= AngleH;
        rect.bottom -= AngleH;
        float centerX = rect.left + rect.width() * 0.5f;
        float AngleY = rect.bottom;
        Path path = new Path();
        path.moveTo(rect.left, rect.bottom);
        path.lineTo(rect.left, rect.top);
        path.lineTo(rect.right, rect.top);
        path.lineTo(rect.right, rect.bottom);
        path.lineTo(centerX + AngleH, AngleY);
        path.lineTo(centerX, AngleY + AngleH);
        path.lineTo(centerX - AngleH, AngleY);
        path.close();
        if (showBackground) canvas.drawPath(path, this.getBackgroundPaint());
        if (showBoxBorder) canvas.drawPath(path, this.getBorderPaint());
    }

    private void renderCapRound(Canvas canvas, RectF rect) {
        if (!showBackground) return;        //此风格无边框
        float AngleH = rect.width() * scale; //0.2f ;
        rect.top -= AngleH;
        rect.bottom -= AngleH;
        float centerX = rect.left + rect.width() * 0.5f;
        float AngleY = rect.bottom;
        float fh = DrawUtil.getInstance().getPaintFontHeight(getBackgroundPaint());
        Path path = new Path();
        path.moveTo(centerX + AngleH, AngleY - fh);
        path.lineTo(centerX, AngleY + AngleH);
        path.lineTo(centerX - AngleH, AngleY - fh);
        path.close();
        canvas.drawRoundRect(rectF, roundRectX, roundRectY, getBackgroundPaint());
        canvas.drawPath(path, this.getBackgroundPaint());
        path.reset();
    }

    /**
     * 圆形标识框中，其半径长度
     *
     * @param radius 半径
     */
    public void setCircleBoxRadius(float radius) {
        this.radius = radius;
    }

    private void renderCircle(Canvas canvas, RectF rect) {
        float radius = Math.max(rect.width(), rect.height()) / 2 + 5;
        if (Float.compare(this.radius, 0.0f) != 0) radius = this.radius;
        if (showBackground)
            canvas.drawCircle(rect.centerX(), rect.centerY(), radius, this.getBackgroundPaint());
        if (showBoxBorder)
            canvas.drawCircle(rect.centerX(), rect.centerY(), radius, this.getBorderPaint());
    }

    protected void clear() {
        if (null != plotDots) plotDots.clear();
        if (null != clickedText) clickedText.clear();
        if (null != clickedPaint) clickedPaint.clear();
    }

    private void getInfoRect() {
        switch (align) {
            case LEFT:
                rectF.left = centerXY.x - rectWidth;
                rectF.right = centerXY.x;
                rectF.top = centerXY.y - rectHeight;
                rectF.bottom = centerXY.y;
                break;
            case CENTER:
                float halfWidth = rectWidth / 2;
                rectF.left = centerXY.x - halfWidth;
                rectF.right = centerXY.x + halfWidth;
                rectF.top = centerXY.y - rectHeight;
                rectF.bottom = centerXY.y;
                break;
            case RIGHT:
                rectF.left = centerXY.x;
                rectF.right = centerXY.x + rectWidth;
                rectF.top = centerXY.y - rectHeight;
                rectF.bottom = centerXY.y;
                break;
            default:
                break;
        }
    }

    /**
     * 不显示标签边框
     */
    public void hideBorder() {
        showBoxBorder = false;
    }

    /**
     * 不显示标签背景
     */
    public void hideBackground() {
        showBackground = false;
    }


    /**
     * 显示标签边框
     */
    public void showBorder() {
        showBoxBorder = true;
    }

    /**
     * 显示图背景
     */
    public void showBackground() {
        showBackground = true;
    }
}
