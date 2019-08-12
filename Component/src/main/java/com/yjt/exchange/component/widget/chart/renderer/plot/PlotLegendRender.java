package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;

import com.hynet.heebit.components.widget.chart.ArcLineData;
import com.hynet.heebit.components.widget.chart.BarData;
import com.hynet.heebit.components.widget.chart.BubbleData;
import com.hynet.heebit.components.widget.chart.LnData;
import com.hynet.heebit.components.widget.chart.PieData;
import com.hynet.heebit.components.widget.chart.RadarData;
import com.hynet.heebit.components.widget.chart.ScatterData;
import com.hynet.heebit.components.widget.chart.constant.ChartType2;
import com.hynet.heebit.components.widget.chart.constant.DotStyle;
import com.hynet.heebit.components.widget.chart.constant.LegendType;
import com.hynet.heebit.components.widget.chart.renderer.XChart;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDot;
import com.hynet.heebit.components.widget.chart.renderer.line.PlotDotRender;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class PlotLegendRender extends PlotLegend {

    private PlotArea plotArea = null;
    private XChart xChart = null;

    private float keyLabelX = 0.0f;
    private float keyLabelY = 0.0f;

    private ArrayList<PlotDot> plotDots = null;
    private ArrayList<String> keys = null;
    private ArrayList<Integer> colors = null;

    private float rectWidth = 0.0f;
    private float rectHeight = 0.0f;

    LinkedHashMap<Integer, Integer> mapID = new LinkedHashMap<>();

    private boolean isLnChart = false;
    private Paint linePaint = null;

    ChartType2 chartType2 = ChartType2.AXIS;

    //此处的5.f相当于this.getBox().getLinePaint()的高度	
    private final int BOX_LINE_SIZE = 5;

    public PlotLegendRender() {
    }

    public PlotLegendRender(XChart xChart) {
        this.xChart = xChart;
    }

    public void setXChart(XChart xChart) {
        this.xChart = xChart;
    }

    private void initEnv() {
        keyLabelX = keyLabelY = 0.0f;
        rectWidth = rectHeight = 0.0f;
    }

    private float getLabelTextWidth(String key) {
        return DrawUtil.getInstance().getTextWidth(getPaint(), key);
    }

    private float getLabelTextHeight() {
        return DrawUtil.getInstance().getPaintFontHeight(getPaint());
    }

    ///////////////////////////////

    // 1. 转成arraylist,convertArray
    // 2. calcRect
    // 3. calcStartXY
    // 4. drawLegend

    public boolean renderBarKey(Canvas canvas, List<BarData> dataSet) {
        if (!isShow()) return false;
        refreshLst();
        convertArrayBarKey(dataSet);
        render(canvas);
        return true;
    }

    public void renderLineKey(Canvas canvas, List<LnData> dataSet) {
        if (!isShow()) return;
        setLnChartStatus();
        refreshLst();
        convertArrayLineKey(dataSet);
        render(canvas);
    }


    public void renderPieKey(Canvas canvas, List<PieData> dataSet) {
        if (!isShow()) return;
        refreshLst();
        chartType2 = ChartType2.CIR;
        convertArrayPieKey(dataSet);
        render(canvas);
    }

    public void renderRdKey(Canvas canvas, List<RadarData> dataSet) {
        if (!isShow()) return;
        setLnChartStatus();
        refreshLst();
        convertArrayRadarKey(dataSet);
        render(canvas);
    }

    public void renderPointKey(Canvas canvas, List<ScatterData> dataSet) {
        if (!isShow()) return;
        refreshLst();
        convertArrayPointKey(dataSet);
        render(canvas);
    }

    public void renderBubbleKey(Canvas canvas, List<BubbleData> dataSet) {
        if (!isShow()) return;
        refreshLst();
        convertArrayBubbleKey(dataSet);
        render(canvas);
    }

    public void renderRoundBarKey(Canvas canvas, List<ArcLineData> dataSet) {
        if (!isShow()) return;
        refreshLst();
        convertArrayArcLineKey(dataSet);
        render(canvas);
    }

    public void renderRangeBarKey(Canvas canvas, String key, int textColor) {
        if (!isShow()) return;
        if (TextUtils.isEmpty(key)) return;
        refreshLst();
        keys.add(key);
        colors.add(textColor);
        PlotDot pDot = new PlotDot();
        pDot.setDotStyle(DotStyle.RECT);
        plotDots.add(pDot);
        render(canvas);
    }


    private void setLnChartStatus() {
        if (null == linePaint) linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.linePaint.setStrokeWidth(2);
        isLnChart = true;
    }


    private void render(Canvas canvas) {
        if (null == xChart) return;
        if (null == plotArea) plotArea = xChart.getPlotArea();
        calcContentRect();
        getStartXY();
        drawLegend(canvas);
    }

    private float getRectWidth() {
        float rectWidth = 0.0f;
        float textHeight = getLabelTextHeight();
        if (isLnChart) {
            rectWidth = 2 * textHeight;
        } else {
            rectWidth = textHeight / 2 + textHeight;
        }
        return rectWidth;
    }

    private void calcContentRect() {
        int countDots = (null != plotDots) ? plotDots.size() : 0;
        int countText = (null != keys) ? keys.size() : 0;
        if (0 == countText && 0 == countDots) return;
        //int count = (countText > countDots)?countText:countDots;
        String text;
        float textHeight = getLabelTextHeight();
        int row = 1;
        mapID.clear();
        float areaWidth = plotArea.getWidth() - 2 * margin;
        float rectWidth = getRectWidth();
        float rowWidth = 0.0f;
        float rowHeight = textHeight;
        float maxHeight = rowHeight;
        float maxWidth = 0.0f;
        for (int i = 0; i < countText; i++) {
            if (countDots > i) {
                PlotDot plot = plotDots.get(i);
                if (isLnChart) {
                    rowWidth += rectWidth + colSpan;
                } else {
                    if (plot.getDotStyle() != DotStyle.HIDE)
                        rowWidth += rectWidth + colSpan;
                }
            }
            text = keys.get(i);
            float labelWidth = getLabelTextWidth(text);
            rowWidth += labelWidth;
            switch (getType()) {
                case ROW:
                    if (Float.compare(rowWidth, areaWidth) == 1) {    //换行
                        rowWidth = rectWidth + colSpan + labelWidth;
                        maxHeight += rowHeight + rowSpan;
                        row++;
                    } else {
                        rowWidth += colSpan;
                        if (Float.compare(rowWidth, maxWidth) == 1) maxWidth = rowWidth;
                    }
                    break;
                case COLUMN:
                    if (Float.compare(rowWidth, maxWidth) == 1) maxWidth = rowWidth;
                    maxHeight += rowHeight + rowSpan;

                    rowWidth = 0.0f;
                    row++;
                    break;
                default:
                    break;
            }
            mapID.put(i, row);
        }
        this.rectWidth = maxWidth + 2 * margin;
        rectHeight = maxHeight + 2 * margin;
        if (LegendType.COLUMN == getType()) rectHeight -= 2 * rowSpan;
    }


    private void getStartXY() {
        float mBoxLineSize = BOX_LINE_SIZE;
        if (!this.showBox) mBoxLineSize = 0.0f;
        switch (getHorizontalAlign()) {
            case LEFT:
                if (ChartType2.CIR == chartType2) {
                    keyLabelX = xChart.getLeft() + offsetX;
                } else {
                    keyLabelX = plotArea.getLeft() + offsetX;
                }
                keyLabelX += mBoxLineSize;
                break;
            case CENTER:
                keyLabelX = this.xChart.getLeft() + (xChart.getWidth() - rectWidth) / 2 + offsetX;
                break;
            case RIGHT:
                if (ChartType2.CIR == chartType2) {
                    keyLabelX = xChart.getRight() - offsetX - rectWidth;
                } else {
                    keyLabelX = plotArea.getRight() - offsetX - rectWidth;
                }
                keyLabelX -= mBoxLineSize;
                break;
            default:
                break;
        }
        switch (getVerticalAlign()) {
            case TOP:
                if (LegendType.COLUMN == getType()) {
                    keyLabelY = plotArea.getTop() + offsetY;
                    keyLabelY += mBoxLineSize;
                } else {
                    keyLabelY = plotArea.getTop() - rectHeight - offsetY;
                    keyLabelY -= mBoxLineSize;
                }
                break;
            case MIDDLE:
                keyLabelY = plotArea.getTop() + (plotArea.getHeight() - rectHeight) / 2;
                break;
            case BOTTOM:
                if (LegendType.COLUMN == getType()) {
                    keyLabelY = xChart.getBottom() + offsetY;
                    keyLabelY += xChart.getBorderWidth();
                    keyLabelY += mBoxLineSize;
                } else {
                    keyLabelY = xChart.getBottom() - rectHeight - offsetY;
                    keyLabelY -= xChart.getBorderWidth();
                    keyLabelY -= mBoxLineSize;
                }
                break;
            default:
                break;
        }

    }


    private void drawLegend(Canvas canvas) {
        int countDots = (null != plotDots) ? plotDots.size() : 0;
        int countText = (null != keys) ? keys.size() : 0;
        if (0 == countText && 0 == countDots) return;
        int countColor = (null != colors) ? colors.size() : 0;
        float currDotsX = keyLabelX + margin;
        float currRowX = currDotsX;
        float currRowY = keyLabelY + margin;
        float textHeight = getLabelTextHeight();
        float rowHeight = textHeight;
        float rectWidth = getRectWidth(); //2 * textHeight;		
        int currRowID = 0;
        Iterator iterator = this.mapID.entrySet().iterator();
        //背景
        drawBox(canvas);
        //图例
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Integer id = (Integer) entry.getKey();
            Integer row = (Integer) entry.getValue();//行号
            //换行
            if (row > currRowID) {
                if (0 < currRowID) currRowY += rowHeight + rowSpan;
                currRowX = keyLabelX + margin;
                currRowID = row;
            }
            //颜色
            if (countColor > id) {
                this.getPaint().setColor(colors.get(id));
                if (isLnChart) linePaint.setColor(colors.get(id));
            } else {
                this.getPaint().setColor(Color.BLACK);
                if (isLnChart) linePaint.setColor(Color.BLACK);
            }
            if (countDots > id) {
                PlotDot plotDot = plotDots.get(id);
                //line
                if (isLnChart) {
                    canvas.drawLine(currRowX, currRowY + rowHeight / 2, currRowX + rectWidth, currRowY + rowHeight / 2, this.linePaint);
                    PlotDotRender.getInstance().renderDot(canvas, plotDot, currRowX + rectWidth / 2, currRowY + rowHeight / 2, this.getPaint());
                    currRowX += rectWidth + colSpan;
                } else {
                    if (plotDot.getDotStyle() != DotStyle.HIDE) {
                        PlotDotRender.getInstance().renderDot(canvas, plotDot, currRowX + rectWidth / 2, currRowY + rowHeight / 2, this.getPaint());
                        currRowX += rectWidth + colSpan;
                    }
                }
            }
            String label = keys.get(id);
            if (!TextUtils.isEmpty(label))
                canvas.drawText(label, currRowX, currRowY + rowHeight, this.getPaint());
            currRowX += this.getLabelTextWidth(label);
            currRowX += colSpan;
        }
        mapID.clear();
        clearLst();
    }

    private void clearLst() {
        if (null != plotDots) {
            plotDots.clear();
            plotDots = null;
        }
        if (null != keys) {
            keys.clear();
            keys = null;
        }
        if (null != colors) {
            colors.clear();
            colors = null;
        }
    }


    private void drawBox(Canvas canvas) {
        if (!showBox) return;
        RectF rectF = new RectF();
        rectF.left = keyLabelX;
        rectF.right = keyLabelX + rectWidth;
        rectF.top = keyLabelY;
        rectF.bottom = keyLabelY + rectHeight;
        borderRender.renderRect(canvas, rectF, showBoxBorder, showBackground);
    }

    private void refreshLst() {
        initEnv();
        if (null == keys)
            keys = new ArrayList<>();
        else
            keys.clear();
        if (null == plotDots)
            plotDots = new ArrayList<>();
        else
            plotDots.clear();
        if (null == colors)
            colors = new ArrayList<>();
        else
            colors.clear();
    }


    private void convertArrayLineKey(List<LnData> dataSet) {
        if (null == dataSet) return;
        String key;
        for (LnData cData : dataSet) {
            key = cData.getLineKey();
            if (!isDrawKey(key)) continue;
            if (TextUtils.isEmpty(key)) continue;
            keys.add(key);
            colors.add(cData.getLineColor());
            plotDots.add(cData.getPlotLine().getPlotDot());
        }
    }

    private void convertArrayBarKey(List<BarData> dataSet) {
        if (null == dataSet) return;
        String key;
        for (BarData cData : dataSet) {
            key = cData.getKey();
            if (!isDrawKey(key)) continue;
            if (TextUtils.isEmpty(key)) continue;
            keys.add(key);
            colors.add(cData.getColor());
            PlotDot dot = new PlotDot();
            dot.setDotStyle(DotStyle.RECT);
            plotDots.add(dot);
        }
    }


    private void convertArrayPieKey(List<PieData> dataSet) {
        if (null == dataSet) return;
        String key;
        for (PieData cData : dataSet) {
            key = cData.getKey();
            if (!isDrawKey(key)) continue;
            if (TextUtils.isEmpty(key)) continue;
            keys.add(key);
            colors.add(cData.getSliceColor());
            PlotDot dot = new PlotDot();
            dot.setDotStyle(DotStyle.RECT);
            plotDots.add(dot);
        }
    }

    private void convertArrayRadarKey(List<RadarData> dataSet) {
        if (null == dataSet) return;
        String key;
        for (RadarData cData : dataSet) {
            key = cData.getLineKey();
            if (!isDrawKey(key)) continue;
            if (TextUtils.isEmpty(key)) continue;
            keys.add(key);
            colors.add(cData.getLineColor());
            plotDots.add(cData.getPlotLine().getPlotDot());
        }
    }


    private void convertArrayPointKey(List<ScatterData> dataSet) {
        if (null == dataSet) return;
        String key;
        for (ScatterData cData : dataSet) {
            key = cData.getKey();
            if (!isDrawKey(key)) continue;
            if (TextUtils.isEmpty(key)) continue;
            keys.add(key);
            colors.add(cData.getPlotDot().getColor());
            plotDots.add(cData.getPlotDot());
        }
    }

    private void convertArrayBubbleKey(List<BubbleData> dataSet) {
        if (null == dataSet) return;
        String key;
        for (BubbleData cData : dataSet) {
            key = cData.getKey();
            if (!isDrawKey(key)) continue;
            if (TextUtils.isEmpty(key)) continue;
            keys.add(key);
            colors.add(cData.getColor());
            PlotDot plotDot = new PlotDot();
            plotDot.setDotStyle(DotStyle.DOT);
            plotDots.add(plotDot);
        }
    }

    private void convertArrayArcLineKey(List<ArcLineData> dataSet) {
        if (null == dataSet) return;
        String key;
        for (ArcLineData cData : dataSet) {
            key = cData.getKey();
            if (!isDrawKey(key)) continue;
            if (TextUtils.isEmpty(key)) continue;
            keys.add(key);
            colors.add(cData.getBarColor());
            PlotDot pDot = new PlotDot();
            pDot.setDotStyle(DotStyle.RECT);
            plotDots.add(pDot);
        }
    }

    private boolean isDrawKey(String key) {
        if (TextUtils.isEmpty(key)) return false;
        return true;
    }
}

