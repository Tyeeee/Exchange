package com.hynet.heebit.components.widget.chart.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.hynet.heebit.components.constant.Regex;
import com.hynet.heebit.components.widget.chart.constant.LineStyle;
import com.hynet.heebit.components.widget.chart.constant.TriangleDirection;
import com.hynet.heebit.components.widget.chart.constant.TriangleStyle;

import java.util.Random;


public class DrawUtil {

    private static DrawUtil drawUtil;
    private RectF rectF = null;
    private Path path = null;
    private Paint paint = null;

    public DrawUtil() {}

    public static synchronized DrawUtil getInstance() {
        if (drawUtil == null) {
            drawUtil = new DrawUtil();
        }
        return drawUtil;
    }

    public static void releaseInstance() {
        if (drawUtil != null) {
            drawUtil = null;
        }
    }

    private void initializeRectF() {
        if (rectF == null)
            rectF = new RectF();
    }

    private void initializePath() {
        if (path == null) {
            path = new Path();
        } else {
            path.reset();
        }
    }

    private void initializePaint() {
        if (paint == null) {
            paint = new Paint();
        } else {
            paint.reset();
        }
    }

    /**
     * 得到一个随机颜色
     *
     * @return 随机颜色
     */
    public int randomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    /**
     * 通过透明度，算出对应颜色的浅色应当是什么效果
     *
     * @param color 颜色
     * @param alpha 透明度
     *
     * @return 浅色
     */
    public int getLightColor(int color, int alpha) {
        initializePaint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        return paint.getColor();
    }

