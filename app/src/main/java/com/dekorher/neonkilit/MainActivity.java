package com.dekorher.neonkilit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView web;
    private int pil = -1;

    private final BroadcastReceiver pilAlici = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) {
            int seviye = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int olcek = i.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            if (seviye >= 0 && olcek > 0) { pil = Math.round(seviye * 100f / olcek); pilYaz(); }
        }
    };

    private void pilYaz() {
        if (web == null || pil < 0) return;
        web.evaluateJavascript("(function(){var e=document.getElementById('batteryPercent');if(e)e.textContent='" + pil + "%';})()", null);
    }

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        web = new WebView(this);
        web.setBackgroundColor(Color.BLACK);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView v, String url) { pilYaz(); }
        });
        web.loadUrl("file:///android_asset/index.html");
        setContentView(web);
    }

    @Override protected void onResume() {
        super.onResume();
        registerReceiver(pilAlici, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        tamEkran();
    }

    @Override protected void onPause() {
        super.onPause();
        try { unregisterReceiver(pilAlici); } catch (Exception ignored) {}
    }

    @Override public void onWindowFocusChanged(boolean f) { super.onWindowFocusChanged(f); if (f) tamEkran(); }

    private void tamEkran() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }
}
