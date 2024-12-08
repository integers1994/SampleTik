package com.photex.tiktok.registration;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.photex.tiktok.R;
import com.photex.tiktok.activities.MainActivity;
import com.photex.tiktok.models.restmodels.FcmToken;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationCompat.Builder mBuilder;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i("tokenRecived", token);

        SettingManager.setFcmToken(this, token);
        if (!SettingManager.getUserId(this).isEmpty()) {
            updateFCMToken();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String message = data.get("message");
        Log.w("notiRecived", "notification arrived = " + message);

        sendNotification(message);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper = new NotificationHelper(this);
        mBuilder = helper.getNotificationBuilder();
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(getResources().getString(R.string.notificationText));

        Notification notification = mBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

    void updateFCMToken() {
        FcmToken fcmToken = new FcmToken();
        fcmToken.setFcmKey(SettingManager.getFcmToken(this));
        fcmToken.setUserId(SettingManager.getUserId(this));

        Call<String> call = new RestClient(Constants.BASE_URL, this)
                .get()
                .updateFcmToken(fcmToken);

        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // null pointer exception

                if (response != null && response.message() != null
                        && !response.message().isEmpty())
                    Log.e("updateFCMToken", "FCM token is updated" + response.message());
            }

            @Override
            public void onFinallyFail() {

            }
        });
    }

}
