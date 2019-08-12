package com.hynet.heebit.components.widget.tablayout.listener.implement;

import com.hynet.heebit.components.widget.tablayout.listener.TabColorizer;

public class SimpleTabColorizer implements TabColorizer {

    private int[] indicatorColors;
    private int[] dividerColors;

    @Override
    public final int getIndicatorColor(int position) {
        return indicatorColors[position % indicatorColors.length];
    }

    @Override
    public final int getDividerColor(int position) {
        return dividerColors[position % dividerColors.length];
    }

    public void setIndicatorColors(int... colors) {
        this.indicatorColors = colors;
    }

    public void setDividerColors(int... colors) {
        this.dividerColors = colors;
    }
}
