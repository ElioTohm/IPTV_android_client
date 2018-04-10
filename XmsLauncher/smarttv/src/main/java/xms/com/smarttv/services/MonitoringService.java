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
    private String TAG = "Watching";
    private String EVENT_MONITORING = "Monitoring";
    private String channelName;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MonitoringService() {
        super("MonitoringService");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        this.channelName = intent.getStringExtra("Watching");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG, "ECHO START...");
        JSONObject monitor = new JSONObject();
        try {
            monitor.put("device", Realm.getDefaultInstance().where(User.class).findFirst().getRoom());
            monitor.put("stream", this.channelName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(EVENT_MONITORING, monitor);
    }
}
