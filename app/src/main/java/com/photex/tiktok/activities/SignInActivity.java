package com.photex.tiktok.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.Gson;
import com.photex.tiktok.R;
import com.photex.tiktok.models.AppUser;
import com.photex.tiktok.models.UserInfo;
import com.photex.tiktok.models.restmodels.UserSignIn;
import com.photex.tiktok.models.restmodels.UserSignUpInfo;
import com.photex.tiktok.registration.RegistrationIntentService;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.CommonUtils;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public final String TAG = SignInActivity.class.getSimpleName();
    private final int RC_SIGN_IN = 1;
    private final int USER_DETAIL = 2;
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 3;
    private final int MAIN_ACTIVITY_REQUEST = 4;

    SignInButton signInButton;

    GoogleSignInAccount acct;
    private GoogleApiClient mGoogleApiClient;
    //    private ProgressDialog mProgressDialog;
    private ProgressWheel mProgressWheel;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private boolean isBroadCastRegistered;

    GoogleSignInClient mGoogleSignInClient;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*CommonUtils.transparentStatusBar(getActivity());*/
        setContentView(R.layout.activity_sign_in);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        intializeData();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(Constants.TOKEN) &&
                        intent.getStringExtra(Constants.TOKEN) != null) {

                    if (acct != null) {
                        String androidId = Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID);

                        String currentdate = "";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        currentdate = sdf.format(new Date());

                        AppUser appUser = new AppUser();
                        appUser.setGoogle_id(acct.getId());
                        appUser.setPicture_url(acct.getPhotoUrl() + "");
                        appUser.setUser_email_address(acct.getEmail());
                        appUser.setTime_and_date(currentdate);
                        appUser.setFcmkey(intent.getStringExtra(Constants.TOKEN));

                        SettingManager.setFcmToken(SignInActivity.this, intent.getStringExtra(Constants.TOKEN));
                        SettingManager.setUserEmail(SignInActivity.this, acct.getEmail());
                        SettingManager.setGoogleToken(SignInActivity.this, acct.getIdToken());

                        Log.i("googleID", acct.getId());
                        if (acct.getDisplayName() != null) {
                            Log.i("UserDisplayName", acct.getDisplayName() + "");
                            if (acct.getDisplayName().contains("@")) {
                                final String spliteEmail[] = acct.getDisplayName().split("@");
                                SettingManager.setUserName(SignInActivity.this, spliteEmail[0]);
                                appUser.setUser_name(spliteEmail[0]);
                            } else {
                                appUser.setUser_name(acct.getDisplayName());
                                SettingManager.setUserName(SignInActivity.this, acct.getDisplayName());
                            }

                        } else if (acct.getGivenName() != null) {
                            SettingManager.setUserName(SignInActivity.this, acct.getGivenName());
                            appUser.setUser_name(acct.getGivenName());
                        } else if (acct.getFamilyName() != null) {
                            SettingManager.setUserName(SignInActivity.this, acct.getFamilyName());
                            appUser.setUser_name(acct.getFamilyName());
                        } else {
                            if (acct.getEmail() != null) {
                                final String spliteEmail[] = acct.getEmail().split("@");
                                SettingManager.setUserName(SignInActivity.this, spliteEmail[0]);
                                appUser.setUser_name(spliteEmail[0]);
                            }
                        }
                        userSignIn(appUser);
                    } else {
                        Toast.makeText(SignInActivity.this,
                                "Some thing goes wrong ,try again", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SignInActivity.this,
                            "Some thing goes wrong ,try again", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void intializeData() {
        // deep linking with Firebase
        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent());

        if (!SettingManager.getUserEmail(this).isEmpty()) {
            startActivityForResult(new Intent(SignInActivity.this, MainActivity.class), MAIN_ACTIVITY_REQUEST);
        } else {
            initializeView();
            initializeGmailLogin();
        }
    }

    private void initializeView() {


        mProgressWheel = findViewById(R.id.user_signin_progress);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
    }

    private void initializeGmailLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            if (acct != null) {

                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(SignInActivity.this, RegistrationIntentService.class);
                    startService(intent);
                } else {
                    Toast.makeText(SignInActivity.this, "Update your Google Play Services then try again", Toast.LENGTH_LONG).show();
                    progressBarCanaclation();
                }

            } else {
                progressBarCanaclation();
                Toast.makeText(SignInActivity.this,
                        "Google SignIn problem... Please! try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressBarCanaclation();
            Toast.makeText(SignInActivity.this,
                    "Google SignIn problem... Please! try again", Toast.LENGTH_SHORT).show();
        }

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account =
                    completedTask.getResult(ApiException.class);


            // Signed in successfully, show authenticated UI.
            acct = account;
            if (acct != null) {

                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(SignInActivity.this, RegistrationIntentService.class);
                    startService(intent);
                } else {
                    Toast.makeText(SignInActivity.this, "Update your Google Play Services then try again", Toast.LENGTH_LONG).show();
                    progressBarCanaclation();
                }

            } else {
                progressBarCanaclation();
                Toast.makeText(SignInActivity.this,
                        "Google SignIn problem... Please! try again", Toast.LENGTH_SHORT).show();
            }

            // Signed in successfully, show authenticated UI.
            /*updateUI(account);*/
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            progressBarCanaclation();
            Toast.makeText(SignInActivity.this,
                    "Google SignIn problem... Please! try again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("device", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void progressBarCanaclation() {
        if (mProgressWheel != null)
            mProgressWheel.setVisibility(View.GONE);
    }

    private void userSignIn(final AppUser appUser) {
        UserSignIn userSignIn = new UserSignIn();
        userSignIn.setEmailId(appUser.getUser_email_address());

        Call<String> call = new RestClient(Constants.BASE_URL, SignInActivity.this)
                .get().userSignIn(userSignIn);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("testing", "testing " + response.body());
                if (response.body() != null && response.isSuccessful()) {
                    JSONObject jsonObject;
                    boolean success = false;
                    UserInfo userInfo = new UserInfo();
                    String result = response.body();

                    try {
                        jsonObject = new JSONObject(result);
                        success = jsonObject.getBoolean("success");

                        if (!success) {
                            // signup new user
                            if (appUser.getUser_name() != null) {

                                signUpUser(
                                        appUser.getUser_email_address(),
                                        appUser.getUser_name(),
                                        "",
                                        "Not Specified",
                                        appUser.getUser_email_address().split("@")[0] + ".jpeg",
                                        appUser.getPicture_url(),
                                        appUser.getFcmkey()
                                );
                            }
                        } else {

                            String serverToken = jsonObject.getString("token");
                            if (jsonObject.getString("user") != null) {
                                Gson gson = new Gson();
                                userInfo = gson.fromJson(jsonObject.getString("user"), UserInfo.class);

                            }
                            userInfo.setToken(serverToken);

                            SettingManager.setUserId(SignInActivity.this, userInfo.get_id());
                            SettingManager.setUserEmail(SignInActivity.this, appUser.getUser_email_address());
                            SettingManager.setUserName(SignInActivity.this, userInfo.getUserName());
                            SettingManager.setUserFolderName(SignInActivity.this, userInfo.getFolderName());
                            SettingManager.setUserFullName(SignInActivity.this, userInfo.getFullName());
                            SettingManager.setServerToken(SignInActivity.this, userInfo.getToken());
                            if (userInfo.getDisplayPicture() != null)
                                SettingManager.setUserPictureURL(SignInActivity.this, userInfo.getFolderName() + "/" + userInfo.getDisplayPicture());
                            SettingManager.setProfilePictureURL(SignInActivity.this, appUser.getPicture_url());

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.putExtra(Constants.UPLOAD_PHOTO, true);
                            startActivity(intent);
                            progressBarCanaclation();
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SignInActivity.this, "Some thing goes wrong, Please try again!", Toast.LENGTH_LONG).show();
                        progressBarCanaclation();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "Some thing goes wrong, Please try again!", Toast.LENGTH_LONG).show();
                    progressBarCanaclation();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("testing", "onFailure");
                Toast.makeText(SignInActivity.this, "Some thing goes wrong, Please try again!", Toast.LENGTH_LONG).show();
                progressBarCanaclation();
            }
        });
    }

    private void signUpUser(final String userEmail,
                            String fullName,
                            String phoneNumber,
                            String gender,
                            String imageName,
                            final String imageUrl,
                            String fcmkey) {

        if (!userEmail.equals("")) {

            UserSignUpInfo userSignUpInfo = new UserSignUpInfo();
            userSignUpInfo.setFullName(fullName);
            userSignUpInfo.setUserName(userEmail.split("@")[0]);
            userSignUpInfo.setBio("");
            userSignUpInfo.setEmailId(userEmail);
            userSignUpInfo.setPhoneNo(phoneNumber);
            userSignUpInfo.setDisplayPicture(imageName);
            userSignUpInfo.setGender(gender);
            userSignUpInfo.setFcmKey(fcmkey);

            Call<String> call = new RestClient(Constants.BASE_URL, SignInActivity.this).get()
                    .userSignUp(userSignUpInfo);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String result = response.body();
                        Gson gson = new Gson();
                        UserInfo userSignInResponse = null;
                        JSONObject userJson;

                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            userJson = jsonObject.getJSONObject("user_id");
                            String serverToken = jsonObject.getString("token");
                            userSignInResponse = gson.fromJson(userJson.toString(), UserInfo.class);
                            userSignInResponse.setToken(serverToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (userSignInResponse != null && userSignInResponse.get_id() != null) {
                            SettingManager.setUserEmail(SignInActivity.this, userEmail);
                            SettingManager.setUserId(SignInActivity.this, userSignInResponse.get_id());
                            SettingManager.setUserFolderName(SignInActivity.this, userSignInResponse.getFolderName());
                            SettingManager.setUserFullName(SignInActivity.this, userSignInResponse.getFullName());
                            SettingManager.setUserName(SignInActivity.this, userSignInResponse.getUserName());
                            SettingManager.setUserPictureVersion(SignInActivity.this, userSignInResponse.getDisplayPictureLastModified());
                            SettingManager.setServerToken(SignInActivity.this, userSignInResponse.getToken());
                            SettingManager.setProfilePictureURL(SignInActivity.this, imageUrl);

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.putExtra(Constants.UPLOAD_PHOTO, true);
                            startActivity(intent);
                            finish();

                        } else {
                            progressBarCanaclation();
                            Toast.makeText(SignInActivity.this, "User is not Registered ,Please Try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBarCanaclation();
                        Toast.makeText(SignInActivity.this, "User is not Registered ,Please Try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(SignInActivity.this, "Please Try again", Toast.LENGTH_LONG).show();
                    progressBarCanaclation();
                }
            });
        } else {
            Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, SignInActivity.class));
        }
    }

    private void userDisplayPicture(String userEmail, String imageUrl) {

        final String spliteEmail[] = userEmail.split("@");
        SettingManager.setUserPictureURL(
                SignInActivity.this,
                SettingManager.getUserFolderName(SignInActivity.this) + "/" + spliteEmail[0] + ".jpeg");

        if (imageUrl != null
                && !imageUrl.isEmpty()
                && !imageUrl.equals("null")) {

            Glide.with(SignInActivity.this)
                    .asBitmap()
                    .load(imageUrl)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            e.printStackTrace();
                            intentMainActivity();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            File file = Util.storeBitmap(SignInActivity.this, bitmap, Constants.FULL_PHOTO);
                            if (file != null && file.exists()) {
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                intent.putExtra(Constants.IMAGE_FILE_PATH, file.getAbsolutePath());
                                intent.putExtra(Constants.UPLOAD_PHOTO, true);
                                startActivity(intent);
                                finish();
                            } else {
                                intentMainActivity();
                            }
                            return false;
                        }
                    }).into(250, 250);
        } else {
            intentMainActivity();
        }
    }

    private void intentMainActivity() {
        progressBarCanaclation();
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isBroadCastRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.REGISTRATION_COMPLETE));
            isBroadCastRegistered = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        progressBarCanaclation();
        isBroadCastRegistered = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

