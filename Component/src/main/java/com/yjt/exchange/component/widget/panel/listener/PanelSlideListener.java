package com.hynet.heebit.components.widget.panel.listener;


import android.view.View;

import com.hynet.heebit.components.widget.panel.PanelState;

public interface PanelSlideListener {

    void onPanelSlide(View panel, float slideOffset);

    void onPanelStateChanged(View panel, PanelState previousState, PanelState newState);
}
