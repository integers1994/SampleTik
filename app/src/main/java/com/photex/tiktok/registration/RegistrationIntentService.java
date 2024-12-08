package com.photex.tiktok.registration;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.photex.tiktok.R;
import com.photex.tiktok.utils.Constants;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "registration";

    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
            // Get token
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
                            registrationComplete.putExtra(Constants.TOKEN, token);
                            LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(registrationComplete);

                            // Log and toast
                            String msg = getString(R.string.msg_token_fmt, token);
                            Log.d(TAG, msg);
                        }
                    });
    }
}
