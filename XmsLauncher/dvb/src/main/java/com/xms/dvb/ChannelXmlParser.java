package com.xms.dvb;

import android.content.Context;
import android.os.Handler;
import android.util.Xml;
import android.widget.Toast;

import com.eliotohme.data.Channel;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ChannelXmlParser {
    private static final String ns = null;
    private int CHANNEL_NUMBER = 1;
    private Context context;
    private List<Channel> channels;
    public ChannelXmlParser(Context context) {
        this.context = context;
    }

    public void  parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            channels =  readFeed(parser);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(Channel.class);
                    realm.insertOrUpdate(channels);
                }
            });
            int progress = 0;
            for (int i=0; i<channels.size(); i++) {
                int total = (int) (100.0 * i / channels.size());
                if (progress < total) {
                    progress = total;
                    getServiceName(channels.get(i).getStream(), String.valueOf(progress));
                } else {
                    getServiceName(channels.get(i).getStream(), "");
                }

            }
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "Group");
        Handler handler =  new Handler(context.getMainLooper());
        handler.post( new Runnable(){
            public void run(){
                Toast.makeText(context , "Loading Channel info", Toast.LENGTH_LONG).show();
            }
        });
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Data")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
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

        channel.setStream_type(1);
        channel.setStream("udp://@"+ip+":"+port);
        channel.setId(CHANNEL_NUMBER);
        channel.setName("Channel " + CHANNEL_NUMBER);
        CHANNEL_NUMBER++;
        return channel;
    }

    // Processes title tags in the feed.
    private String readIP(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ip");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "ip");
        return title;
    }

    // Processes summary tags in the feed.
    private String readPort(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "port");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "port");
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

    public void getServiceName (final String stream, final String progresspercentage) {
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
                public void onStart() {}

                @Override
                public void onProgress(String message) {
                    if (message.contains("service_name")) {
                        final String name = message.split(":")[1];
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Channel channel = realm.where(Channel.class)
                                        .equalTo("stream", stream)
                                        .contains("name", "Channel")
                                        .findFirst();
                                channel.setName(name);
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

                @Override
                public void onFailure(String message) {}

                @Override
                public void onSuccess(String message) {}

                @Override
                public void onFinish() {}

            });
        } catch (FFmpegCommandAlreadyRunningException | FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