    /**
     * 得到深色
     *
     * @param color 颜色
     *
     * @return 深色
     */
    public int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] + 0.1f;
        hsv[2] = hsv[2] - 0.1f;
        return Color.HSVToColor(hsv);
    }

    /**
     * 得到单个字的高度
     *
     * @param paint 画笔
     *
     * @return 高度
     */
    public float getPaintFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 得到字符串的宽度
     *
     * @param paint 画笔
     * @param text  字符串
     *
     * @return 宽度
     */
    public float getTextWidth(Paint paint, String text) {
        if (text.length() == 0) return 0.0f;
        //float width = Math.abs(paint.measureText(str, 0, str.length()));		 
        return paint.measureText(text, 0, text.length());
    }

    /**
     * 用于计算文字的竖直累加高度
     *
     * @param paint 画笔
     * @param text  字符串
     *
     * @return 高度
     */
    public float calcTextHeight(Paint paint, String text) {
        if (text.length() == 0) return 0;
        return getPaintFontHeight(paint) * text.length();
    }

    /**
     * 绘制旋转了指定角度的文字
     *
     * @param text  文字
     * @param x     X坐标
     * @param y     y坐标
     * @param paint 画笔
     * @param angle 角度
     */
    public void drawRotateText(String text, float x, float y, float angle, Canvas canvas, Paint paint) {
        if (TextUtils.isEmpty(text)) return;
        if (angle != 0) {
            canvas.rotate(angle, x, y);
            //canvas.drawText(text, x, y, paint);
            drawText(canvas, paint, text, x, y);
            canvas.rotate(-1 * angle, x, y);
        } else {
            //canvas.drawText(text, x, y, paint);
            drawText(canvas, paint, text, x, y);
        }
    }

    /**
     * 绘制等腰三角形
     *
     * @param baseLine          底线长度
     * @param baseLnCentX       底线中心点X坐标
     * @param baseLnCentY       底线中心点Y坐标
     * @param triangleDirection 三角形方向
     * @param triangleStyle     填充风格
     * @param canvas            画布
     * @param paint             画笔
     */
    public void drawTrigangle(float baseLine, float baseLnCentX, float baseLnCentY, TriangleDirection triangleDirection, TriangleStyle triangleStyle, Canvas canvas, Paint paint) {
        // 计算偏移量
        int offset = (int) (baseLine / 2 * Math.tan(60 * Math.PI / 180));
        initializePath();
        // 计算三角形3个顶点的坐标
        switch (triangleDirection) {
            case UP: //向上
                path.moveTo(baseLnCentX - baseLine / 2, baseLnCentY);
                path.lineTo(baseLnCentX + baseLine / 2, baseLnCentY);
                path.lineTo(baseLnCentX, baseLnCentY - offset);
                path.close();
                break;
            case DOWN: //向下
                path.moveTo(baseLnCentX - baseLine / 2, baseLnCentY);
                path.lineTo(baseLnCentX + baseLine / 2, baseLnCentY);
                path.lineTo(baseLnCentX, baseLnCentY + offset);
                path.close();
                break;
            case LEFT: //向左
                path.moveTo(baseLnCentX, baseLnCentY - baseLine / 2);
                path.lineTo(baseLnCentX, baseLnCentY + baseLine / 2);
                path.lineTo(baseLnCentX - offset, baseLnCentY);
                path.close();
                break;
            case RIGHT: //向右
                path.moveTo(baseLnCentX, baseLnCentY - baseLine / 2);
                path.lineTo(baseLnCentX, baseLnCentY + baseLine / 2);
                path.lineTo(baseLnCentX + offset, baseLnCentY);
                path.close();
                break;
        }
        //三角形的填充风格
        switch (triangleStyle) {
            case OUTLINE: //空心
                paint.setStyle(Paint.Style.STROKE);
                break;
            case FILL: //FILL 
                paint.setStyle(Paint.Style.FILL);
                break;
        }
        canvas.drawPath(path, paint);
        path.reset();
    }


    public PathEffect getDotLineStyle() {
        return (new DashPathEffect(new float[]{2, 2, 2, 2}, 1));
    }

    public PathEffect getDashLineStyle() {
        //虚实线
        return (new DashPathEffect(new float[]{4, 8, 5, 10}, 1));
    }

    /**
     * 绘制点
     *
     * @param startX 起始点X坐标
     * @param startY 起始点Y坐标
     * @param stopX  终止点X坐标
     * @param stopY  终止点Y坐标
     * @param canvas 画布
     * @param paint  画笔
     */
    public void drawDotLine(float startX, float startY, float stopX, float stopY, Canvas canvas, Paint paint) {
        //PathEffect effects = new DashPathEffect(new float[] { 2, 2, 2, 2}, 1);  
        paint.setPathEffect(getDotLineStyle());
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        paint.setPathEffect(null);
    }

    /**
     * 绘制虚实线
     *
     * @param startX 起始点X坐标
     * @param startY 起始点Y坐标
     * @param stopX  终止点X坐标
     * @param stopY  终止点Y坐标
     * @param canvas 画布
     * @param paint  画笔
     */
    public void drawDashLine(float startX, float startY, float stopX, float stopY, Canvas canvas, Paint paint) {
        //虚实线
        //PathEffect effects = new DashPathEffect(new float[] { 4, 8, 5, 10}, 1);  
        paint.setPathEffect(getDashLineStyle());
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        paint.setPathEffect(null);
    }


    //下次应当做的:虚实线 比例的灵活定制,线的阴影渲染
    public void drawLine(LineStyle lineStyle, float startX, float startY, float stopX, float stopY, Canvas canvas, Paint paint) {
        switch (lineStyle) {
            case SOLID:
                canvas.drawLine(startX, startY, stopX, stopY, paint);
                break;
            case DOT:
                drawDotLine(startX, startY, stopX, stopY, canvas, paint);
                break;
            case DASH:
                drawDashLine(startX, startY, stopX, stopY, canvas, paint);
                break;
        }
    }

    /**
     * 绘制图中显示所占比例 的扇区
     *
     * @param paintArc   画笔
     * @param cirX       x坐标
     * @param cirY       y坐标
     * @param radius     半径
     * @param startAngle 偏移角度
     * @param sweepAngle 当前角度
     */
    public void drawPercent(Canvas canvas, Paint paintArc, final float cirX, final float cirY, final float radius, final float startAngle, final float sweepAngle, boolean useCenter) {
        initializeRectF();
        rectF.left = cirX - radius;
        rectF.top = cirY - radius;
        rectF.right = cirX + radius;
        rectF.bottom = cirY + radius;
        //在饼图中显示所占比例  
        canvas.drawArc(rectF, startAngle, sweepAngle, useCenter, paintArc);
        rectF.setEmpty();
    }

    public void drawPathArc(Canvas canvas, Paint paintArc, final float cirX, final float cirY, final float radius, final float startAngle, final float sweepAngle) {
        initializeRectF();
        rectF.left = cirX - radius;
        rectF.top = cirY - radius;
        rectF.right = cirX + radius;
        rectF.bottom = cirY + radius;
        //弧形			 
        initializePath();
        path.addArc(rectF, startAngle, sweepAngle);
        canvas.drawPath(path, paintArc);
        rectF.setEmpty();
        path.reset();
    }

    public float drawText(Canvas canvas, Paint paint, String text, float x, float y) {
        if (text.length() > 0) {
            if (text.indexOf(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext())) > 0) {
                float height = getPaintFontHeight(paint);
                for (String data : text.split(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()))) {
                    canvas.drawText(data, x, y, paint);
                    y += height;
                }
            } else {
                canvas.drawText(text, x, y, paint);
            }
        }
        return y;
    }
}
