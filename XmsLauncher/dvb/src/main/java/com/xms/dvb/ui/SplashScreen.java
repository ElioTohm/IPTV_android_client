package com.xms.dvb.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eliotohme.data.Channel;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;
import com.xms.dvb.R;
import com.xms.dvb.XmlParser;
import com.xms.dvb.app.Preferences;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class SplashScreen extends Activity {
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadXmlTask().cancel(true);
        new checkChannelsLoaded().cancel(true);
        setContentView(R.layout.activity_splash);
        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        if (user == null || user.getAccess_token().equals(null) || user.getAccess_token().equals("")) {
            registerdevice();
        } else if (realm.where(Channel.class).count() > 0) {
            // Start a new thread that will download all the data
            new checkChannelsLoaded().execute();
            if (Preferences.getStartingUrl().equals("")) {
                new DownloadXmlTask().execute();
            } else {
                startActivity(new Intent(this, DVBPlayer.class));
                finish();
            }
        } else {
            chooseFetchStreamsMethod();
        }
    }

    /**
     * Register Device to set URL for xml channel list
     */
    private void registerserverXML() {
        // initalize dialog
        final View view = getLayoutInflater().inflate(R.layout.server_register_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
        dialog.setView(view);
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(false);
        dialog.setNegativeButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // get android id
                final EditText serverURI = view.findViewById(R.id.server_url);
                final EditText udpFileName =  view.findViewById(R.id.udpfilename);

                Preferences.setServerUrl(String.valueOf(serverURI.getText()));
                Preferences.setXmlFileName(String.valueOf(udpFileName.getText()));

                // Start a new thread that will download all the data
                new DownloadXmlTask().execute();
            }
        });
        dialog.show();
    }

    private class DownloadXmlTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                loadXmlFromNetwork();
            } catch (IOException e) {
                Log.e(TAG,"error connections");
                Handler handler =  new Handler(SplashScreen.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Connectiondialoghandler ();
                    }
                });

            } catch (XmlPullParserException e) {
                Log.e(TAG, "error parsing");
            }
            return null;
        }
    }

    // Uploads XML from , parses it,
    private void loadXmlFromNetwork() throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        XmlParser XmlParser = new XmlParser(SplashScreen.this);

        try {
            stream = downloadUrl(Preferences.getServerUrl()+Preferences.getXmlFileName());
            XmlParser.parse(stream);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
                startActivity(new Intent(SplashScreen.this, DVBPlayer.class));
            }
        }
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    private class checkChannelsLoaded extends AsyncTask<Void, Void, Void>  {
        @Override
        protected Void doInBackground(Void... voids) {
            Realm realm = Realm.getDefaultInstance();
            List<Channel> Unloaded_channels = realm.where(Channel.class).contains("name", "Unkown").findAll();
            if (Unloaded_channels.size() > 0 ) {
                Handler handler =  new Handler(SplashScreen.this.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(SplashScreen.this , "loading channel info...", Toast.LENGTH_LONG).show();
                    }
                });
                XmlParser channelXmlParser_resume = new XmlParser(SplashScreen.this);
                long allChannelSize = realm.where(Channel.class).count();
                int progress = 0;
                for (int i=0; i<Unloaded_channels.size(); i++) {
                    int total = (int) (100.0 * (i + allChannelSize - Unloaded_channels.size())  / allChannelSize);
                    if (progress < total) {
                        progress = total;
                        channelXmlParser_resume.getServiceName(Unloaded_channels.get(i).getStream(), String.valueOf(progress));
                    } else {
                        channelXmlParser_resume.getServiceName(Unloaded_channels.get(i).getStream(), "");
                    }
                }
            }
            realm.close();
            return null;
        }
    }

    private void Connectiondialoghandler () {
        new AlertDialog.Builder(SplashScreen.this)
                .setMessage("Can not connect please try again")
                .setCancelable(false)
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DownloadXmlTask().execute();
                    }
                })
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        registerserverXML();
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
                                    User user = new User();
                                    user.setId(response.body().getId());
                                    user.setAccess_token(response.body().getAccess_token());
                                    user.setToken_type(response.body().getToken_type());
                                    realm.insertOrUpdate(user);
                                }
                            });
                            chooseFetchStreamsMethod();
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

    private void chooseFetchStreamsMethod () {

        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
        dialog.setTitle("Search method");
        dialog.setMessage("Please select search method");
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(false);
        dialog.setNeutralButton("Online Search", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // get android id
                registerserverSearch();
            }
        })
        .setPositiveButton("XML file", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerserverXML();
            }
        });
        dialog.show();
    }

    private void registerserverSearch () {
        // initalize dialog
        final View view = getLayoutInflater().inflate(R.layout.search_stream_by_range, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
        dialog.setView(view);
        // set cancelable to true to be able to fix network before registery
        dialog.setCancelable(false);
        dialog.setNegativeButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                // get android id
                final EditText sUrl = view.findViewById(R.id.strating_url);
                final EditText prt =  view.findViewById(R.id.starting_port);
                final EditText channelNum = view.findViewById(R.id.number_of_channels);
                final EditText uhopes = view.findViewById(R.id.number_of_hope_udp);
                final EditText phope = view.findViewById(R.id.number_of_hope_port);
                String startUrl = String.valueOf(sUrl.getText());
                int port = Integer.parseInt(String.valueOf(prt.getText()));
                int channelNumber = Integer.parseInt(String.valueOf(channelNum.getText()));
                int uri_hopes = Integer.parseInt(String.valueOf(uhopes.getText()));
                int port_hope = Integer.parseInt(String.valueOf(phope.getText()));
                Preferences.setStartingUrl(startUrl);
                Preferences.setPORT(port);
                Preferences.setNumberOfChannels(channelNumber);
                Preferences.setUriHope(uri_hopes);
                Preferences.setPortHope(port_hope);

                final ArrayList<Channel> channels = new ArrayList<Channel>();
                int CHANNEL_NUMBER = 1;
                Channel initchannel = new Channel();
                initchannel.setStream_type(1);
                initchannel.setStream(startUrl + ":" + port);
                initchannel.setId(CHANNEL_NUMBER);
                initchannel.setName("Unkown");
                channels.add(initchannel);
                int lastindex = startUrl.lastIndexOf(".");
                int increamented  = Integer.parseInt(startUrl.substring(lastindex+1,
                        startUrl.length()));
                int increamnetedport = port;
                CHANNEL_NUMBER++;
                for (int i = 1; i < Integer.parseInt(String.valueOf(channelNumber)); i ++ ) {
                    Channel channel = new Channel();


                    increamented  = (increamented  + uri_hopes);
                    increamnetedport = increamnetedport + port_hope;
                    channel.setStream_type(1);
                    channel.setStream(startUrl.substring(0,lastindex)+ "." + increamented + ":" + increamnetedport);
                    channel.setId(CHANNEL_NUMBER);
                    channel.setName("Unkown");
                    channels.add(channel);
                    CHANNEL_NUMBER++;
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Channel.class);
                        realm.insert(channels);
                        // Start a new thread that will download all the data
                        new checkChannelsLoaded().cancel(true);
                        new checkChannelsLoaded().execute();
                        startActivity(new Intent(SplashScreen.this, DVBPlayer.class));
                        finish();
                    }
                });
            }
        });
        dialog.show();
    }

}
