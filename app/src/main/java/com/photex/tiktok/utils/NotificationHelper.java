/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.photex.tiktok.utils;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.photex.tiktok.R;
import com.photex.tiktok.services.UploadProfilePictureService;

/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationHelper(Context ctx) {
        super(ctx);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan1 = null;
            chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    getString(R.string.default_notification_channel_id), NotificationManager.IMPORTANCE_LOW);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(chan1);

/*            NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                    getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH);
            chan2.setLightColor(Color.BLUE);
            chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan2);*/
        }

    }


    public NotificationCompat.Builder getNotificationBuilder() {

        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                    PRIMARY_CHANNEL);
        } else {
            mBuilder = new NotificationCompat.Builder(getApplicationContext());
        }
        return mBuilder;
    }

    public NotificationCompat.Builder getNotificationForPictureUpload(
            String title,
            String body,
            int smallIcon,
            int notificationTimeId) {

        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                    PRIMARY_CHANNEL);
        } else {
            mBuilder = new NotificationCompat.Builder(getApplicationContext());
        }

        mBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setOngoing(true)
                .setProgress(0, 0, true);
        /*To stop service for cancel button */
        Intent stopService = new Intent(this,
                UploadProfilePictureService.class);
        stopService.setAction(Constants.STOP_PROFILE_PICTURE_SERVICE);

        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this,
                notificationTimeId,
                stopService,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.menu_cancel,
                getString(R.string.cancel), pendingIntentYes);


        return mBuilder;
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
