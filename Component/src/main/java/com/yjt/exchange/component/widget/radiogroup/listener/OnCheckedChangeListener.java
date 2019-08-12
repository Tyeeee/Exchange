package com.hynet.heebit.components.widget.radiogroup.listener;

import com.hynet.heebit.components.widget.radiogroup.NestedRadioGroupManager;

import androidx.annotation.IdRes;

public interface OnCheckedChangeListener {

    void onCheckedChanged(NestedRadioGroupManager groupManager, @IdRes int checkedId);
}
