package com.marianhello.bgloc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.marianhello.backgroundgeolocation.R;
import com.marianhello.logging.LoggerManager;

public class NotificationHelper {
    public static final String SERVICE_CHANNEL_ID = "bglocservice";
    // https://github.com/nishkarsh/android-permissions/blob/master/src/main/java/com/intentfilter/androidpermissions/services/NotificationService.java#L15
    public static final String ANDROID_PERMISSIONS_CHANNEL_ID = "android-permissions";

    public static final String SYNC_CHANNEL_ID = "syncservice";
    public static final String SYNC_CHANNEL_NAME = "Sync Service";
    public static final String SYNC_CHANNEL_DESCRIPTION = "Shows sync progress";

    public static class NotificationFactory {
        private Context mContext;
        private ResourceResolver mResolver;

        private org.slf4j.Logger logger;

        public NotificationFactory(Context context) {
            mContext = context;
            mResolver = ResourceResolver.newInstance(context);
            logger = LoggerManager.getLogger(NotificationFactory.class);
        }

        private Integer parseNotificationIconColor(String color) {
            int iconColor = 0;
            if (color != null) {
                try {
                    iconColor = Color.parseColor(color);
                } catch (IllegalArgumentException e) {
                    logger.error("Couldn't parse color from android options");
                }
            }
            return iconColor;
        }

        public Notification getNotification(String title, String text, String largeIcon, String smallIcon, String color) {
            Context appContext = mContext.getApplicationContext();
            String packageName = appContext.getPackageName();
            Intent i = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    appContext,
                    1,
                    i,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );


            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the notification channel.
                //notificationChannel.setDescription("Channel description");
                //notificationChannel.enableLights(true);
                //notificationChannel.setVibrationPattern(new long[]{0L});
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
            Notification mNotification = builder
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .build();

            notificationManager.notify(/*notification id*/1, mNotification);

            return mNotification;

        }
    }

    public static void registerAllChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = ResourceResolver.newInstance(context).getString(("app_name"));
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(createServiceChannel(appName));
            notificationManager.createNotificationChannel(createSyncChannel());
            notificationManager.createNotificationChannel(createAndroidPermissionsChannel(appName));
        }
    }

    public static void registerServiceChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = ResourceResolver.newInstance(context).getString(("app_name"));
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(createServiceChannel(appName));
        }
    }

    public static void registerSyncChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(createSyncChannel());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel createServiceChannel(CharSequence name) {
        NotificationChannel channel = new NotificationChannel(SERVICE_CHANNEL_ID, name, android.app.NotificationManager.IMPORTANCE_LOW);
        channel.enableVibration(false);
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel createSyncChannel() {
        NotificationChannel channel = new NotificationChannel(SYNC_CHANNEL_ID, SYNC_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(SYNC_CHANNEL_DESCRIPTION);
        channel.enableVibration(false);
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel createAndroidPermissionsChannel(CharSequence name) {
        NotificationChannel channel = new NotificationChannel(ANDROID_PERMISSIONS_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(false);
        return channel;
    }
}
