/*
 * Copyright 2018 Jean-Baptiste VINCEY.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hynet.heebit.components.widget.radiogroup;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewStructure;
import android.widget.CompoundButton;

import com.hynet.heebit.components.widget.radiogroup.listener.OnCheckedChangeListener;

import androidx.annotation.IdRes;
import androidx.annotation.VisibleForTesting;

public class NestedRadioGroupManager {

    @VisibleForTesting
    protected int checkedId = -1;
    @VisibleForTesting
    protected CompoundButton.OnCheckedChangeListener childOnCheckedChangeListener;
    private boolean protectFromCheckedChange = false;
    private OnCheckedChangeListener onCheckedChangeListener;
    @VisibleForTesting
    protected int initialCheckedId = View.NO_ID;
    private final SparseArray<NestedRadioButton> radioButtons;


    public NestedRadioGroupManager() {
        this.radioButtons = new SparseArray<>();
        this.childOnCheckedChangeListener = new CheckedStateTracker();
    }

    public void initializeCheckedId(int value) {
        this.checkedId = value;
        this.initialCheckedId = value;
    }

    public int getCheckedId() {
        return checkedId;
    }

    public void addNestedRadioButton(NestedRadioButton nestedRadioButton) {
        this.radioButtons.put(nestedRadioButton.getId(), nestedRadioButton);
        if (checkedId == nestedRadioButton.getId()) {
            this.protectFromCheckedChange = true;
            setCheckedStateForView(checkedId, true);
            this.protectFromCheckedChange = false;
            setCheckedId(nestedRadioButton.getId());
        }
        nestedRadioButton.setOnCheckedChangeListener(childOnCheckedChangeListener);
    }

    public void check(@IdRes int id) {
        if (id != View.NO_ID && (id == checkedId)) {
            return;
        }
        if (checkedId != View.NO_ID) {
            setCheckedStateForView(checkedId, false);
        }
        if (id != View.NO_ID) {
            setCheckedStateForView(id, true);
        }
        setCheckedId(id);
    }

    @VisibleForTesting
    protected void setCheckedId(@IdRes int id) {
        this.checkedId = id;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checkedId);
        }
    }

    @VisibleForTesting
    protected void setCheckedStateForView(int viewId, boolean checked) {
        NestedRadioButton checkedView = findViewById(viewId);
        if (checkedView != null) {
            checkedView.setChecked(checked);
        }
    }

    private NestedRadioButton findViewById(int viewId) {
        return radioButtons.get(viewId);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void onProvideAutofillStructure(ViewStructure structure) {
        structure.setDataIsSensitive(checkedId != initialCheckedId);
    }

    public void clearCheck() {
        check(View.NO_ID);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (protectFromCheckedChange) {
                return;
            }
            int id = buttonView.getId();

            protectFromCheckedChange = true;
            if (checkedId != View.NO_ID && checkedId != id) {
                setCheckedStateForView(checkedId, false);
            }
            protectFromCheckedChange = false;

            setCheckedId(id);
        }
    }

}
