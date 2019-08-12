/**
 * Copyright 2014  XCL-Charts
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Project XCL-Charts
 * @Description Android图表基类库
 * @author XiongChuanLiang<br                                                                                                                               />(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version v0.1
 */

package com.hynet.heebit.components.widget.chart.renderer.axis;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.text.TextUtils;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.constant.Location;
import com.hynet.heebit.components.widget.chart.constant.RoundTickAxisType;
import com.hynet.heebit.components.widget.chart.utils.DrawUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class RoundAxisRender extends RoundAxis {

    private Location location = Location.BOTTOM;

    public RoundAxisRender() {
    }

    public void setAxisPercentage(List<Float> angle) {
        if (null != percentage) percentage.clear();
        if (null == percentage) percentage = new ArrayList<>();
        this.percentage = angle;
    }

    public void setAxisColor(List<Integer> color) {
        if (null != this.color) this.color.clear();
        if (null == this.color) this.color = new ArrayList<>();
        this.color = color;
    }

    public void setAxisLabels(List<String> labels) {
        if (null != this.labels) this.labels.clear();
        if (null == this.labels) this.labels = new ArrayList<>();
        this.labels = labels;
    }

    public void setLineAxisLocation(Location location) {
        this.location = location;
    }

    /**
     * 绘制标签环形轴标签
     *
     * @param canvas 画布
     * @param labels 标签集合
     *
     * @return 是否绘制成功
     */
    public boolean renderTicks(Canvas canvas, List<String> labels) {
        float cirX = circleX;
        float cirY = circleY;
        int count = labels.size();
        float stepsAngle = 0;
        if (Float.compare(totalAngle, 360f) == 0) {
            stepsAngle = MathUtil.getInstance().div(totalAngle, count);
        } else {
            stepsAngle = MathUtil.getInstance().div(totalAngle, count - 1);
        }
        float innerRadius1 = radius;
        float tickRadius = 0.0f, detailRadius = 0.0f;
        if (RoundTickAxisType.INNER_TICKAXIS == roundTickAxisType) {
            tickRadius = radius * 0.95f;
            detailRadius = tickRadius;
            //有启用主明细步长设置 (inner)
            if (1 < detailModeSteps)
                tickRadius = tickRadius - radius * 0.05f;
        } else {
            tickRadius = radius + radius * 0.05f;
            detailRadius = tickRadius;
            if (1 < detailModeSteps)
                tickRadius = radius + radius * 0.08f;
        }
        int steps = detailModeSteps;
        float angle = 0.0f;
        float tickMarkWidth = getTickMarksPaint().getStrokeWidth();
        float stopX = 0.0f, stopY = 0.0f;
        float labelX = 0.0f, labelY = 0.0f;
        float startX = 0.0f, startY = 0.0f;
        for (int i = 0; i < count; i++) {
            if (0 == i) {
                angle = initializeAngle;
            } else {
                //Angle =  MathUtil.getInstance().add(Angle,stepsAngle);
                angle = MathUtil.getInstance().add(initializeAngle, i * stepsAngle);
            }
            MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, innerRadius1, angle);
            startX = MathUtil.getInstance().getPosX();
            startY = MathUtil.getInstance().getPosY();
            stopX = stopY = 0.0f;
            labelX = labelY = 0.0f;
            MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, tickRadius, angle);
            labelX = MathUtil.getInstance().getPosX();
            labelY = MathUtil.getInstance().getPosY();
            if (steps == detailModeSteps) {
                stopX = labelX;
                stopY = labelY;
                steps = 0;
            } else {
                MathUtil.getInstance().calcArcEndPointXY(cirX, cirY, detailRadius, angle);
                stopX = MathUtil.getInstance().getPosX();
                stopY = MathUtil.getInstance().getPosY();
                steps++;
            }
            if (isShowTickMarks()) {
                if (0 == steps && longTickfakeBold) {
                    getTickMarksPaint().setStrokeWidth(tickMarkWidth + 1);
                } else {
                    if (longTickfakeBold) getTickMarksPaint().setStrokeWidth(tickMarkWidth);
                }
                canvas.drawLine(startX, startY, stopX, stopY, getTickMarksPaint());
            }
            if (isShowAxisLabels()) {
                //回调函数定制化标签显示格式
                String label = getFormatterLabel(labels.get(i));
                PointF pLabel = getLabelXY(label, labelX, labelY, cirX, cirY, totalAngle, angle);
                //标签显示
                DrawUtil.getInstance().drawRotateText(label, pLabel.x, pLabel.y, getTickLabelRotateAngle(), canvas, getTickLabelPaint());
            }
        } //end for
        return true;
    }

    //得到标签显示位置
    private PointF getLabelXY(String label, float defLabelX, float defLabelY, float cirX, float cirY, float totalAngle, float Angle) {
        PointF pLabel = new PointF(defLabelX, defLabelY);
        float labelWidth = DrawUtil.getInstance().getTextWidth(getTickLabelPaint(), label);
        float labelHeight = DrawUtil.getInstance().getPaintFontHeight(getTickLabelPaint());
        getTickLabelPaint().setTextAlign(Align.CENTER);
        if (RoundTickAxisType.INNER_TICKAXIS == roundTickAxisType) {
            if (Float.compare(pLabel.y, cirY) == 0) {
                if (Float.compare(pLabel.x, cirX) == -1) {
                    pLabel.x += labelWidth / 2;
                } else {
                    pLabel.x -= labelWidth / 2;
                }
            } else if (Float.compare(pLabel.x, cirX) == 0) {
                if (Float.compare(pLabel.y, cirY) == -1) {
                    pLabel.y += labelHeight / 2;
                } else {
                    pLabel.y -= labelHeight / 2;
                }
            } else if (Float.compare(totalAngle, Angle) == 0) {
                pLabel.y += labelHeight;
            } else if (Float.compare(pLabel.x, cirX) == 1) {
                if (Float.compare(totalAngle, 360f) == 0) {
                    getTickLabelPaint().setTextAlign(Align.RIGHT);
                } else {
                    pLabel.x -= labelWidth / 2;
                }
            } else if (Float.compare(pLabel.x, cirX) == -1) {
                if (Float.compare(totalAngle, 360f) == 0) {
                    getTickLabelPaint().setTextAlign(Align.LEFT);
                } else {
                    pLabel.x += labelWidth / 2;
                }
            }
        } else {
            if (Float.compare(pLabel.y, cirY) == 0) {
                if (Float.compare(pLabel.x, cirX) == -1) {
                    pLabel.x -= labelWidth / 2;
                } else {
                    pLabel.x += labelWidth / 2;
                }
            } else if (Float.compare(pLabel.x, cirX) == 0) {
                if (Float.compare(pLabel.y, cirY) == -1) {
                    pLabel.y -= labelHeight / 2;
                } else {
                    pLabel.y += labelHeight / 2;
                }
            } else if (Float.compare(totalAngle, Angle) == 0) {
                pLabel.y -= labelHeight;
            } else if (Float.compare(pLabel.x, cirX) == 1) {
                if (Float.compare(totalAngle, 360f) == 0) {
                    getTickLabelPaint().setTextAlign(Align.LEFT);
                } else {
                    pLabel.x += labelWidth / 2;
                }
            } else if (Float.compare(pLabel.x, cirX) == -1) {
                if (Float.compare(totalAngle, 360f) == 0) {
                    getTickLabelPaint().setTextAlign(Align.RIGHT);
                } else {
                    pLabel.x -= labelWidth / 2;
                }
            }
        }
        return pLabel;
    }

    /**
     * 绘制填充环形轴
     *
     * @param canvas 画布
     *
     * @return 是否成功
     *
     * @throws Exception 例外
     */
    public boolean renderFillAxis(Canvas canvas) {
        if (isShow() && isShowAxisLine()) {
            if (null != color)
                getFillAxisPaint().setColor(color.get(0));
            DrawUtil.getInstance().drawPercent(canvas, this.getFillAxisPaint(), circleX, circleY, radius, initializeAngle, totalAngle, true);
        }
        return true;
    }

    /**
     * 绘制标签环形轴
     *
     * @param canvas
     *
     * @return
     *
     * @throws Exception
     */
    public boolean renderTickAxis(Canvas canvas) {
        if (!isShow()) return false;
        if (null == labels) return false;
        if (isShowAxisLine()) {
            DrawUtil.getInstance().drawPathArc(canvas, this.getAxisPaint(), this.circleX, this.circleY, this.radius, this.initializeAngle, this.totalAngle);
        }
        return renderTicks(canvas, this.labels);
    }

    //arcline

    /**
     * 绘制弧形环形轴
     *
     * @param canvas
     *
     * @return
     *
     * @throws Exception
     */
    public boolean renderArcLineAxis(Canvas canvas) {
        if (isShow() && isShowAxisLine()) {
            DrawUtil.getInstance().drawPathArc(canvas, this.getAxisPaint(), circleX, circleY, radius, this.initializeAngle, this.totalAngle);
        }
        return true;
    }

    public boolean renderCircleAxis(Canvas canvas) {
        if (isShow() && isShowAxisLine()) {
            if (null != color)
                getAxisPaint().setColor(color.get(0));
            canvas.drawCircle(circleX, circleY, radius, this.getAxisPaint());
        }
        return true;
    }

    /**
     * 绘制颜色块环形轴
     *
     * @param canvas 画布
     *
     * @return 结果
     *
     * @throws Exception 例外
     */
    public boolean renderRingAxis(Canvas canvas) {
        if (!isShow() || !isShowAxisLine()) return true;
        if (null == percentage) return false;
        int angleCount = 0, colorCount = 0, labelsCount = 0;
        angleCount = this.percentage.size();
        if (null != color) colorCount = this.color.size();
        if (null != labels) labelsCount = this.labels.size();
        float offsetAngle = this.initializeAngle;
        int currentColor = -1;
        String currentLabel = null;
        float sweepAngle = 0.0f;
        for (int i = 0; i < angleCount; i++) {
            if (null != color && colorCount > i) currentColor = color.get(i);
            if (null != labels && labelsCount > i) currentLabel = labels.get(i);
            sweepAngle = MathUtil.getInstance().mul(totalAngle, percentage.get(i));
            renderPartitions(canvas, offsetAngle, sweepAngle, currentColor, currentLabel);
            offsetAngle = MathUtil.getInstance().add(offsetAngle, sweepAngle);
            currentColor = -1;
            currentLabel = null;
        }
        if (Float.compare(getRingInnerRadiusPercentage(), 0.0f) != 0 && Float.compare(getRingInnerRadiusPercentage(), 0.0f) == 1) {
            canvas.drawCircle(this.circleX, circleY, getRingInnerRadius(), this.getFillAxisPaint());
        }

        return true;
    }


    /**
     * 绘制颜色轴
     *
     * @throws Exception
     */
    private boolean renderPartitions(Canvas canvas, float startAngle, float sweepAngle, int color, String label) {
        //if(color >= -1) 
        getAxisPaint().setColor(color);
        if (Float.compare(sweepAngle, 0.0f) < 0) {
             LogUtil.Companion.getInstance().print("负角度???!!!");
            return false;
        } else if (Float.compare(sweepAngle, 0.0f) == 0) {
             LogUtil.Companion.getInstance().print("零角度???!!!");
            return true;
        }
        DrawUtil.getInstance().drawPercent(canvas, this.getAxisPaint(), this.circleX, this.circleY, radius, startAngle, sweepAngle, true);
        if (isShowAxisLabels() && !TextUtils.isEmpty(label)) {
            float Angle = MathUtil.getInstance().add(startAngle, sweepAngle / 2);
            MathUtil.getInstance().calcArcEndPointXY(this.circleX, this.circleY, radius * 0.5f, Angle);
            float labelX = MathUtil.getInstance().getPosX();
            float labelY = MathUtil.getInstance().getPosY();
            //定制化显示格式	 Angle* -2
            DrawUtil.getInstance().drawRotateText(getFormatterLabel(label), labelX, labelY, getTickLabelRotateAngle(), canvas, getTickLabelPaint());
        }
        return true;
    }

    /**
     * 中心点的线轴
     *
     * @param canvas 画布
     *
     * @return 结果
     *
     * @throws Exception 例外
     */
    public boolean renderLineAxis(Canvas canvas) {
        if (!isShow() || !isShowAxisLine()) return true;
        switch (location) {
            case TOP:
                canvas.drawLine(circleX, circleY, circleX, circleY - radius, this.getAxisPaint());
                break;
            case BOTTOM:
                canvas.drawLine(circleX, circleY, circleX, circleY + radius, this.getAxisPaint());
                break;
            case LEFT:
                canvas.drawLine(circleX, circleY, circleX - radius, circleY, this.getAxisPaint());
                break;
            case RIGHT:
                canvas.drawLine(circleX, circleY, circleX + radius, circleY, this.getAxisPaint());
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * 圆心坐标
     *
     * @param x x坐标
     * @param y y坐标
     */
    public void setCenterXY(float x, float y) {
        this.circleX = x;
        this.circleY = y;
    }

    /**
     * Ploat范围半径
     *
     * @param radius
     */
    public void setOrgRadius(float radius) {
        orgRadius = radius;
    }

    /**
     * 指定角度及偏移
     *
     * @param totalAngle 总角度
     * @param initAngle  偏移
     */
    public void setAngleInfo(float totalAngle, float initAngle) {
        this.totalAngle = totalAngle;
        this.initializeAngle = initAngle;
    }


    /**
     * 绘制图表
     *
     * @param canvas 画布
     *
     * @return 是否成功
     *
     * @throws Exception 例外
     */
    public boolean render(Canvas canvas) {
        boolean ret = false;
        radius = getOuterRadius();
        // TICKAXIS,RINGAXIS,LENAXIS
        switch (getAxisType()) {
            case TICKAXIS:
                ret = renderTickAxis(canvas);
                break;
            case RINGAXIS:
                ret = renderRingAxis(canvas);
                break;
            case ARCLINEAXIS:
                ret = renderArcLineAxis(canvas);
                break;
            case FILLAXIS:
                ret = renderFillAxis(canvas);
                break;
            case CIRCLEAXIS:
                ret = renderCircleAxis(canvas);
                break;
            case LINEAXIS:
                ret = renderLineAxis(canvas);
                break;
            default:
                break;
        }
        return ret;
    }
}
