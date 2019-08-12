package com.hynet.heebit.components.widget.chart;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Path;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.ChartType;
import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.constant.RadarChartType;
import com.hynet.heebit.components.widget.chart.renderer.RdChart;
import com.hynet.heebit.components.widget.chart.renderer.axis.CategoryAxis;
import com.hynet.heebit.components.widget.chart.renderer.axis.CategoryAxisRender;
import com.hynet.heebit.components.widget.chart.renderer.axis.DataAxis;
import com.hynet.heebit.components.widget.chart.renderer.axis.DataAxisRender;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDotRender;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotLine;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.List;

public class RadarChart extends RdChart {

    //数据轴
    private DataAxisRender dataAxisRender = null;
    //分类轴
    private CategoryAxisRender categoryAxisRender = null;
    //数据源
    private List<RadarData> radarData;
    //依次存下每个圈上，每个标签节点的X,Y坐标
    private Float[][] dotX = null;
    private Float[][] dotY = null;
    private Float[][] labelX = null;
    private Float[][] labelY = null;
    //依次存下每个标签节点归属的圆心角度
    private Float[] labelAgent = null;
    //半径
    private Float[] radius = null;
    //外围标签偏移距离
    private int labelOffset = 0;
    //透明度
    private int areaAlpha = 100;
    private Path path = new Path();
    private RadarChartType radarChartType = RadarChartType.RADAR;

    public RadarChart() {
        initializeChart();
    }

    @Override
    public ChartType getType() {
        return ChartType.RADAR;
    }

    private void initializeChart() {
        if (null == dataAxisRender) dataAxisRender = new DataAxisRender();
        if (null != dataAxisRender) {
            dataAxisRender.setHorizontalTickAlign(Align.LEFT);
            dataAxisRender.getTickLabelPaint().setTextAlign(Align.RIGHT);
            dataAxisRender.hideTickMarks();
        }
        if (null == categoryAxisRender) categoryAxisRender = new CategoryAxisRender();
        if (null == plotLegendRender) this.plotLegendRender.show();
    }

    private void clearArray() {
        if (null != dotX) dotX = null;
        if (null != dotY) dotY = null;
        if (null != labelX) labelX = null;
        if (null != labelY) labelY = null;
        //依次存下每个标签节点归属的圆心角度
        if (null != labelAgent) labelAgent = null;
        //半径
        if (null != radius) radius = null;
    }


    /**
     * 设置雷达图显示类型(蛛网或圆形)
     *
     * @param type 显示类型
     */
    public void setChartType(RadarChartType type) {
        radarChartType = type;
    }

    /**
     * 返回数据轴
     *
     * @return 数据轴
     */
    public DataAxis getDataAxisRender() {
        return dataAxisRender;
    }

    /**
     * 返回分类轴
     *
     * @return 分类轴
     */
    public CategoryAxis getCategoryAxisRender() {
        return categoryAxisRender;
    }

    /**
     * 分类轴的数据源
     *
     * @param dataSeries 标签集
     */
    public void setCategories(List<String> dataSeries) {
        if (null != categoryAxisRender) categoryAxisRender.setDataBuilding(dataSeries);
    }

    /**
     * 设置数据轴的数据源
     *
     * @param dataSeries 数据源
     */
    public void setDataSource(List<RadarData> dataSeries) {
        this.radarData = dataSeries;
    }

    /**
     * 返回图的数据源
     *
     * @return 数据源
     */
    public List<RadarData> getDataSource() {
        return radarData;
    }

    /**
     * 设置透明度,默认为100
     *
     * @param alpha 透明度
     */
    public void setAreaAlpha(int alpha) {
        areaAlpha = alpha;
    }


    private boolean validateParams() {
        if (this.categoryAxisRender.getDataSet().size() <= 0) {
             LogUtil.Companion.getInstance().print("标签数据源为空");
            return false;
        }
        if (this.radarData.size() <= 0) {
             LogUtil.Companion.getInstance().print("数据源为空");
            return false;
        }
        return true;
    }

