package xms.com.smarttv.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eliotohme.data.Channel;
import com.eliotohme.data.HotelService;
import com.eliotohme.data.Movie;
import com.eliotohme.data.Section;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;
import com.eliotohme.data.network.ModelNetworkCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.Player.TVPlayerActivity;
import xms.com.smarttv.R;
import xms.com.smarttv.app.Preferences;

public class SplashScreen extends Activity implements ModelNetworkCallback {
    private Realm realm;
    private User user;
    private  String TKN_TYPE;
    private String TKN;
    private ApiInterface apiInterface= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        if (!Preferences.getServerUrl().equals("")) {
            user = realm.where(User.class).findFirst();
            // select user from database
            if (user != null && user.getAccess_token() != null) {
                // if user is found continue
                TKN_TYPE = user.getToken_type();
                TKN = user.getAccess_token();
                checkForUpdate();
            } else {
                // if user row is null register device
                registerdevice();
            }
        } else {
            // if SERVERURL row is empty register device
            registerdevice();
        }
    }

    /**
     * Register Device set token for api authentication
     */
    private void registerdevice () {
        // initalize dialog
        final View view = getLayoutInflater().inflate(R.layout.client_register_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
        dialog.setView(view);
        // get android id
        final EditText user_id = view.findViewById(R.id.text_ID);
        final EditText user_secret = view.findViewById(R.id.text_secret);
        final EditText serverURI = view.findViewById(R.id.server_url);
        serverURI.setText(getResources().getString(R.string.defaultDEVURL));

        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(false);
        dialog.setNegativeButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                Preferences.setServerUrl(String.valueOf(serverURI.getText()));
                // register client
                apiInterface = ApiService.createService(ApiInterface.class, String.valueOf(serverURI.getText()));

                Call<User> userCall = apiInterface.registerdevice(Integer.parseInt(user_id.getText().toString()),
                        user_secret.getText().toString());

                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull final Response<User> response) {
                        if (response.code() == 200 && response.body().getError() != 401) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    // save token
                                    TKN = response.body().getAccess_token();
                                    TKN_TYPE = response.body().getToken_type();
                                    realm.insertOrUpdate(response.body());
                                }
                            });
                            getStreams();
                        } else {
                            registerdevice();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        callError();
                    }
                });

            }
        });
        dialog.show();
    }

    /**
     * Get Channels, Movies and save them in database
     * function will always be called when device is turned on
     */
    private void getStreams () {
        apiInterface = ApiService.createService(ApiInterface.class, Preferences.getServerUrl(), TKN_TYPE, TKN);
        new Channel().getlist_network(apiInterface, this);
    }

    private void checkForUpdate () {
        Double appversion = null;
        try {
            PackageInfo appInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appversion =  Double.parseDouble(appInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        apiInterface = ApiService.createService(ApiInterface.class,Preferences.getServerUrl(), TKN_TYPE, TKN);
        Call<ResponseBody> call = apiInterface.checkUpdate(appversion);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.body() != null) {
                    if (!response.body().contentType().type().equals("text")) {
                        realm.close();
                        File apkpdate = new File(SplashScreen.this.getExternalCacheDir().getAbsolutePath() + "/xmslauncher.apk");
                        if (apkpdate.exists()) {
                            apkpdate.delete();
                        }
                        Toast.makeText(SplashScreen.this,"Downloading Updates...", Toast.LENGTH_LONG).show();
                        new SaveApk(SplashScreen.this).execute(response);
                    } else {
                        getStreams();
                    }
                } else {
                    getStreams();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("TEST", t.toString());
                callError();
            }
        });
    }

    @Override
    public void callPassed() {
        new Section().getlist_network(apiInterface);
        new Movie().getlist_network(apiInterface);
        new HotelService().getlist_network(apiInterface);
        // start TVplayer
        startActivity(new Intent(getApplication(), TVPlayerActivity.class));
        finish();
    }

    @Override
    public void callFailed() {
        registerdevice();
    }

    @Override
    public void callError() {

        new AlertDialog.Builder(SplashScreen.this)
                .setMessage("Can not connect please try again")
                .setCancelable(false)
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getStreams();
                    }
                })
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        registerdevice();
                    }
                })
                .setNeutralButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.android.tv.settings");
                        startActivity( LaunchIntent );
                    }
                })
                .show();
    }

    private static class SaveApk extends AsyncTask<Response<ResponseBody>, Void, Void> {

        private WeakReference<SplashScreen> activityReference;

        SaveApk(SplashScreen context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Response<ResponseBody>[] responses) {
            int count = responses.length;
            for (int i = 0; i < count; i++) {
                writeResponseBodyToDisk(responses[i].body());
                if (isCancelled()) break;
            }

            // start apk as intent to update code
            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                File apkpdate = new File(activityReference.get().getExternalCacheDir().getAbsolutePath() + "/xmslauncher.apk");
                final Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                promptInstall.setDataAndType(Uri.fromFile(apkpdate), "application/vnd.android.package-archive");
                promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityReference.get().startActivity(promptInstall);
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(activityReference.get())
                                .setMessage("XMS launcher Could not update please download latest version manually")
                                .setCancelable(false)
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        activityReference.get().finish();
                                    }
                                })
                                .show();
                    }
                });
            }
            return null;
        }

        private void writeResponseBodyToDisk(ResponseBody body) {
            try {
                File apkpdate = new File(activityReference.get().getExternalCacheDir().getAbsolutePath() + "/xmslauncher.apk");

                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    byte[] fileReader = new byte[4096];

                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;

                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(apkpdate);

                    while (true) {
                        int read = inputStream.read(fileReader);

                        if (read == -1) {
                            break;
                        }

                        outputStream.write(fileReader, 0, read);

                        fileSizeDownloaded += read;

                        Log.d("TEST", "file download: " + (fileSize/fileSizeDownloaded) + "%");
                    }

                    outputStream.flush();

                } catch (IOException e) {
                    Log.e("test", e.toString());
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                Log.d("TEST", "writeResponseBodyToDisk: " + e);
            }
        }

    }

}
