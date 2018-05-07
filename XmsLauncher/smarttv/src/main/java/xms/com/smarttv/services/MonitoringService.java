package xms.com.smarttv.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.eliotohme.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.socket.client.Socket;
import xms.com.smarttv.app.SmartTv;

public class MonitoringService extends IntentService {

    private Socket socket = SmartTv.getInstance().getSocket();
    public static String TAG_STREAM = "stream";
    public static String TAG_ACTIVITY = "activity";
    public static String EVENT_MONITORING = "Monitoring";
    private String stream;
    private String activity;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MonitoringService() {
        super("MonitoringService");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent.getStringExtra(TAG_STREAM) != null) {
            this.stream = intent.getStringExtra(TAG_STREAM);
        }
        if (intent.getStringExtra(TAG_ACTIVITY) != null) {
            this.activity = intent.getStringExtra(TAG_ACTIVITY);
        }

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        JSONObject monitor = new JSONObject();
        try {

            monitor.put("device", Realm.getDefaultInstance().where(User.class).findFirst().getRoom());
            if (this.stream != null) {
                monitor.put("stream", this.stream);
            }
            if (this.activity != null) {
                monitor.put("activity", this.activity);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(EVENT_MONITORING, monitor);
    }
}
