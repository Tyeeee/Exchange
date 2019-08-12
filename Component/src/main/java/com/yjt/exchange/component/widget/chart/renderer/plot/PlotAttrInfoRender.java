package com.hynet.heebit.components.widget.chart.renderer.plot;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.text.TextUtils;

public class PlotAttrInfoRender extends PlotAttrInfo {

    PointF pointF = new PointF();

    public PlotAttrInfoRender() {
    }

    /**
     * 绘制附加信息
     *
     * @param canvas     画布
     * @param centerX    绘图区中心点X坐标
     * @param centerY    绘图区中心点Y坐标
     * @param plotRadius 当前半径
     */
    public void renderAttrInfo(Canvas canvas, float centerX, float centerY, float plotRadius) {
        if (null == attrInfo) return;
        if (null == locations) return;
        float radius = 0.0f;
        String info;
        for (int i = 0; i < attrInfo.size(); i++) {
            info = attrInfo.get(i);
            if (TextUtils.isEmpty(info)) continue;
            if (null == attrInfoPostion || attrInfoPostion.size() < i) continue;
            if (null == attrInfoPaint.get(i) || attrInfoPaint.size() < i) continue;
            pointF.x = centerX;
            pointF.y = centerY;
            radius = plotRadius * attrInfoPostion.get(i);
            switch (locations.get(i)) {
                case TOP:
                    pointF.y = centerY - radius;
                    break;
                case BOTTOM:
                    pointF.y = centerY + radius;
                    break;
                case LEFT:
                    pointF.x = centerX - radius;
                    break;
                case RIGHT:
                    pointF.x = centerX + radius;
                    break;
            }
            canvas.drawText(info, pointF.x, pointF.y, attrInfoPaint.get(i));
        }
    }
}