    /**
     * 用来绘制蜘蛛网线
     */
    private void renderGridLines(Canvas canvas) {
        switch (radarChartType) {
            case RADAR:
                renderGridLinesRadar(canvas);
                break;
            case ROUND:
                renderGridLinesRound(canvas);
                break;
            default:
                break;
        }
    }

    private void renderGridLinesRadar(Canvas canvas) {
        path.reset();
        for (int i = 0; i < getAxisTickCount(); i++) {
            for (int j = 0; j < getPlotAgentNumber(); j++) {
                if (0 == j) {
                    path.moveTo(dotX[i][j], dotY[i][j]);
                } else {
                    path.lineTo(dotX[i][j], dotY[i][j]);
                }
            }
            path.close();
            canvas.drawPath(path, getLinePaint());
            path.reset();
        }
    }

    private void renderGridLinesRound(Canvas canvas) {
        for (int i = 0; i < radius.length; i++) {
            canvas.drawCircle(plotAreaRender.getCenterX(), plotAreaRender.getCenterY(), radius[i], getLinePaint());
        }
    }


    /**
     * 绘制各个圆心角的轴线
     *
     * @param canvas 画布
     */
    private void renderAxisLines(Canvas canvas) {
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        //标签个数决定角的个数
        int labelsCount = getPlotAgentNumber();
        //轴线tick总数
        int dataAxisTickCount = getAxisTickCount();
        //轴线总数
        int i = dataAxisTickCount - 1;
        for (int j = 0; j < labelsCount; j++) {
            //用于绘制各个方向上的轴线
            canvas.drawLine(cirX, cirY, dotX[i][j], dotY[i][j], getLinePaint());
        }
    }


    /**
     * 绘制最外围的标签及主轴的刻度线标签
     *
     * @param canvas 画布
     */
    private void renderAxisLabels(Canvas canvas) {
        //标签个数决定角的个数
        int labelsCount = getPlotAgentNumber();
        //轴线tick总数
        int dataAxisTickCount = getAxisTickCount();
        for (int i = 0; i < dataAxisTickCount; i++) {
            for (int j = 0; j < labelsCount; j++) {
                //绘制最外围的标签
                if (i == dataAxisTickCount - 1) {
                    //绘制最外围的标签
                    String label = categoryAxisRender.getDataSet().get(j);
                    canvas.drawText(label, labelX[i][j], labelY[i][j], getLabelPaint());
                }
                //绘制主轴的刻度线与标签
                if (0 == j) { //显示在第一轴线上(即270度的那根线)
                    //绘制主轴(DataAxis)的刻度线
                    double tick = this.dataAxisRender.getAxisSteps() * i + dataAxisRender.getAxisMin();
                    dataAxisRender.renderAxisHorizontalTick(this.getLeft(), this.getPlotArea().getLeft(), canvas, dotX[i][j], dotY[i][j], Double.toString(tick), true);
                }
            }
        }
    }


    /**
     * 轴线上tick总个数
     *
     * @return 总个数
     */
    private int getAxisTickCount() {
        if (null == dataAxisRender) return 0;
        return Math.round(dataAxisRender.getAixTickCount() + 1);
    }

    /**
     * 标签个数决定了图中角的个数
     *
     * @return 标签总个数
     */
    private int getPlotAgentNumber() {
        if (null == categoryAxisRender) return 0;
        return categoryAxisRender.getDataSet().size();
    }

    /**
     * 设置外围标签位置的偏移距离
     *
     * @param offset 偏移距离
     */
    public void setlabelOffset(int offset) {
        labelOffset = offset;
    }

