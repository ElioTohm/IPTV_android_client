package com.xms.dvb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eliotohme.data.Channel;
import com.xms.dvb.app.Preferences;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.realm.Realm;

import static android.content.ContentValues.TAG;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Realm realm = Realm.getDefaultInstance();
        if (!Preferences.getServerUrl().equals("") && realm.where(Channel.class).count() > 0) {
            // Start a new thread that will download all the data
            new DownloadXmlTask().execute();
            new checkChannelsLoaded().execute();
        } else {
            registerdevice();
        }
    }

    /**
     * Register Device to set URL for xml channel list
     */
    private void registerdevice() {
        // initalize dialog
        final View view = getLayoutInflater().inflate(R.layout.client_register_dialog, null);

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
            }
        }
        startActivity(new Intent(this, DVBPlayer.class));
        finish();
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
                        Toast.makeText(SplashScreen.this , "Resuming loading channel info...", Toast.LENGTH_LONG).show();
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
}
