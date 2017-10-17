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
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.Player.TVPlayerActivity;
import xms.com.smarttv.R;
import xms.com.smarttv.app.Preferences;

public class SplashScreen extends Activity {
    private Realm realm;
    private User user;
    private  String TKN_TYPE;
    private String TKN;
    private int USER_ID;

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
                USER_ID = user.getId();

                // update if not getchannels
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
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(false);
        dialog.setNegativeButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // get android id
                final EditText user_id = view.findViewById(R.id.text_ID);
                final EditText user_secret = view.findViewById(R.id.text_secret);
                final EditText serverURI = view.findViewById(R.id.server_url);

                Preferences.setServerUrl(String.valueOf(serverURI.getText()));

                // register client
                final ApiInterface apiInterface = ApiService.createService(ApiInterface.class, String.valueOf(serverURI.getText()));

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
                                    User user = new User();
                                    user.setId(response.body().getId());
                                    user.setAccess_token(response.body().getAccess_token());
                                    user.setToken_type(response.body().getToken_type());
                                    realm.insertOrUpdate(user);
                                }
                            });
                            getChannels();
                        } else {
                            registerdevice();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        Connectiondialoghandler();
                    }
                });

            }
        });
        dialog.show();
    }

    /**
     * Get Channels and save them in database
     * function will always be called when device is turned on
     */
    private void getChannels () {
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class, Preferences.getServerUrl(), TKN_TYPE, TKN);

        Call<List<Channel>> channelCall = apiInterface.getChannel();
        channelCall.enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(@NonNull Call<List<Channel>> call, @NonNull final Response<List<Channel>> response) {
                Realm backgroundRealm = Realm.getDefaultInstance();
                backgroundRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                    if (response.code() == 200) {
                        realm.delete(Channel.class);
                        realm.insertOrUpdate(response.body());
                    } else {
                        realm.deleteAll();
                        registerdevice();
                    }
                    }
                });
                if (response.code() == 200) {
                    try {
                        getClientInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startTVplayer();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Channel>> call, @NonNull Throwable t) {
                Log.e("TEST", String.valueOf(t));
                Connectiondialoghandler();
            }
        });
    }

    /**
     * Start Tvplayer by default
     */
    private void startTVplayer (){
        // start TVplayer
        startActivity(new Intent(getApplication(), TVPlayerActivity.class));
    }

    /**
     * checks Client info for change
     * f there is change show the onboarding activity
     * @throws IOException
     */
    private void getClientInfo() throws IOException {
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class,Preferences.getServerUrl(), TKN_TYPE, TKN);
        Call<Client> clientCall = apiInterface.getClientInfo(USER_ID);
        clientCall.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull final Response<Client> response) {
                Realm subrealm = Realm.getDefaultInstance();
                if (response.code() == 200 && (subrealm.where(Client.class).findFirst() ==  null
                        || !response.body().getEmail().equals(subrealm.where(Client.class).findFirst().getEmail()))) {

                    subrealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(Client.class);
                            realm.insert(response.body());
                        }
                    });

                    // This is the first time running the app, let's go to onboarding
                    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", response.body().getName());
                    intent.putExtra("welcome_message", response.body().getWelcomeMessage());
                    intent.putExtra("welcome_image", response.body().getWelcomeImage());
                    getApplication().startActivity(intent);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {}
        });
    }

    private void Connectiondialoghandler () {
        new AlertDialog.Builder(SplashScreen.this)
                .setMessage("Can not connect please try again")
                .setCancelable(false)
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    getChannels();
                    }
                })
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    registerdevice();
                    }
                })
                .setNeutralButton("Settigns", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                })
                .show();
    }

    private void checkForUpdate () {
        Double appversion = null;
        try {
            PackageInfo appInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appversion =  Double.parseDouble(appInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ApiInterface apiInterface = ApiService.createService(ApiInterface.class,Preferences.getServerUrl(), TKN_TYPE, TKN);
        Call<ResponseBody> call = apiInterface.checkUpdate(appversion);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (!response.body().contentType().type().equals("text")) {
                    File apkpdate = new File(SplashScreen.this.getExternalCacheDir().getAbsolutePath() + "/xmslauncher.apk");
                    if (apkpdate.exists()) {
                        apkpdate.delete();
                    }
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            writeResponseBodyToDisk(response.body());
                            // start apk as intent to update code
                            try {
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());

                                File apkpdate = new File(SplashScreen.this.getExternalCacheDir().getAbsolutePath() + "/xmslauncher.apk");
                                Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                                promptInstall.setDataAndType(Uri.fromFile(apkpdate), "application/vnd.android.package-archive");
                                promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(promptInstall);
                            } catch (Exception e) {
                                new AlertDialog.Builder(SplashScreen.this)
                                        .setMessage("XMS launcher Could not update please download latest version manually")
                                        .setCancelable(false)
                                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                SplashScreen.this.finish();
                                            }
                                        })
                                        .show();
                            }

                            return null;
                        }
                    }.execute();
                } else {
                    getChannels();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("TEST", t.toString());
            }
        });
    }
    private void writeResponseBodyToDisk(ResponseBody body) {
        try {
            File apkpdate = new File(SplashScreen.this.getExternalCacheDir().getAbsolutePath() + "/xmslauncher.apk");

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

        }
    }
}
