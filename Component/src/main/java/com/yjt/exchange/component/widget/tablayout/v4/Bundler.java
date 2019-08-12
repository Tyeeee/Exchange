package com.hynet.heebit.components.widget.tablayout.v4;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class Bundler {

    private final Bundle bundle;

    public Bundler() {
        this(null);
    }

    private Bundler(Bundle b) {
        bundle = (b == null) ? new Bundle() : new Bundle(b);
    }

    public static Bundler of(Bundle b) {
        return new Bundler(b);
    }

    public Bundler putAll(Bundle bundle) {
        this.bundle.putAll(bundle);
        return this;
    }

    public Bundler putByte(String key, byte value) {
        bundle.putByte(key, value);
        return this;
    }

    public Bundler putChar(String key, char value) {
        bundle.putChar(key, value);
        return this;
    }

    public Bundler putShort(String key, short value) {
        bundle.putShort(key, value);
        return this;
    }

    public Bundler putFloat(String key, float value) {
        bundle.putFloat(key, value);
        return this;
    }

    public Bundler putCharSequence(String key, CharSequence value) {
        bundle.putCharSequence(key, value);
        return this;
    }

    public Bundler putParcelable(String key, Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    @TargetApi(21)
    public Bundler putSize(String key, Size value) {
        bundle.putSize(key, value);
        return this;
    }

    @TargetApi(21)
    public Bundler putSizeF(String key, SizeF value) {
        bundle.putSizeF(key, value);
        return this;
    }

    public Bundler putParcelableArray(String key, Parcelable[] value) {
        bundle.putParcelableArray(key, value);
        return this;
    }

    public Bundler putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
        bundle.putParcelableArrayList(key, value);
        return this;
    }

    public Bundler putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
        bundle.putSparseParcelableArray(key, value);
        return this;
    }

    public Bundler putIntegerArrayList(String key, ArrayList<Integer> value) {
        bundle.putIntegerArrayList(key, value);
        return this;
    }

    public Bundler putStringArrayList(String key, ArrayList<String> value) {
        bundle.putStringArrayList(key, value);
        return this;
    }

    @TargetApi(8)
    public Bundler putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        bundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public Bundler putSerializable(String key, Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

    public Bundler putByteArray(String key, byte[] value) {
        bundle.putByteArray(key, value);
        return this;
    }

    public Bundler putShortArray(String key, short[] value) {
        bundle.putShortArray(key, value);
        return this;
    }

    public Bundler putCharArray(String key, char[] value) {
        bundle.putCharArray(key, value);
        return this;
    }

    public Bundler putFloatArray(String key, float[] value) {
        bundle.putFloatArray(key, value);
        return this;
    }

    @TargetApi(8)
    public Bundler putCharSequenceArray(String key, CharSequence[] value) {
        bundle.putCharSequenceArray(key, value);
        return this;
    }

    public Bundler putBundle(String key, Bundle value) {
        bundle.putBundle(key, value);
        return this;
    }

    @TargetApi(18)
    public Bundler putBinder(String key, IBinder value) {
        bundle.putBinder(key, value);
        return this;
    }

    public Bundler putBoolean(String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public Bundler putInt(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public Bundler putLong(String key, long value) {
        bundle.putLong(key, value);
        return this;
    }

    public Bundler putDouble(String key, double value) {
        bundle.putDouble(key, value);
        return this;
    }

    public Bundler putString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public Bundler putBooleanArray(String key, boolean[] value) {
        bundle.putBooleanArray(key, value);
        return this;
    }

    public Bundler putIntArray(String key, int[] value) {
        bundle.putIntArray(key, value);
        return this;
    }

    public Bundler putLongArray(String key, long[] value) {
        bundle.putLongArray(key, value);
        return this;
    }

    public Bundler putDoubleArray(String key, double[] value) {
        bundle.putDoubleArray(key, value);
        return this;
    }

    public Bundler putStringArray(String key, String[] value) {
        bundle.putStringArray(key, value);
        return this;
    }

    public Bundle get() {
        return bundle;
    }

    public <T extends Fragment> T into(T fragment) {
        fragment.setArguments(get());
        return fragment;
    }

}