    /**
     * 得到所有相关的交叉点坐标
     */
    private void calcAllPoints() {
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        //标签个数决定角的个数
        int labelsCount = getPlotAgentNumber();
        //轴线tick总数
        int dataAxisTickCount = getAxisTickCount();
        //扇形角度,依标签个数决定
        float pAngle = MathUtil.getInstance().div(360f, labelsCount); //   72f; 
        //270为中轴线所处圆心角
        float initOffsetAgent = MathUtil.getInstance().sub(270f, pAngle);
        //依标签总个数算出环数,依数据刻度数决定
        float avgRadius = MathUtil.getInstance().div(getRadius(), (dataAxisTickCount - 1));
        //当前半径
        //float curRadius = 0.0f;
        //当前圆心角偏移量
        float offsetAgent = 0.0f;
        //坐标与圆心角
        dotX = new Float[dataAxisTickCount][labelsCount];
        dotY = new Float[dataAxisTickCount][labelsCount];
        labelAgent = new Float[labelsCount];
        labelX = new Float[dataAxisTickCount][labelsCount];
        labelY = new Float[dataAxisTickCount][labelsCount];
        radius = new Float[dataAxisTickCount];
        float labelHeight = DrawUtil.getInstance().getPaintFontHeight(getLabelPaint());
        float labelRadius = this.getRadius() + labelHeight + labelOffset;
        float currAgent = 0.0f;
        for (int i = 0; i < dataAxisTickCount; i++) { //数据轴
            //curRadius = avgRadius * i; //当前半径长度，依此算出各节点坐标	
            radius[i] = avgRadius * i;
            for (int j = 0; j < labelsCount; j++) {
                offsetAgent = MathUtil.getInstance().add(initOffsetAgent, pAngle * j);
                currAgent = MathUtil.getInstance().add(offsetAgent, pAngle);
                //计算位置
                if (Float.compare(0.f, radius[i]) == 0) {
                    dotX[i][j] = cirX;
                    dotY[i][j] = cirY;
                } else {
                    MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, radius[i], currAgent);
                    //点的位置
                    dotX[i][j] = MathUtil.getInstance().getPosX();
                    dotY[i][j] = MathUtil.getInstance().getPosY();
                }
                //记下每个标签对应的圆心角
                if (0 == i) labelAgent[j] = currAgent;
                //外围标签位置
                MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, labelRadius, currAgent);
                labelX[i][j] = MathUtil.getInstance().getPosX();
                labelY[i][j] = MathUtil.getInstance().getPosY();
            }
        }
    }


    /**
     * 绘制数据区网络
     *
     * @param canvas 画布
     */
    private void renderDataArea(Canvas canvas) {
        float cirX = plotAreaRender.getCenterX();
        float cirY = plotAreaRender.getCenterY();
        int j = 0;
        for (RadarData lineData : radarData) {
            //画各自的网
            List<Double> dataset = lineData.getLinePoint();
            int dataSize = dataset.size();
            if (dataSize < 3) {
                 LogUtil.Companion.getInstance().print("这几个数据可不够，最少三个起步.");
                continue;
            }
            Float[] arrayDataX = new Float[dataSize];
            Float[] arrayDataY = new Float[dataSize];
            int i = 0;
            for (Double data : dataset) {
                if (Double.compare(data, 0.d) == 0) {
                    arrayDataX[i] = plotAreaRender.getCenterX();
                    arrayDataY[i] = plotAreaRender.getCenterY();
                    i++; //标签
                    continue;
                }
                Double per = (data - dataAxisRender.getAxisMin()) / dataAxisRender.getAxisRange();
                float curRadius = (float) (getRadius() * per);
                //计算位置
                MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, curRadius, labelAgent[i]);
                //依Path还是Line来决定画线风格
                arrayDataX[i] = MathUtil.getInstance().getPosX();
                arrayDataY[i] = MathUtil.getInstance().getPosY();
                i++; //标签
            }
            //画线或填充
            switch (lineData.getAreaStyle()) {
                case FILL:
                    drawDataPath(canvas, lineData, arrayDataX, arrayDataY, j);
                    break;
                case STROKE:
                    renderDataLine(canvas, lineData, arrayDataX, arrayDataY, j);
                    break;
                default:
                     LogUtil.Companion.getInstance().print("这类型不认识.");
                    break;
            }
            j++;
        }

    }

    /**
     * 绘制数据连接线
     *
     * @param canvas     画布
     * @param lineData   数据集类
     * @param arrayDataX x坐标
     * @param arrayDataY y坐标
     */

    private void renderDataLine(Canvas canvas, RadarData lineData, Float[] arrayDataX, Float[] arrayDataY, int dataID) {
        float startX = 0.0f, startY = 0.0f;
        float initX = 0.0f, initY = 0.0f;
        for (int p = 0; p < arrayDataX.length; p++) {
            if (0 == p) {
                initX = startX = arrayDataX[p];
                initY = startY = arrayDataY[p];
            } else {
                DrawUtil.getInstance().drawLine(lineData.getLineStyle(), startX, startY, arrayDataX[p], arrayDataY[p], canvas, lineData.getPlotLine().getLinePaint());
                startX = arrayDataX[p];
                startY = arrayDataY[p];
            }
        }
        //收尾
        DrawUtil.getInstance().drawLine(lineData.getLineStyle(), startX, startY, initX, initY, canvas, lineData.getPlotLine().getLinePaint());
        //绘制点及对应的标签
        for (int p = 0; p < arrayDataX.length; p++) {
            renderDotAndLabel(canvas, lineData, arrayDataX[p], arrayDataY[p], dataID, p);
        }
    }


    /**
     * 绘制图网络线
     *
     * @param canvas     画布
     * @param lineData   数据集类
     * @param arrayDataX x坐标
     * @param arrayDataY y坐标
     */
    private void drawDataPath(Canvas canvas, RadarData lineData, Float[] arrayDataX, Float[] arrayDataY, int dataID) {
        float initX = 0.0f, initY = 0.0f;
        path.reset();
        for (int p = 0; p < arrayDataX.length; p++) {
            if (0 == p) {
                initX = arrayDataX[p];
                initY = arrayDataY[p];
                path.moveTo(initX, initY);
            } else {
                path.lineTo(arrayDataX[p], arrayDataY[p]);
            }
        }
        //收尾
        path.lineTo(initX, initY);
        path.close();
        int oldAlpha = lineData.getPlotLine().getLinePaint().getAlpha();
        lineData.getPlotLine().getLinePaint().setAlpha(areaAlpha);
        canvas.drawPath(path, lineData.getPlotLine().getLinePaint());
        //绘制点及对应的标签
        lineData.getPlotLine().getLinePaint().setAlpha(oldAlpha);
        for (int p = 0; p < arrayDataX.length; p++) {
            renderDotAndLabel(canvas, lineData, arrayDataX[p], arrayDataY[p], dataID, p);
        }
    }


    private void renderDotAndLabel(Canvas canvas, RadarData lineData, float currentX, float currentY, int dataID, int childID) {
        PlotLine plotLine = lineData.getPlotLine();
        float itemAngle = lineData.getItemLabelRotateAngle();

        if (!plotLine.getDotStyle().equals(DotStyle.HIDE)) {
            PlotDot pDot = plotLine.getPlotDot();
            float radius = pDot.getDotRadius();
            PlotDotRender.getInstance().renderDot(canvas, pDot, currentX, currentY, lineData.getPlotLine().getDotPaint());
            savePointRecord(dataID, childID, currentX, currentY, currentX - radius, currentY - radius, currentX + radius, currentY + radius);
        }
        //是否显示标签
        if (lineData.getLabelVisible()) {
            DrawUtil.getInstance().drawRotateText(this.getFormatterDotLabel(lineData.getLinePoint().get(childID)), currentX, currentY, itemAngle, canvas, lineData.getPlotLine().getDotLabelPaint());
        }
    }


    /**
     * 绘制图
     */
    protected void renderPlot(Canvas canvas) {
        if (!validateParams()) return;
        calcAllPoints();
        renderGridLines(canvas);
        renderAxisLines(canvas);
        renderDataArea(canvas);
        renderAxisLabels(canvas);
        //图例
        plotLegendRender.renderRdKey(canvas, radarData);
        clearArray();
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


