package com.hynet.heebit.components.widget.chart.utils;

import android.graphics.PointF;

import java.math.BigDecimal;

public class MathUtil {

    private static MathUtil mathUtil;
    //Position位置
    private float positionX = 0.0f;
    private float positionY = 0.0f;
    private PointF pointF = new PointF();
    //除法运算精度
    private static final int DEFAULT_DIV_SCALE = 10;
    private boolean highPrecision = true;

    public MathUtil() {
    }

    public static synchronized MathUtil getInstance() {
        if (mathUtil == null) {
            mathUtil = new MathUtil();
        }
        return mathUtil;
    }

    public static void releaseInstance() {
        if (mathUtil != null) {
            mathUtil = null;
        }
    }

    private void resetEndPointXY() {
        positionX = positionY = 0.0f;
        pointF.x = positionX;
        pointF.y = positionY;
    }

    //依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标	
    public PointF calcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle) {
        resetEndPointXY();
        if (Float.compare(cirAngle, 0.0f) == 0 ||
                Float.compare(radius, 0.0f) == 0) {
            return pointF;
        }
        //将角度转换为弧度	
        float arcAngle = (float) (Math.PI * div(cirAngle, 180.0f));
        if (Float.compare(arcAngle, 0.0f) == -1) positionX = positionY = 0.0f;
        if (Float.compare(cirAngle, 90.0f) == -1) {
            positionX = add(cirX, (float) Math.cos(arcAngle) * radius);
            positionY = add(cirY, (float) Math.sin(arcAngle) * radius);
        } else if (Float.compare(cirAngle, 90.0f) == 0) {
            positionX = cirX;
            positionY = add(cirY, radius);
        } else if (Float.compare(cirAngle, 90.0f) == 1 && Float.compare(cirAngle, 180.0f) == -1) {
            arcAngle = (float) (Math.PI * (sub(180f, cirAngle)) / 180.0f);
            positionX = sub(cirX, (float) (Math.cos(arcAngle) * radius));
            positionY = add(cirY, (float) (Math.sin(arcAngle) * radius));
        } else if (Float.compare(cirAngle, 180.0f) == 0) {
            positionX = cirX - radius;
            positionY = cirY;
        } else if (Float.compare(cirAngle, 180.0f) == 1 && Float.compare(cirAngle, 270.0f) == -1) {
            arcAngle = (float) (Math.PI * (sub(cirAngle, 180.0f)) / 180.0f);
            positionX = sub(cirX, (float) (Math.cos(arcAngle) * radius));
            positionY = sub(cirY, (float) (Math.sin(arcAngle) * radius));
        } else if (Float.compare(cirAngle, 270.0f) == 0) {
            positionX = cirX;
            positionY = sub(cirY, radius);
        } else {
            arcAngle = (float) (Math.PI * (sub(360.0f, cirAngle)) / 180.0f);
            positionX = add(cirX, (float) (Math.cos(arcAngle) * radius));
            positionY = sub(cirY, (float) (Math.sin(arcAngle) * radius));
        }
        pointF.x = positionX;
        pointF.y = positionY;
        return pointF;
    }

    public PointF getArcEndPointF() {
        return pointF;
    }

    public float getPosX() {
        return positionX;
    }

    public float getPosY() {
        return positionY;
    }


    //两点间的角度
    public double getDegree(float sx, float sy, float tx, float ty) {
        float nX = tx - sx;
        float nY = ty - sy;
        double angrad = 0d, angel = 0d, tpi = 0d;
        float tan = 0.0f;
        if (Float.compare(nX, 0.0f) != 0) {
            tan = Math.abs(nY / nX);
            angel = Math.atan(tan);
            if (Float.compare(nX, 0.0f) == 1) {
                if (Float.compare(nY, 0.0f) == 1 || Float.compare(nY, 0.0f) == 0) {
                    angrad = angel;
                } else {
                    angrad = 2 * Math.PI - angel;
                }
            } else {
                if (Float.compare(nY, 0.0f) == 1 || Float.compare(nY, 0.0f) == 0) {
                    angrad = Math.PI - angel;
                } else {
                    angrad = Math.PI + angel;
                }
            }
        } else {
            tpi = Math.PI / 2;
            if (Float.compare(nY, 0.0f) == 1) {
                angrad = tpi;
            } else {
                angrad = -1 * tpi;
            }
        }
        return Math.toDegrees(angrad);
    }

    //两点间的距离
    public double getDistance(float sx, float sy, float tx, float ty) {
        float nx = Math.abs(tx - sx);
        float ny = Math.abs(ty - sy);

        return Math.hypot(nx, ny);
    }

    /**
     * 将百分比转换为图心角角度
     *
     * @param totalAngle 总角度(如:360度)
     * @param percentage 百分比
     *
     * @return 圆心角度
     */
    public float getSliceAngle(float totalAngle, float percentage) {
        float angle = 0.0f;
        float currentValue = percentage;
        if (currentValue >= 101f || currentValue < 0.0f) {
            //Log.e(TAG,"输入的百分比不合规范.须在0~100之间.");			
        } else {
            angle = MathUtil.getInstance().round(MathUtil.getInstance().mul(totalAngle, MathUtil.getInstance().div(currentValue, 100f)), 2);
        }
        return angle;
    }


    //函数来自LnChart，位置算法现在到处都是，可以考虑优化下。[各轴，定制线]
    public float getLnPlotXValPosition(float plotScreenWidth, float plotAreaLeft, double xValue, double maxValue, double minValue) {
        // 对应的X坐标
        double maxminRange = sub(maxValue, minValue);
        double xScale = div(sub(xValue, minValue), maxminRange);
        return mul(plotScreenWidth, (float) xScale);
    }

    public float getLnXValPosition(float plotScreenWidth, float plotAreaLeft, double xValue, double maxValue, double minValue) {
        // 对应的X坐标
        return add(plotAreaLeft, getLnXValPosition(plotScreenWidth, plotAreaLeft, xValue, maxValue, minValue));
    }

    public void disableHighPrecision() {
        highPrecision = false;
    }

    public void enabledHighPrecision() {
        highPrecision = true;
    }

    /**
     * 加法运算
     *
     * @param v1
     * @param v2
     *
     * @return
     */
    public float add(float v1, float v2) {
        if (!highPrecision) {
            return (v1 + v2);
        } else {
            // BigDecimal bgNum1 = new BigDecimal(Float.toString(v1));
            BigDecimal bgNum1 = new BigDecimal(Float.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Float.toString(v2));
            return bgNum1.add(bgNum2).floatValue();
        }
    }

    /**
     * 减法运算
     *
     * @param v1
     * @param v2
     *
     * @return 运算结果
     */
    public float sub(float v1, float v2) {
        if (!highPrecision) {
            return (v1 - v2);
        } else {
            BigDecimal bgNum1 = new BigDecimal(Float.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Float.toString(v2));
            return bgNum1.subtract(bgNum2).floatValue();
        }
    }

    /**
     * 乘法运算
     *
     * @param v1
     * @param v2
     *
     * @return 运算结果
     */
    public float mul(float v1, float v2) {
        if (!highPrecision) {
            return (v1 * v2);
        } else {
            BigDecimal bgNum1 = new BigDecimal(Float.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Float.toString(v2));
            return bgNum1.multiply(bgNum2).floatValue();
        }
    }

    /**
     * 除法运算,当除不尽时，精确到小数点后10位
     *
     * @param v1
     * @param v2
     *
     * @return 运算结果
     */
    public float div(float v1, float v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 除法运算,当除不尽时，精确到小数点后scale位
     *
     * @param v1
     * @param v2
     * @param scale
     *
     * @return 运算结果
     */
    public float div(float v1, float v2, int scale) {
        if (scale < 0)
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        if (Float.compare(v2, 0.0f) == 0) return 0.0f;
        if (!highPrecision) {
            return (v1 / v2);
        } else {
            BigDecimal bgNum1 = new BigDecimal(Float.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Float.toString(v2));
            return bgNum1.divide(bgNum2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
        }
    }

    /**
     * 四舍五入到小数点后scale位
     *
     * @param v
     * @param scale
     *
     * @return
     */
    public float round(float v, int scale) {
        if (scale < 0)
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        BigDecimal bgNum1 = new BigDecimal(Float.toString(v));
        BigDecimal bgNum2 = new BigDecimal("1");
        return bgNum1.divide(bgNum2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
        // return b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public double add(double v1, double v2) {
        if (!highPrecision) {
            return (v1 + v2);
        } else {
            BigDecimal bgNum1 = new BigDecimal(Double.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Double.toString(v2));
            return bgNum1.add(bgNum2).doubleValue();
        }
    }

    public double sub(double v1, double v2) {
        if (!highPrecision) {
            return (v1 - v2);
        } else {
            BigDecimal bgNum1 = new BigDecimal(Double.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Double.toString(v2));
            return bgNum1.subtract(bgNum2).doubleValue();
        }
    }


    /**
     * 除法计算,使用默认精度
     *
     * @param v1
     * @param v2
     *
     * @return
     */
    public double div(double v1, double v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 除法计算
     *
     * @param v1
     * @param v2
     * @param scale 指定保留精度
     *
     * @return
     */
    public double div(double v1, double v2, int scale) {
        if (scale < 0)
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        if (Double.compare(v2, 0d) == 0) return 0d;
        if (!highPrecision) {
            return (v1 / v2);
        } else {
            BigDecimal bgNum1 = new BigDecimal(Double.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Double.toString(v2));
            return bgNum1.divide(bgNum2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }

    /**
     * 乘法运算
     *
     * @param v1
     * @param v2
     *
     * @return 运算结果
     */
    public double mul(double v1, double v2) {
        if (!highPrecision) {
            return (v1 * v2);
        } else {
            BigDecimal bgNum1 = new BigDecimal(Double.toString(v1));
            BigDecimal bgNum2 = new BigDecimal(Double.toString(v2));
            return bgNum1.multiply(bgNum2).doubleValue();
        }
    }

    /**
     * 四舍五入到小数点后scale位
     *
     * @param v
     * @param scale
     *
     * @return
     */
    public double round(double v, int scale) {
        if (scale < 0)
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        BigDecimal bgNum1 = new BigDecimal(Double.toString(v));
        BigDecimal bgNum2 = new BigDecimal("1");
        return bgNum1.divide(bgNum2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        // return b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
