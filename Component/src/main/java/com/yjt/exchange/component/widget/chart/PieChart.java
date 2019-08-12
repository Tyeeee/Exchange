package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.LabelInfoSaveType;
import com.hynet.heebit.components.widget.chart.event.click.ArcPosition;
import com.hynet.heebit.components.widget.chart.renderer.CirChart;
import com.hynet.heebit.components.widget.chart.renderer.info.PlotArcLabelInfo;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class PieChart extends CirChart {

    //是否使用渲染来突出效果
    private boolean gradient = true;
    //选中区偏移分离长度
    private float selectedOffset = 10.0f;
    //数据源
    private List<PieData> pieData;
    private Paint paint = null;
    protected RectF rectF = null;
    //扇形边框
    protected Paint arcBorderPaint = null;
    //是否需要保存标签的位置
    private boolean saveLabelsPosition = false;
    private LabelInfoSaveType labelInfoSaveType = LabelInfoSaveType.ONLYPOSITION;
    //保存标签的坐标信息
    protected ArrayList<PlotArcLabelInfo> plotArcLabelInfos = null;
    //总角度
    private float mTotalAngle = 360.f;

    public PieChart() {
        if (null == plotArcLabelInfos) plotArcLabelInfos = new ArrayList<>();
    }

    @Override
    public ChartType getType() {
        return ChartType.PIE;
    }

    /**
     * 开放饼图扇区的画笔
     *
     * @return 画笔
     */
    public Paint geArcPaint() {
        //画笔初始化
        if (null == paint) {
            paint = new Paint();
            paint.setAntiAlias(true);
        }
        return paint;
    }

    /**
     * 设置图表的数据源
     *
     * @param piedata 来源数据集
     */
    public void setDataSource(List<PieData> piedata) {
        this.pieData = piedata;
    }

    /**
     * 返回数据轴的数据源
     *
     * @return 数据源
     */
    public List<PieData> getDataSource() {
        return pieData;
    }

    /**
     * 设置总圆心角度,默认360
     *
     * @param total 总角度
     */
    public void setTotalAngle(float total) {
        mTotalAngle = total;
    }

    /**
     * 返回当前总圆心角度
     *
     * @return 总角度
     */
    public float getTotalAngle() {
        return mTotalAngle;
    }

    /**
     * 是否保存标签显示位置并设置标签信息的保存类型 <br/>
     * ONLYPOSITION : 保存坐标信息，但不显示标签 <br/>
     * ALL : 保存坐标信息，也显示标签 <br/>
     * 用来在其上绘图片之类操作 <br/>
     *
     * @param type 类型
     */
    public void saveLabelsPosition(LabelInfoSaveType type) {
        labelInfoSaveType = type;
        saveLabelsPosition = LabelInfoSaveType.NONE != type;
    }

    /**
     * 返回保存的标签坐标点,前提是有指定saveLabelsPosition()，否则返回空
     *
     * @return 标签坐标点集合
     */
    public ArrayList<PlotArcLabelInfo> getLabelsPosition() {
        return plotArcLabelInfos;
    }

    /**
     * 绘制扇形边框画笔
     *
     * @return 画笔
     */
    public Paint getArcBorderPaint() {
        if (null == arcBorderPaint) {
            arcBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            arcBorderPaint.setStyle(Style.STROKE);
            arcBorderPaint.setColor(Color.WHITE);
        }
        return arcBorderPaint;
    }

    /**
     * 显示渲染效果(此函数对3D饼图无效)
     */
    public void showGradient() {
        gradient = true;
    }

    /**
     * 隐藏渲染效果(此函数对3D饼图无效)
     */
    public void hideGradient() {
        gradient = false;
    }

    /**
     * 确认是否可使用渲染效果(此函数对3D饼图无效)
     *
     * @return 是否使用渲染
     */
    public boolean getGradient() {
        return gradient;
    }

    /**
     * 选中区偏移分离距离
     *
     * @param len 距离
     */
    public void setSelectedOffset(float len) {
        selectedOffset = len;
    }

    /**
     * 选中区偏移分离距离
     *
     * @return 距离
     */
    public float getSelectedOffset() {
        return selectedOffset;
    }


    /**
     * 给画笔设置渲染颜色和效果
     *
     * @param paintArc 画笔
     * @param cirX     中心点X坐标
     * @param cirY     中心点Y坐标
     * @param radius   半径
     *
     * @return 返回渲染效果类
     */
    private RadialGradient renderRadialGradient(Paint paintArc, float cirX, float cirY, float radius) {
        float radialRadius = radius * 0.8f;
        int color = paintArc.getColor();
        int darkerColor = DrawUtil.getInstance().getDarkerColor(color);
        RadialGradient radialGradient = new RadialGradient(cirX, cirY, radialRadius, darkerColor, color, Shader.TileMode.MIRROR);
        //返回环形渐变  
        return radialGradient;
    }

    /**
     * 检查角度的合法性
     *
     * @param Angle 角度
     *
     * @return 是否正常
     */
    protected boolean validateAngle(float Angle) {
        if (Float.compare(Angle, 0.0f) == 0 || Float.compare(Angle, 0.0f) == -1) {
            //Log.i(TAG, "扇区圆心角小于等于0度. 当前圆心角为:"+Float.toString(Angle));
            return false;
        }
        return true;
    }

    protected void renderArcBorder(Canvas canvas, RectF rect, float offsetAngle, float currentAngle) {
        //边框
        if (null != arcBorderPaint) {
            canvas.drawArc(rect, offsetAngle, currentAngle, true, arcBorderPaint);
        }
    }

    protected void initializeRectF(float left, float top, float right, float bottom) {
        if (null == rectF) {
            rectF = new RectF(left, top, right, bottom);
        } else {
            rectF.set(left, top, right, bottom);
        }
    }

    protected boolean renderLabels(Canvas canvas) {
        if (null == plotArcLabelInfos) return false;
        boolean showLabel = true;
        if (saveLabelsPosition) {
            if (LabelInfoSaveType.ONLYPOSITION == labelInfoSaveType) showLabel = false;
        }
        int count = plotArcLabelInfos.size();
        for (int i = 0; i < count; i++) {
            PlotArcLabelInfo info = plotArcLabelInfos.get(i);
            renderLabel(canvas, pieData.get(info.getID()), info, saveLabelsPosition, showLabel);
        }
        if (!saveLabelsPosition) plotArcLabelInfos.clear();
        return true;
    }

    /**
     * 绘制图
     */
    protected boolean renderPlot(Canvas canvas) {
        if (null == pieData) {
            return false;
        }
        //中心点坐标
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        float radius = getRadius();
        if (Float.compare(radius, 0.0f) == 0 || Float.compare(radius, 0.0f) == -1) {
            return false;
        }
        //用于存放当前百分比的圆心角度
        float currentAngle = 0.0f;
        float angle = offsetAngle;
        plotArcLabelInfos.clear();
        float left = sub(cirX, radius);
        float top = sub(cirY, radius);
        float right = add(cirX, radius);
        float bottom = add(cirY, radius);

        int count = pieData.size();
        for (int i = 0; i < count; i++) {
            PieData cData = pieData.get(i);
            //currentAngle = cData.getSliceAngle();	
            currentAngle = MathUtil.getInstance().getSliceAngle(getTotalAngle(), (float) cData.getPercentage());
            if (!validateAngle(currentAngle)) continue;
            geArcPaint().setColor(cData.getSliceColor());
            // 绘制环形渐变
            if (getGradient())
                geArcPaint().setShader(renderRadialGradient(geArcPaint(), cirX, cirY, radius));
            //指定突出哪个块
            if (cData.getSelected()) {
                //偏移圆心点位置(默认偏移半径的1/10)
                float newRadius = div(radius, selectedOffset);
                //计算百分比标签
                PointF point = MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, newRadius, add(angle, currentAngle / 2f));
                initializeRectF(sub(point.x, radius), sub(point.y, radius), add(point.x, radius), add(point.y, radius));
                plotArcLabelInfos.add(new PlotArcLabelInfo(i, point.x, point.y, radius, angle, currentAngle));
            } else {
                initializeRectF(left, top, right, bottom);
                plotArcLabelInfos.add(new PlotArcLabelInfo(i, cirX, cirY, radius, angle, currentAngle));
            }
            //在饼图中显示所占比例  
            canvas.drawArc(rectF, angle, currentAngle, true, geArcPaint());
            //边框
            renderArcBorder(canvas, rectF, angle, currentAngle);
            //保存角度
            saveArcRecord(i, cirX + this.translateXY[0], cirY + this.translateXY[1], radius, angle, currentAngle, selectedOffset, getOffsetAngle());
            //下次的起始角度  
            angle = add(angle, currentAngle);
        }
        //绘制Label
        renderLabels(canvas);
        //图KEY
        plotLegendRender.renderPieKey(canvas, this.pieData);
        return true;
    }

    /**
     * 检验传入参数,累加不能超过360度
     *
     * @return 是否通过效验
     */
    protected boolean validateParams() {
        if (null == pieData) return false;
        float totalAngle = 0.0f, currentValue = 0.0f;
        for (PieData cData : pieData) {
            //currentValue = cData.getSliceAngle();		
            currentValue = MathUtil.getInstance().getSliceAngle(getTotalAngle(), (float) cData.getPercentage());
            totalAngle = add(totalAngle, currentValue);
            //Log.e(TAG,"圆心角:"+Float.toString(currentValue)+" 合计:"+Float.toString(totalAngle));
            if (Float.compare(totalAngle, 0.0f) == -1) {
                LogUtil.Companion.getInstance().print("传入参数不合理，圆心角总计小于等于0度. 现有圆心角合计:" + Float.toString(totalAngle) + " 当前圆心角:" + Float.toString(currentValue) + " 当前百分比:" + Double.toString(cData.getPercentage()));
                //return false;
            } else if (Float.compare(totalAngle, getTotalAngle() + 0.5f) == 1) {
                //圆心角总计大于360度
                LogUtil.Companion.getInstance().print("传入参数不合理，圆心角总计大于总角度. 现有圆心角合计:" + Float.toString(totalAngle));
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
    public ArcPosition getPositionRecord(float x, float y) {
        return getArcRecord(x, y);
    }

    @Override
    protected boolean postRender(Canvas canvas) {
        super.postRender(canvas);
        //检查值是否合理
        if (!validateParams()) return false;
        //绘制图表
        renderPlot(canvas);
        //显示焦点
        renderFocusShape(canvas);
        //响应提示
        renderToolTip(canvas);
        return true;
    }
}
