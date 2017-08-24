package xms.com.smarttv.weather;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import xms.com.smarttv.R;

public class OpenUrlActivity extends Activity {
    public static  final String LON = "55.207660";
    public static  final String LAT = "25.082351";
    public static final String WEATHER_WEB_URL = String.format("http://forecast.io/embed/#lat=%s&lon=%s&name=Wellington color=#00aaff&font=Arial&units=ca", LAT, LON);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_preview);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(WEATHER_WEB_URL);

    }
}
