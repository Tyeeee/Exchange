package com.hynet.heebit.components.widget.lock.listener;


import com.hynet.heebit.components.widget.lock.NodeDrawable;

/**
 * 手势密码point选择监听
 *
 * @author yjt
 */
public interface OnPointSelectedListener {

    int setOnPointSelectedListener(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength);
}
    