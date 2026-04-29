package com.luxurypixel.watchface;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Calendar;

public class LuxuryWatchFaceService extends WallpaperService {
    private static final int SAMPLE_STEPS = 7645;
    private static final int SAMPLE_HEART_RATE = 72;

    @Override
    public Engine onCreateEngine() {
        return new LuxuryEngine();
    }

    private final class LuxuryEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Runnable drawRunnable = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        private boolean visible;

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                drawFrame();
            } else {
                handler.removeCallbacks(drawRunnable);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacks(drawRunnable);
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onDestroy() {
            handler.removeCallbacks(drawRunnable);
            super.onDestroy();
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    LuxuryWatchFaceRenderer.draw(
                            canvas,
                            canvas.getWidth(),
                            canvas.getHeight(),
                            Calendar.getInstance(),
                            BatteryReader.readPercent(LuxuryWatchFaceService.this),
                            SAMPLE_STEPS,
                            SAMPLE_HEART_RATE,
                            false);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            handler.removeCallbacks(drawRunnable);
            if (visible) {
                long now = System.currentTimeMillis();
                long delay = 1000L - (now % 1000L) + 16L;
                handler.postDelayed(drawRunnable, delay);
            }
        }
    }
}