/*
        if (resultCode != RESULT_CANCELED)
*/
        if (requestCode == RC_SIGN_IN) {
     /*       Task<GoogleSignInAccount> task = GoogleSignIn.
                        getSignedInAccountFromIntent(data);
            try {
                mProgressWheel.setVisibility(View.VISIBLE);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null)
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

                progressBarCanaclation();
                Toast.makeText(SignInActivity.this,
                        "Google SignIn problem..." +
                                " Please! try again", Toast.LENGTH_LONG).show();
                // ...
            }*/


            if (resultCode == RESULT_OK && data != null) {
                mProgressWheel.setVisibility(View.VISIBLE);

     /*           GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);*/

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } else {
                progressBarCanaclation();
                Toast.makeText(SignInActivity.this,
                        "Google SignIn problem... Please! try again", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MAIN_ACTIVITY_REQUEST) {
            finish();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(SignInActivity.this, "Some thing goes wrong ,please check your internet",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        progressBarCanaclation();
    }

    @Override
    public void onBackPressed() {
        progressBarCanaclation();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view == signInButton) {
            if (Util.isNetworkAvailable(SignInActivity.this)) {
             /*   Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(
                        mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);*/

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

               /* Intent signInIntent = mGoogleApiClient.;
                startActivityForResult(signInIntent, RC_SIGN_IN);*/

               /* AppUser appUser = new AppUser() ;
                appUser.setUser_email_address("silence0sea@gmail.com");
                userSignIn(appUser);*/
            } else {
                Util.internetNotAvailableDialouge(SignInActivity.this);
            }
        }
    }


    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    progressBarCanaclation();
                                    if (checkPlayServices()) {
                                        // Start IntentService to register this application with GCM.
                                        Intent intent = new Intent(SignInActivity.this, RegistrationIntentService.class);
                                        startService(intent);
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Update your Google Play Services then try again", Toast.LENGTH_LONG).show();
                                        progressBarCanaclation();
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG, "signInWithCredential:fail");


                                }

                                // [START_EXCLUDE]

                                // [END_EXCLUDE]
                            }
                        });
    }
    // [END auth_with_google]

    public Activity getActivity() {
        return SignInActivity.this;
    }
}
