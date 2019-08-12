package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Paint;

import com.hynet.heebit.components.widget.chart.constant.Location;

import java.util.ArrayList;
import java.util.List;

public class PlotAttrInfo {

    protected List<Location> locations = null;
    protected List<String> attrInfo = null;
    protected List<Float> attrInfoPostion = null;
    protected List<Paint> attrInfoPaint = null;

    public PlotAttrInfo() {
    }

    /**
     * 返回附加信息集合
     *
     * @return 集合
     */
    public List<String> getPlotAttrInfo() {
        return attrInfo;
    }

    /**
     * 返回附加信息位置集合
     *
     * @return 集合
     */
    public List<Float> getPlotAttrInfoPostion() {
        return attrInfoPostion;
    }

    /**
     * 返回附加信息画笔集合
     *
     * @return 集合
     */
    public List<Paint> getPlotAttrInfoPaint() {
        return attrInfoPaint;
    }


    /**
     * 清掉所有附加信息
     */
    public void clearPlotAttrInfo() {
        if (null != locations) locations.clear();
        if (null != attrInfo) attrInfo.clear();
        if (null != attrInfoPostion) attrInfoPostion.clear();
        if (null != attrInfoPaint) attrInfoPaint.clear();
    }


    /**
     * 增加附加信息
     *
     * @param position                显示方位
     * @param info                    附加信息
     * @param infoPosRadiusPercentage 信息显示在总半径指定比例所在位置
     * @param paint                   用来绘制用的画笔
     */
    public void addAttributeInfo(Location position, String info, float infoPosRadiusPercentage, Paint paint) {
        if (null == locations) locations = new ArrayList<>();
        if (null == attrInfo) attrInfo = new ArrayList<>();
        if (null == attrInfoPostion) attrInfoPostion = new ArrayList<>();
        if (null == attrInfoPaint) attrInfoPaint = new ArrayList<>();
        locations.add(position);
        attrInfo.add(info);
        attrInfoPostion.add(infoPosRadiusPercentage);
        attrInfoPaint.add(paint);
    }
}
