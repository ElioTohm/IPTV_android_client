package xms.com.smarttv.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eliotohme.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import xms.com.smarttv.R;
import xms.com.smarttv.app.SmartTv;


public class MonitorService extends IntentService {

    private Socket socket = SmartTv.getInstance().getSocket();

    private String TAG = "MONITORING";

    private String channelName = "";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MonitorService(String name) {
        super(name);
        channelName = name;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG, "ECHO START...");
        channelMonitoring(this.channelName);
    }

    public void channelMonitoring (String channel) {
        JSONObject object = new JSONObject();
        JSONObject auth = new JSONObject();
        JSONObject headers = new JSONObject();

        try {
            object.put("channel", "private-Notification_To_");
            object.put("name", "subscribe");
            headers.put("Authorization", "Bearer " + Realm.getDefaultInstance().where(User.class).findFirst().getAccess_token());
            auth.put("headers", headers);
            object.put("auth", auth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("App\\Events\\NotificationEvent", object);
    }
}
