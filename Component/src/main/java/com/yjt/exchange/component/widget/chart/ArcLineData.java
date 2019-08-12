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
 * @author XiongChuanLiang<br                               />(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.5
 */
package com.hynet.heebit.components.widget.chart;

import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.widget.chart.utils.MathUtil;

public class ArcLineData {

    private String key;
    private String label;
    private double value = 0.0f;
    private int color = 0;

    public ArcLineData() {
        //super();
    }

    /**
     * @param label   标签
     * @param percent 百分比
     * @param color   显示颜色
     */
    public ArcLineData(String label, double percent, int color) {
        setLabel(label);
        setPercentage(percent);
        setBarColor(color);
    }

    /**
     * @param key     键值
     * @param label   标签
     * @param percent 百分比
     * @param color   显示颜色
     */
    public ArcLineData(String key, String label, double percent, int color) {
        setLabel(label);
        setPercentage(percent);
        setBarColor(color);
        setKey(key);
    }


    /**
     * 设置Key值
     *
     * @param key Key值
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回Key值
     *
     * @return Key值
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置标签
     *
     * @param label 标签
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 设置百分比,绘制时，会将其转换为对应的圆心角
     *
     * @param value 百分比
     */
    public void setPercentage(double value) {
        this.value = value;
    }

    /**
     * 设置扇区颜色
     *
     * @param color 颜色
     */
    public void setBarColor(int color) {
        this.color = color;
    }

    /**
     * 返回标签
     *
     * @return 标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 返回当前百分比
     *
     * @return 百分比
     */
    public double getPercentage() {
        return value;
    }

    /**
     * 返回扇区颜色
     *
     * @return 颜色
     */
    public int getBarColor() {
        return color;
    }

    /**
     * 将百分比转换为图显示角度
     *
     * @return 圆心角度
     */
    public float getSliceAngle() {
        float angle = 0.0f;
        float currentValue = (float) this.getPercentage();
        if (currentValue >= 101f || currentValue < 0.0f) {
             LogUtil.Companion.getInstance().print("输入的百分比不合规范.须在0~100之间.");
        } else {
            //Angle = (float) Math.rint( 360f *  (currentValue / 100f) );
            angle = MathUtil.getInstance().round(360f * (currentValue / 100f), 2);
        }
        return angle;
    }
}
