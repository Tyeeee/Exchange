package com.hynet.heebit.components.widget.zxing.decode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;

import com.hynet.heebit.components.utils.LogUtil;

import java.util.concurrent.RejectedExecutionException;

public final class InactivityTimer {

    private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

    private final Activity activity;
    private final BroadcastReceiver broadcastReceiver;
    private boolean registered;
    private AsyncTask<Object, Object, Object> inactivityTask;

    public InactivityTimer(Activity activity) {
        this.activity = activity;
        this.broadcastReceiver = new PowerStatusReceiver();
        this.registered = false;
        onActivity();
    }

    public synchronized void onActivity() {
        cancel();
        inactivityTask = new InactivityAsyncTask();
        try {
            inactivityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (RejectedExecutionException e) {
             LogUtil.Companion.getInstance().print("Couldn't schedule inactivity task; ignoring");
        }
    }

    public synchronized void onPause() {
        cancel();
        if (registered) {
            activity.unregisterReceiver(broadcastReceiver);
            registered = false;
        } else {
             LogUtil.Companion.getInstance().print("PowerStatusReceiver was never registered?");
        }
    }

    public synchronized void onResume() {
        if (registered) {
             LogUtil.Companion.getInstance().print("PowerStatusReceiver was already registered?");
        } else {
            activity.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            registered = true;
        }
        onActivity();
    }

    public  synchronized void cancel() {
        AsyncTask<?, ?, ?> task = inactivityTask;
        if (task != null) {
            task.cancel(true);
            inactivityTask = null;
        }
    }

    private final class PowerStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // 0 indicates that we're on battery
                boolean onBatteryNow = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) <= 0;
                if (onBatteryNow) {
                    InactivityTimer.this.onActivity();
                } else {
                    InactivityTimer.this.cancel();
                }
            }
        }
    }

    private final class InactivityAsyncTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            try {
                Thread.sleep(INACTIVITY_DELAY_MS);
                 LogUtil.Companion.getInstance().print("Finishing activity due to inactivity");
                activity.finish();
            } catch (InterruptedException e) {
                // continue without killing
            }
            return null;
        }
    }
}
