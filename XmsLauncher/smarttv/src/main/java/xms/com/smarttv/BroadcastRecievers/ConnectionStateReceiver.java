package xms.com.smarttv.BroadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionStateReceiver extends BroadcastReceiver {
    private ConnectionStateInterface connectionStateInterface;
    public ConnectionStateReceiver (ConnectionStateInterface connectionStateInterface) {
        this.connectionStateInterface = connectionStateInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                connectionStateInterface.result(true);
                Log.d("Network", "Internet YAY");
            } else {
                connectionStateInterface.result(false);
                Log.d("Network", "No internet :(");
            }
        }
    }

    public interface ConnectionStateInterface {
        void result (Boolean connected);
    }
}
