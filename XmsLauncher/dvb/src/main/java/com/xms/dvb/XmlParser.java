package com.xms.dvb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Stream;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.xms.dvb.app.Preferences;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XmlParser {
    private static final String ns = null;
    private int CHANNEL_NUMBER = 1;
    private Context context;
    private List<Channel> channels = new ArrayList<Channel>();
    private String appVersion;
    private String XML_VERSION;
    public XmlParser(Context context) {
        this.context = context;
    }

    public void  parse(InputStream in) throws XmlPullParserException, IOException, PackageManager.NameNotFoundException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
            String currentAppVersion = null;
            try {
                PackageInfo appInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                currentAppVersion =  appInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (!XML_VERSION.equals(appVersion)) {
                Handler handler =  new Handler(context.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(context , "Loading Channel info", Toast.LENGTH_LONG).show();
                    }
                });
                Preferences.setXmlVersion(XML_VERSION);
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Channel.class);
                        realm.insertOrUpdate(channels);
                    }
                });
                int progress = 0;
                for (int i = 0; i < channels.size(); i++) {
                    int total = (int) (100.0 * i / channels.size());
                    if (progress < total) {
                        progress = total;
                        getServiceName(channels.get(i).getStream().getVid_stream(), String.valueOf(progress));
                    } else {
                        getServiceName(channels.get(i).getStream().getVid_stream(), "");
                    }
                }
            }
            if (!currentAppVersion.equals(appVersion)) {
                downloadapk();
            }
        } finally {
            in.close();
        }
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Group");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Data")) {
                channels.add(readEntry(parser));
            } else if (name.equals("AppVersion")) {
                appVersion = readAppVersion(parser);
            } else if (name.equals("Version")) {
                XML_VERSION = readVersion(parser);
            }else{
                skip(parser);
            }
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Channel readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Data");
        String ip = null;
        String port = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("ip")) {
                ip = readIP(parser);
            } else if (name.equals("port")) {
                port = readPort(parser);
            } else {
                skip(parser);
            }
        }
        Channel channel = new Channel();
        Stream stream = new Stream("udp://@"+ip+":"+port, null, 1);
//        channel.setStream_type(1);
        channel.setStream(stream);
        channel.setId(CHANNEL_NUMBER);
        channel.setName("Unkown");
        CHANNEL_NUMBER++;
        return channel;
    }

    // Processes ip tags in the feed.
    private String readIP(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ip");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "ip");
        return title;
    }

    // Processes port tags in the feed.
    private String readPort(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "port");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "port");
        return summary;
    }

    // Processes Version tags in the feed.
    private String readVersion(XmlPullParser parser) throws IOException, XmlPullParserException {
        // Starts by looking for the entry tag
        parser.require(XmlPullParser.START_TAG, ns, "Version");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Version");
        return summary;
    }

    // Processes AppVersion tags in the feed.
    private String readAppVersion(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "AppVersion");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "AppVersion");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public void getServiceName(final String stream, final String progresspercentage) {
        FFmpeg ffmpeg = FFmpeg.getInstance(this.context);

        String[] cmd = {"-i", stream, "-hide_banner"};
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }
            });
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onProgress(String message) {
                    if (message.contains("service_name")) {
                        final String name = message.split(":")[1];
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Channel channel = realm.where(Channel.class)
                                        .equalTo("stream.vid_stream", stream)
                                        .contains("name", "Unkown")
                                        .findFirst();
                                if (channel != null) {
                                    channel.setName(name);
                                }

                            }
                        });
                        if (!progresspercentage.equals("")) {
                            Handler handler =  new Handler(context.getMainLooper());
                            handler.post( new Runnable(){
                                public void run(){
                                    Toast.makeText(context , "Loaded " + progresspercentage + "%", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            });
        } catch (FFmpegCommandAlreadyRunningException | FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void downloadapk () {
        ApiInterface apiInterface = ApiService.createService(ApiInterface.class,Preferences.getServerUrl());
        Call<ResponseBody> call = apiInterface.downloadFileWithDynamicUrlSync(Preferences.getServerUrl()+"/xmsdvb.apk");

        Handler handler =  new Handler(context.getMainLooper());
        handler.post( new Runnable(){
            public void run(){
                Toast.makeText(context , "Updating Application please do not turn off device", Toast.LENGTH_LONG).show();
            }
        });

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (!response.body().contentType().type().equals("text")) {
                    File apkpdate = new File(context.getExternalCacheDir().getAbsolutePath() + "/xmsdvb.apk");
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

                                File apkpdate = new File(context.getExternalCacheDir().getAbsolutePath() + "/xmsdvb.apk");
                                Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                                promptInstall.setDataAndType(Uri.fromFile(apkpdate), "application/vnd.android.package-archive");
                                promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(promptInstall);
                            } catch (Exception e) {
                                new AlertDialog.Builder(context)
                                        .setMessage("XMS launcher Could not update please download latest version manually")
                                        .setCancelable(false)
                                        .show();
                            }
                            return null;
                        }
                    }.execute();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ELIO", t.toString());
            }
        });
    }

    private void writeResponseBodyToDisk(ResponseBody body) {
        try {
            File apkpdate = new File(context.getExternalCacheDir().getAbsolutePath() + "/xmsdvb.apk");

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

                    Log.d("ELIO", "file download: " + (fileSize/fileSizeDownloaded) + "%");
                }

                outputStream.flush();

            } catch (IOException e) {
                Log.e("ELIO", e.toString());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d("ELIO", "writeResponseBodyToDisk: " + e);
        }
    }
}
