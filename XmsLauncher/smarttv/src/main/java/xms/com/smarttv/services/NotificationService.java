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


import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import xms.com.smarttv.R;
import xms.com.smarttv.app.SmartTv;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Notification background service
 * Connects to SocketIO port
 * listens to channel Notification
 * subscribes to presence-Online channel
 */
public class NotificationService extends IntentService {

    private String TAG = "TEST";
    private Socket socket = SmartTv.getInstance().getSocket();
    private String EVENT_SUBSCRIBE = "Subscribe";
    private String EVENT_BROADCASTNOTIFICATION = "Notification ";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        EVENT_BROADCASTNOTIFICATION = EVENT_BROADCASTNOTIFICATION + Realm.getDefaultInstance().where(User.class).findFirst().getRoom();
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit(EVENT_SUBSCRIBE, Realm.getDefaultInstance().where(User.class).findFirst().getRoom());
            }
        })
        .on(EVENT_BROADCASTNOTIFICATION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                Notification notification = new Notification();
                try {
                    JSONObject message = obj.getJSONObject("message");
                    notification.setType(message.getInt("type"));
                    notification.setMessage(message.getString("message"));
                    notification.setImage(message.getString("image"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final Notification finalNotification = notification;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        checkDrawOverlayPermission(finalNotification);
                    }
                });

            }
        })
        .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "ECHO DISCONNECTED");
            }
        });

        socket.connect();
    }

    public void checkDrawOverlayPermission(Notification notification) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                showNotification(notification);
            }
        } else {
            showNotification(notification);
        }
    }

    /**
     * User window manager to show notification message and image
     * @param notification
     */
    private void showNotification (Notification notification) {
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final RelativeLayout notificationView = (RelativeLayout) inflater.inflate(R.layout.notification, null);

        TextView notificationText = notificationView.findViewById(R.id.notification_message);
        ImageView notificationImage = notificationView.findViewById(R.id.notification_background);
        Button button = notificationView.findViewById(R.id.close_notification);
//        SurfaceView surfaceView = notificationView.findViewById(R.id.camera_view);
//        RTPPlayer RTPPlayer = new RTPPlayer(notificationView.getContext(), surfaceView,null, 600, 350);
//        RTPPlayer.createPlayer();
//        RTPPlayer.SetSource("rtsp://192.168.10.102:554/user=admin&password=&channel=1&stream=0.sdp");

        int layouttype;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layouttype = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layouttype = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams  params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layouttype,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.END | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = 800;
        params.height = 250;

        notificationText.setText(notification.getMessage());

        Glide.with(context)
                .load(notification.getImage())
                .into(notificationImage);

        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        windowManager.addView(notificationView, params);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(notificationView);
            }
        });

    }

}
