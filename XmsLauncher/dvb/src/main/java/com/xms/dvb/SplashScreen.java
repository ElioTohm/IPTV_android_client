package com.xms.dvb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.xms.dvb.app.Preferences;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (!Preferences.getServerUrl().equals("")) {
            startActivity(new Intent(this, DVBPlayer.class));
            finish();
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

                Preferences.setServerUrl(String.valueOf(serverURI.getText()));

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
        ChannelXmlParser channelXmlParser = new ChannelXmlParser(SplashScreen.this);

        try {
            stream = downloadUrl(Preferences.getServerUrl());
            channelXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
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

}
