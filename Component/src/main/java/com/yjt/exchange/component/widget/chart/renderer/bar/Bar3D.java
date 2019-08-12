package com.hynet.heebit.components.widget.chart.renderer.bar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Shader;

import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class Bar3D extends Bar {

    //3D效果厚度
    private int thickness = 20;
    //偏移角色度
    private int angle = 45;
    //透明度
    private int alpha = 200;
    //3D浅色画笔
    private Paint paint = null;
    private Paint basePaint = null;
    private Paint base3DPaint = null;
    //3D效果厚度
    private int axisBaseThickness = 20;
    //底盘颜色
    private int axisBaseColor = Color.rgb(73, 172, 72);
    private Path rectangle2DPath = new Path();
    private Paint linePaint = new Paint();

    public Bar3D() {
        if (null == paint) paint = new Paint();
        if (null == basePaint) basePaint = new Paint();
        if (null == base3DPaint) base3DPaint = new Paint();
    }

    /**
     * 得到水平偏移量
     *
     * @param thickness 厚度
     * @param angle     角度
     *
     * @return 水平偏移量
     */
    public double getOffsetX(double thickness, double angle) {
        return thickness * Math.cos(angle * Math.PI / 180);
    }

    /**
     * 得到垂直偏移量
     *
     * @param thickness 厚度
     * @param angle     角度
     *
     * @return 垂直偏移量
     */
    public double getOffsetY(double thickness, double angle) {
        return thickness * Math.sin(angle * Math.PI / 180);
    }

    /**
     * 返回水平偏移量
     *
     * @return 偏移量
     */
    public double getOffsetX() {
        return getOffsetX(axisBaseThickness, angle);
    }

    /**
     * 返回垂直偏移量
     *
     * @return 偏移量
     */
    public double getOffsetY() {
        return getOffsetY(axisBaseThickness, angle);
    }

    /**
     * 计算同标签多柱形时的Y分隔
     *
     * @param YSteps    Y轴步长
     * @param barNumber 柱形个数
     *
     * @return 返回单个柱形的高度及间距
     */
    public float[] getBarHeightAndMargin(float YSteps, int barNumber) {
        return calcBarHeightAndMargin(YSteps, barNumber);
    }

    /**
     * 计算同标签多柱形时的X分隔
     *
     * @param XSteps    X轴步长
     * @param barNumber 柱形个数
     *
     * @return 返回单个柱形的宽度及间距
     */
    public float[] getBarWidthAndMargin(float XSteps, int barNumber) {
        return calcBarWidthAndMargin(XSteps, barNumber);
    }


    /**
     * 竖向柱形的3D效果
     *
     * @param barLeft   左边X坐标
     * @param barTop    顶部Y坐标
     * @param barRight  右边X坐标
     * @param barBottom 底部Y坐标
     * @param color     柱形颜色
     * @param canvas    画布
     */
    public void renderVertical3DBar(float barLeft, float barTop, float barRight, float barBottom, int color, Canvas canvas) {
        //浅色	
        int lightColor = DrawUtil.getInstance().getLightColor(color, alpha);
        getBarPaint().setColor(color);
        paint.setColor(lightColor);
        //水平偏移量
        double offsetX = getOffsetX();
        //垂直偏移量
        double offsetY = getOffsetY();
        //Shadow
        float barLeft2 = Math.round(barLeft - offsetX);
        float barTop2 = Math.round(barTop + offsetY);
        float barRight2 = Math.round(barRight - offsetX);
        float barBottom2 = Math.round(barBottom + offsetY);
        //顶	
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(barLeft, barTop);
        rectangle2DPath.lineTo(barLeft2, barTop2);
        rectangle2DPath.lineTo(barRight2, barTop2);
        rectangle2DPath.lineTo(barRight, barTop);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, getBarPaint());
        //右侧边
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(barRight, barTop);
        rectangle2DPath.lineTo(barRight2, barTop2);
        rectangle2DPath.lineTo(barRight2, barBottom2);
        rectangle2DPath.lineTo(barRight, barBottom);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, getBarPaint());
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(barRight2, barTop2);
        rectangle2DPath.lineTo(barRight2, barBottom2);
        rectangle2DPath.lineTo(barLeft2, barBottom2);
        rectangle2DPath.lineTo(barLeft2, barTop2);
        rectangle2DPath.close();
        //正面 浅色
        LinearGradient linearGradient = new LinearGradient(barLeft2, barBottom2, barRight2, barBottom2, new int[]{color, lightColor}, null, Shader.TileMode.REPEAT);
        paint.setShader(linearGradient);
        paint.setStyle(Style.FILL);
        canvas.drawPath(rectangle2DPath, paint);
        //柱形顶上用白画一个RECT,强化3D效果
        linePaint.reset();
        linePaint.setStyle(Style.STROKE);
        linePaint.setColor(Color.WHITE);
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(barLeft2, barTop2);
        rectangle2DPath.lineTo(barRight2, barTop2);
        rectangle2DPath.lineTo(barRight, barTop);
        canvas.drawPath(rectangle2DPath, linePaint);
        //柱形正面画一根白色竖线,强化3D效果
        canvas.drawLine(barRight2, barTop2, barRight2, barBottom2, linePaint);
    }


    /**
     * 竖向3D柱形图的底 座
     *
     * @param baseLeft   左边X坐标
     * @param baseTop    顶部Y坐标
     * @param baseRight  右边X坐标
     * @param baseBottom 底部Y坐标
     * @param canvas     画布
     */
    public void render3DXAxis(float baseLeft, float baseTop, float baseRight, float baseBottom, Canvas canvas) {
        //浅色
        int baseLightColor = DrawUtil.getInstance().getLightColor(getAxis3DBaseColor(), alpha);
        basePaint.setColor(getAxis3DBaseColor());
        base3DPaint.setColor(baseLightColor);
        //水平偏移量
        float offsetX = (float) getOffsetX();
        //垂直偏移量
        float offsetY = (float) getOffsetY();
        //Shadow
        float baseLeft2 = MathUtil.getInstance().sub(baseLeft, offsetX);
        float baseTop2 = MathUtil.getInstance().add(baseTop, offsetY);
        float baseRight2 = MathUtil.getInstance().sub(baseRight, offsetX);
        float baseBottom2 = MathUtil.getInstance().add(baseBottom, offsetY);
        //顶 用浅色
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(baseLeft, baseBottom);
        rectangle2DPath.lineTo(baseLeft2, baseBottom2);
        rectangle2DPath.lineTo(baseRight2, baseBottom2);
        rectangle2DPath.lineTo(baseRight, baseBottom);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, base3DPaint);
        //右侧边 深色
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(baseRight, baseTop);
        rectangle2DPath.lineTo(baseRight2, baseTop2);
        rectangle2DPath.lineTo(baseRight2, baseBottom2);
        rectangle2DPath.lineTo(baseRight, baseBottom);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, basePaint);
        //正面 深色
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(baseRight2, baseTop2);
        rectangle2DPath.lineTo(baseRight2, baseBottom2);
        rectangle2DPath.lineTo(baseLeft2, baseBottom2);
        rectangle2DPath.lineTo(baseLeft2, baseTop2);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, basePaint);
        //水平偏移量
        linePaint.reset();
        linePaint.setColor(getAxis3DBaseColor());
        linePaint.setStyle(Style.FILL);
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(baseRight2, baseBottom2);
        rectangle2DPath.lineTo(baseRight, baseBottom);
        rectangle2DPath.lineTo(baseRight, MathUtil.getInstance().add(baseBottom, axisBaseThickness));
        rectangle2DPath.lineTo(baseRight2, baseBottom2 + axisBaseThickness);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, linePaint);
        linePaint.setColor(baseLightColor);
        canvas.drawRect(baseLeft2, baseBottom2, baseRight2, baseBottom2 + axisBaseThickness, linePaint);
    }

    /**
     * 横向3D柱形图
     *
     * @param barLeft   左边X坐标
     * @param barTop    顶部Y坐标
     * @param barRight  右边X坐标
     * @param barBottom 底部Y坐标
     * @param color     柱形颜色
     * @param canvas    画布
     */
    public void renderHorizontal3DBar(float barLeft, float barTop, float barRight, float barBottom, int color, Canvas canvas) {
        if (Float.compare(barTop, barBottom) == 0) return;
        //浅色
        int lightColor = DrawUtil.getInstance().getLightColor(color, alpha);
        getBarPaint().setColor(color);
        paint.setColor(lightColor);
        //水平偏移量
        float offsetX = (float) getOffsetX();
        //垂直偏移量
        float offsetY = (float) getOffsetY();
        //Shadow
        float barLeft2 = MathUtil.getInstance().sub(barLeft, offsetX);
        float barTop2 = MathUtil.getInstance().add(barTop, offsetY);
        float barRight2 = MathUtil.getInstance().sub(barRight, offsetX);
        float barBottom2 = MathUtil.getInstance().add(barBottom, offsetY);
        //右侧边 浅色
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(barRight, barTop);
        rectangle2DPath.lineTo(barRight, barBottom);
        rectangle2DPath.lineTo(barRight2, barBottom2);
        rectangle2DPath.lineTo(barRight2, barTop2);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, paint);
        //正面
        canvas.drawRect(barLeft2, barTop2, barRight2, barBottom2, paint);
        //顶
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(barLeft, barTop);
        rectangle2DPath.lineTo(barLeft2, barTop2);
        rectangle2DPath.lineTo(barRight2, barTop2);
        rectangle2DPath.lineTo(barRight, barTop);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, getBarPaint());
        //轮廓线
        linePaint.reset();
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Style.STROKE);
        canvas.drawLine(barLeft2, barTop2, barRight2, barTop2, linePaint);
        canvas.drawLine(barRight2, barTop2, barRight2, barBottom2, linePaint);
        canvas.drawLine(barRight, barTop, barRight2, barTop2, linePaint);
    }


    /**
     * 横向3D柱形图的底 座
     *
     * @param baseLeft   左边X坐标
     * @param baseTop    顶部Y坐标
     * @param baseRight  右边X坐标
     * @param baseBottom 底部Y坐标
     * @param canvas     画布
     */
    public void render3DYAxis(float baseLeft, float baseTop, float baseRight, float baseBottom, Canvas canvas) {
        //浅色
        int baseLightColor = DrawUtil.getInstance().getLightColor(getAxis3DBaseColor(), alpha);
        basePaint.setColor(getAxis3DBaseColor());
        base3DPaint.setColor(baseLightColor);
        //水平偏移量
        float offsetX = (float) (getOffsetX());
        //垂直偏移量
        float offsetY = (float) (getOffsetY());
        //Shadow
        float baseLeft2 = MathUtil.getInstance().sub(baseLeft, offsetX);
        float baseTop2 = MathUtil.getInstance().add(baseTop, offsetY);
        float baseBottom2 = MathUtil.getInstance().add(baseBottom, offsetY);
        //左侧面
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(baseLeft, baseTop);
        rectangle2DPath.lineTo(baseLeft2, baseTop2);
        rectangle2DPath.lineTo(baseLeft2, baseBottom2);
        rectangle2DPath.lineTo(baseLeft, baseBottom);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, basePaint);
        //正面
        canvas.drawRect(baseLeft2, baseTop2, MathUtil.getInstance().sub(baseLeft2, offsetX), baseBottom2, base3DPaint);
        //侧面顶
        rectangle2DPath.reset();
        rectangle2DPath.moveTo(baseLeft, baseTop);
        rectangle2DPath.lineTo(baseLeft2, baseTop2);
        rectangle2DPath.lineTo(MathUtil.getInstance().sub(baseLeft2, offsetX), baseTop2);
        rectangle2DPath.lineTo(MathUtil.getInstance().sub(baseLeft, offsetX), baseTop);
        rectangle2DPath.close();
        canvas.drawPath(rectangle2DPath, base3DPaint);
    }

    /**
     * 返回柱形3D厚度
     *
     * @return 厚度
     */
    public int getThickness() {
        return thickness;
    }

    /**
     * 设置柱形3D厚度
     *
     * @param thickness 厚度
     */
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    /**
     * 设置3D偏转角度
     *
     * @return 偏转角度
     */
    public int getAngle() {
        return angle;
    }

    /**
     * 设置3D偏转角度
     *
     * @param angle 角度
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }

    /**
     * 返回透明度
     *
     * @return 透明度
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * 设置透明度
     *
     * @param alpha 透明度
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    /**
     * 返回坐标系底座厚度
     *
     * @return 底座厚度
     */
    public int getAxis3DBaseThickness() {
        return axisBaseThickness;
    }

    /**
     * 设置坐标系底座厚度
     *
     * @param baseThickness 底座厚度
     */
    public void setAxis3DBaseThickness(int baseThickness) {
        this.axisBaseThickness = baseThickness;
    }

    /**
     * 绘制柱形标签
     *
     * @param text   文本内容
     * @param x      x坐标
     * @param y      y坐标
     * @param canvas 画布
     */
    public void renderBarItemLabel(String text, float x, float y, Canvas canvas) {
        drawBarItemLabel(text, x, y, canvas);
    }

    /**
     * 设置3D底座颜色
     *
     * @param color 颜色
     */
    public void setAxis3DBaseColor(int color) {
        axisBaseColor = color;
    }

    /**
     * 返回3D底座颜色
     *
     * @return 颜色
     */
    public int getAxis3DBaseColor() {
        return axisBaseColor;
    }
}
