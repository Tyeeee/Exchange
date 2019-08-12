package com.hynet.heebit.components.utils;

import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtil {

    private FragmentManager fragmentManager;
    private HashMap<String, OperationInfo> items = new HashMap<String, OperationInfo>();
    private OperationInfo operationInfo;
    private int resource;
    private int[] animations = new int[2];

    public FragmentUtil(FragmentManager fragmentManager, int resource) {
        this.fragmentManager = fragmentManager;
        this.resource = resource;
        items.clear();
        animations[0] = android.R.anim.fade_in;
        animations[1] = android.R.anim.fade_out;
    }

    public void addItem(OperationInfo operationInfo) {
        items.put(operationInfo.getTag(), operationInfo);
    }

    public void getItem(OperationInfo operationInfo) {
        items.get(operationInfo.getTag());
    }

    public boolean isShowing(String tag) {
        return operationInfo.tag.equals(tag);
    }

    public Fragment show(String tag, boolean hasAnimate) {
        return show(items.get(tag), hasAnimate);
    }

    public Fragment show(int id, boolean hasAnimate) {
        return show(items.get(String.valueOf(id)), hasAnimate);
    }

    public Fragment show(String tag, Bundle args, boolean hasAnimate) {
        OperationInfo info = items.get(tag);
        info.bundle = args;
        return show(info, hasAnimate);
    }

    private Fragment show(OperationInfo info, boolean hasAnimate) {
        FragmentTransaction transaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if (hasAnimate) {
            transaction.setCustomAnimations(animations[0], animations[1]);
        }
        if (operationInfo != info) {
            if (operationInfo != null && operationInfo.fragment != null) {
                transaction.hide(operationInfo.fragment);
            }
            operationInfo = info;
            if (operationInfo != null) {
                if (operationInfo.fragment == null) {
                    operationInfo.fragment = Fragment.instantiate(operationInfo.context, operationInfo.clazz.getName(), operationInfo.bundle);
                    if (info.bundle != null) {
                        operationInfo.fragment.setArguments(info.bundle);
                    }
                    transaction.add(resource, operationInfo.fragment, operationInfo.tag);
                } else {
                    transaction.show(operationInfo.fragment);
                }
            }
        } else {
            //已经显示
        }
        transaction.commitAllowingStateLoss();
        if (operationInfo == null) {
            return null;
        } else {
            return operationInfo.fragment;
        }
    }

    public static class OperationInfo {
        protected Context context;
        protected String tag;
        protected Class<?> clazz;
        protected Bundle bundle;
        protected Fragment fragment;

        public OperationInfo(Context context, String tag, Class<?> cls) {
            this(context, tag, cls, null);
        }

        public OperationInfo(Context context, int viewId, Class<?> cls) {
            this(context, viewId, cls, null);
        }

        public OperationInfo(Context context, String tag, Class<?> cls, Bundle args) {
            this.context = context;
            this.tag = tag;
            this.clazz = cls;
            this.bundle = args;
        }

        public OperationInfo(Context context, int viewId, Class<?> cls, Bundle args) {
            this.context = context;
            this.tag = String.valueOf(viewId);
            this.clazz = cls;
            this.bundle = args;
        }

        public String getTag() {
            return tag;
        }
    }
}
