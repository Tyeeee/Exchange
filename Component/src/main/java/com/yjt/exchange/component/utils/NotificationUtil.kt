package com.hynet.heebit.components.utils

import android.app.*
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat

class NotificationUtil {

    companion object {

        private const val NOTIFICATION_GROUP_SUMMARY_ID = 1

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NotificationUtil()
        }
    }

    private var notificationId = NOTIFICATION_GROUP_SUMMARY_ID + 1

    fun generateNotification(context: Context, notificationManager: NotificationManager, channelGroupId: String, channelGroupName: String, channelId: String, channelName: String, smallIconId: Int, largeIconId: Int, title: String, content: String, isAotuCancel: Boolean, contentIntent: PendingIntent, deleteIntent: PendingIntent, group: String) {
        val builder: NotificationCompat.Builder
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannelGroup(NotificationChannelGroup(channelGroupId, channelGroupName))
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 600)
            notificationChannel.setBypassDnd(true)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = NotificationCompat.Builder(context, channelId)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setOngoing(false)
                    .setLocalOnly(true)
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(smallIconId)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIconId))
                    .setContentTitle(title)
                    .setContentText(content)
                    .setTicker(content)
                    .setAutoCancel(isAotuCancel)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deleteIntent)
                    .setNumber(10)
                    .setGroupSummary(true)
                    .setGroup(group)
        } else {
            builder = NotificationCompat.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setOngoing(false)
                    .setLocalOnly(true)
                    .setOnlyAlertOnce(true)
                    .setSmallIcon(smallIconId)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIconId))
                    .setContentTitle(title)
                    .setContentText(content)
                    .setTicker(content)
                    .setAutoCancel(isAotuCancel)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deleteIntent)
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 600))
                    .setNumber(10)
                    .setGroupSummary(true)
                    .setGroup(group)
        }
        notificationManager.notify(getNewNotificationId(), builder.build())

        //        if (getNotificationsCount(notificationManager) > 1) {
        //            NotificationCompat.Builder builder2;
        //            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        //                notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(channelGroupId, channelGroupName));
        //                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        //                notificationChannel.enableLights(true);
        //                notificationChannel.setLightColor(Color.RED);
        //                notificationChannel.setShowBadge(true);
        //                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        //                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 600});
        //                notificationChannel.setBypassDnd(true);
        //                notificationManager.createNotificationChannel(notificationChannel);
        //                builder2 = new NotificationCompat.Builder(context, channelId)
        //                        .setSmallIcon(smallIconId)
        ////                        .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(notificationContent))
        //                        .setGroup(group)
        //                        .setGroupSummary(true);
        //            } else {
        //                builder2 = new NotificationCompat.Builder(context)
        //                        .setSmallIcon(smallIconId)
        //                        //                        .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(notificationContent))
        //                        .setGroup(group)
        //                        .setGroupSummary(true);
        //            }
        //            notificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID, builder2.build());
        //        } else {
        //            notificationManager.cancel(NOTIFICATION_GROUP_SUMMARY_ID);
        //        }
    }

    fun cancel(notificationManager: NotificationManager?, id: Int) {
        notificationManager?.cancel(id)
    }

    fun cancelAll(notificationManager: NotificationManager?) {
        notificationManager?.cancelAll()
    }

    private fun getNewNotificationId(): Int {
        var notificationId = this.notificationId++
        if (notificationId == NOTIFICATION_GROUP_SUMMARY_ID) {
            notificationId = this.notificationId++
        }
        return notificationId
    }

    private fun getNotificationsCount(notificationManager: NotificationManager): Int {
        val statusBarNotifications = notificationManager.activeNotifications
        for (notification in statusBarNotifications) {
            if (notification.id == NOTIFICATION_GROUP_SUMMARY_ID) {
                return statusBarNotifications.size - 1
            }
        }
        return statusBarNotifications.size
    }

}