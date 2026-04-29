package com.luxurypixel.watchface;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PreviewActivity extends Activity {
    private LuxuryWatchFaceView watchFaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        if (getIntent().getBooleanExtra("legacy_renderer", false)) {
            boolean dialOnly = getIntent().getBooleanExtra("dial_only", false);
            boolean backgroundOnly = getIntent().getBooleanExtra("background_only", false);
            watchFaceView = new LuxuryWatchFaceView(this, dialOnly, backgroundOnly);
            setContentView(watchFaceView);
        } else {
            watchFaceView = null;
            setContentView(new WffPreviewView(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (watchFaceView != null) {
            watchFaceView.start();
        }
    }

    @Override
    protected void onPause() {
        if (watchFaceView != null) {
            watchFaceView.stop();
        }
        super.onPause();
    }
}
