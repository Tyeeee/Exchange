package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;

import com.hynet.heebit.components.widget.chart.constant.LabelLinePoint;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class LabelBrokenLineRender extends LabelBrokenLine {

    private Path path = null;

    public LabelBrokenLineRender() {
    }

    public PointF renderLabelLine(String text, float itemAngle, float cirX, float cirY, float radius, float calcAngle, Canvas canvas, Paint paintLabel, boolean showLabel, PlotLabelRender plotLabel) {
        float pointRadius = 0.0f;
        if (getLinePointStyle() == LabelLinePoint.END || getLinePointStyle() == LabelLinePoint.ALL)
            pointRadius = getRadius();
        //显示在扇形的外部
        //1/4处为起始点
        float calcRadius = MathUtil.getInstance().sub(radius, radius / brokenStartPoint);
        MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, calcRadius, calcAngle);
        float startX = MathUtil.getInstance().getPosX();
        float startY = MathUtil.getInstance().getPosY();
        //延长原来半径的一半在外面
        calcRadius = radius / 2f;
        MathUtil.getInstance().calcArcEndPointXY(startX, startY, calcRadius, calcAngle);
        float stopX = MathUtil.getInstance().getPosX();
        float stopY = MathUtil.getInstance().getPosY();
        float borkenline = getBrokenLine(); //折线长度
        float endX = 0.0f, endLabelX = 0.0f;
        if (Float.compare(stopX, cirX) == 0) { //位于中间竖线上
            if (Float.compare(stopY, cirY) == 1) {//中点上方,左折线
                paintLabel.setTextAlign(Align.LEFT);
                endX = stopX + borkenline; //+ pointRadius;
                endLabelX = endX + pointRadius;
            } else { //中点下方,右折线	
                paintLabel.setTextAlign(Align.RIGHT);
                endX = stopX - borkenline;
                endLabelX = endX - pointRadius;
            }
        } else if (Float.compare(stopY, cirY) == 0) { //中线横向两端
            endX = stopX;
            if (Float.compare(stopX, cirX) == 0 || Float.compare(stopX, cirX) == -1) { //左边
                paintLabel.setTextAlign(Align.RIGHT);
                endLabelX = endX - pointRadius;
            } else {
                paintLabel.setTextAlign(Align.LEFT);
                endLabelX = endX + pointRadius;
            }
        } else if (Float.compare(stopX + borkenline, cirX) == 1) { //右边
            paintLabel.setTextAlign(Align.LEFT);
            endX = stopX + borkenline;
            endLabelX = endX + pointRadius;
        } else if (Float.compare(stopX - borkenline, cirX) == -1) { //左边
            paintLabel.setTextAlign(Align.RIGHT);
            endX = stopX - borkenline;
            endLabelX = endX - pointRadius;
        } else {
            endLabelX = endX = stopX;
            paintLabel.setTextAlign(Align.CENTER);
        }
        if (isBZLine) {
            //绘制贝塞尔曲线  
            drawBZLine(startX, startY, stopX, stopY, endX, canvas);
        } else {
            //转折线
            drawBrokenLine(startX, startY, stopX, stopY, endX, canvas);
        }
        //标签点NONE,BEGIN,END,ALL    
        drawPoint(startX, startY, stopX, stopY, endX, pointRadius, canvas);

        if (showLabel) { //标签 
            if (null == plotLabel) {
                DrawUtil.getInstance().drawRotateText(text, endLabelX, stopY, itemAngle, canvas, paintLabel);
            } else {
                plotLabel.drawLabel(canvas, paintLabel, text, endLabelX, stopY, itemAngle);
            }
        }

        return (new PointF(endLabelX, stopY));
    }


    private void drawBrokenLine(float startX, float startY, float stopX, float stopY, float endX, Canvas canvas) {
        //连接线
        canvas.drawLine(startX, startY, stopX, stopY, getLabelLinePaint());
        //转折线
        canvas.drawLine(stopX, stopY, endX, stopY, getLabelLinePaint());
    }

    private void drawBZLine(float startX, float startY, float stopX, float stopY, float endX, Canvas canvas) {
        if (null == path) path = new Path();
        getLabelLinePaint().setStyle(Style.STROKE);
        //绘制贝塞尔曲线  
        path.reset();
        path.moveTo(startX, startY);
        path.quadTo(stopX, stopY, endX, stopY);
        canvas.drawPath(path, getLabelLinePaint());
    }


    private void drawPoint(float startX, float startY, float stopX, float stopY, float endX, float pointRadius, Canvas canvas) {
        //NONE,BEGIN,END,ALL
        switch (getLinePointStyle()) {
            case NONE:
                break;
            case BEGIN:
                canvas.drawCircle(startX, startY, pointRadius, getPointPaint());
                break;
            case END:
                canvas.drawCircle(endX, stopY, pointRadius, getPointPaint());
                break;
            case ALL:
                canvas.drawCircle(startX, startY, pointRadius, getPointPaint());
                canvas.drawCircle(endX, stopY, pointRadius, getPointPaint());
                break;
            default:
                break;
        }
    }
}