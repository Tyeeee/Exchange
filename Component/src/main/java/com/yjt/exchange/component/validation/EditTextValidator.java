package com.hynet.heebit.components.validation;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.hynet.heebit.components.constant.Constant;
import com.hynet.heebit.components.constant.Regex;
import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.utils.ViewUtil;
import com.hynet.heebit.components.validation.listener.OnButtonEnableListener;
import com.hynet.heebit.components.validation.listener.OnTextCountListener;

import java.util.ArrayList;

public class EditTextValidator {

    private ArrayList<Validation> validations;

    public EditTextValidator() {
        validations = Lists.newArrayList();
    }

    public EditTextValidator add(Validation validation) {
        validations.add(validation);
        return this;
    }

    public EditTextValidator clear() {
        validations.clear();
        return this;
    }

    public EditTextValidator execute(final Context ctx, final View view, final int backgroundId1, final int backgroundId2, final int textColorId1, final int textColorId2, final OnTextCountListener onTextCountListener, final OnButtonEnableListener onButtonEnableListener, final boolean clickable) {
        for (final Validation validation : validations) {
            if (validation.getEditText() != null) {
                validation.isTextEmpty();
                validation.getEditText().addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                        if (ctx != null && view != null) {
                            if (validation.isRelateButton()) {
                                if (onButtonEnableListener != null) {
                                    onButtonEnableListener.onButtonEnable(ctx, view, backgroundId1, backgroundId2, textColorId1, textColorId2);
                                } else {
                                    setEnabled(ctx, view, backgroundId1, backgroundId2, textColorId1, textColorId2, clickable);
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(validation.getFormat())) {
                            int length = sequence.toString().trim().length();
                            switch (validation.getFormat()) {
                                case Constant.Validation.BANK_CARD_FORMAT:
                                    if (length != 0 && length % 5 == 0) {
                                        validation.getEditText().setText(new StringBuilder(sequence).insert(sequence.length() - 1, Regex.SPACE.getRegext()));
                                    }
                                    Selection.setSelection(validation.getEditText().getText(), validation.getEditText().length());
                                    break;
                                case Constant.Validation.PHONE_NUMBER_FORMAT:
                                    if (length != 0 && (length == 4 || length == 9)) {
                                        validation.getEditText().setText(new StringBuilder(sequence).insert(sequence.length() - 1, Regex.SPACE.getRegext()));
                                    }
                                    Selection.setSelection(validation.getEditText().getText(), validation.getEditText().length());
                                    break;
                                default:
                                    break;
                            }
                            if (onTextCountListener != null) {
                                onTextCountListener.getTextCount(validation.getFormat(), sequence);
                            }
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (!TextUtils.isEmpty(validation.getFormat())) {
                            switch (validation.getFormat()) {
                                case Constant.Validation.ID_CARD_FORMAT:
                                     LogUtil.Companion.getInstance().print("start:" + start);
                                     LogUtil.Companion.getInstance().print("count:" + count);
                                     LogUtil.Companion.getInstance().print("before:" + after);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        validation.isTextEmpty();
                    }
                });
            } else {
                return this;
            }
        }
        return this;
    }

    private void setEnabled(Context ctx, View view, int backgroundId1, int backgroundId2, int textColorId1, int textColorId2, boolean clickable) {
        for (Validation validation : validations) {
            if (validation.isTextEmpty()) {
                ViewUtil.Companion.getInstance().setButton(ctx, (TextView) view, backgroundId1, textColorId1, false, clickable);
                return;
            } else {
                if (!view.isEnabled()) {
                    ViewUtil.Companion.getInstance().setButton(ctx, (TextView) view, backgroundId2, textColorId2, true, clickable);
                }
            }
        }
    }

    public boolean validate(Context ctx) {
        for (Validation validation : validations) {
            if (validation != null) {
                if (validation.getValidationExecutor() == null || validation.getEditText() == null) {
                    return true;
                }
                if (!validation.getValidationExecutor().doValidate(ctx, String.valueOf(validation.getEditText().getText()))) {
                    return false;
                }
            }
        }
        return true;
    }
}
