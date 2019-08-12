package com.hynet.heebit.components.widget.lock.listener.implement;


import com.hynet.heebit.components.constant.Constant;
import com.hynet.heebit.components.widget.lock.NodeDrawable;
import com.hynet.heebit.components.widget.lock.listener.OnPointSelectedListener;

public class NoneHighLightMode implements OnPointSelectedListener {

    @Override
    public int setOnPointSelectedListener(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength) {
//         LogUtil.Companion.getInstance().print("NoneHighLightMode:" + nodeX + "," + nodeY);
        return Constant.Lock.STATE_SELECTED;
    }

}
