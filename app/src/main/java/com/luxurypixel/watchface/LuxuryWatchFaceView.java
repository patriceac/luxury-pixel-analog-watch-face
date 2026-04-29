package com.luxurypixel.watchface;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.Calendar;

public class LuxuryWatchFaceView extends View {
    private static final int SAMPLE_STEPS = 7645;
    private static final int SAMPLE_HEART_RATE = 72;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            invalidate();
            scheduleNextTick();
        }
    };

    private boolean running;
    private final boolean dialOnly;
    private final boolean backgroundOnly;

    public LuxuryWatchFaceView(Context context) {
        this(context, false, false);
    }

    public LuxuryWatchFaceView(Context context, boolean dialOnly) {
        this(context, dialOnly, false);
    }

    public LuxuryWatchFaceView(Context context, boolean dialOnly, boolean backgroundOnly) {
        super(context);
        this.dialOnly = dialOnly;
        this.backgroundOnly = backgroundOnly;
        setKeepScreenOn(true);
        setFocusable(false);
    }

    public void start() {
        if (!running) {
            running = true;
            invalidate();
            scheduleNextTick();
        }
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(ticker);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundOnly) {
            LuxuryWatchFaceRenderer.drawWffBackground(canvas, getWidth(), getHeight());
        } else if (dialOnly) {
            LuxuryWatchFaceRenderer.drawDialOnly(canvas, getWidth(), getHeight());
        } else {
            LuxuryWatchFaceRenderer.draw(
                    canvas,
                    getWidth(),
                    getHeight(),
                    Calendar.getInstance(),
                    BatteryReader.readPercent(getContext()),
                    SAMPLE_STEPS,
                    SAMPLE_HEART_RATE,
                    false);
        }
    }

    private void scheduleNextTick() {
        if (!running) {
            return;
        }
        long now = System.currentTimeMillis();
        long delay = 1000L - (now % 1000L) + 16L;
        handler.postDelayed(ticker, delay);
    }
}
