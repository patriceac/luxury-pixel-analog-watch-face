package com.luxurypixel.watchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

import com.cyberpat.luxurypixel.watchface.preview.R;

import java.util.Calendar;
import java.util.Locale;

final class WffPreviewView extends View {
    private static final float WFF_SIZE = 450f;
    private static final int SAMPLE_STEPS = 25000;
    private static final int SAMPLE_BATTERY = 78;
    private static final int SAMPLE_HEART_RATE = 120;

    private final Bitmap dialBackground;
    private final Bitmap hourHand;
    private final Bitmap minuteHand;
    private final Bitmap secondHand;
    private final Bitmap centerPin;
    private final Bitmap stepsIcon;
    private final Bitmap batteryIcon;
    private final Bitmap heartIcon;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private final Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
    private final Typeface thin = Typeface.create("sans-serif-thin", Typeface.NORMAL);

    WffPreviewView(Context context) {
        super(context);
        setKeepScreenOn(true);
        dialBackground = bitmap(R.drawable.dial_background);
        hourHand = bitmap(R.drawable.hour_hand);
        minuteHand = bitmap(R.drawable.minute_hand);
        secondHand = bitmap(R.drawable.second_hand);
        centerPin = bitmap(R.drawable.center_pin);
        stepsIcon = bitmap(R.drawable.steps_icon);
        batteryIcon = bitmap(R.drawable.battery_icon);
        heartIcon = bitmap(R.drawable.heart_icon);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale = Math.min(getWidth(), getHeight()) / WFF_SIZE;
        float offsetX = (getWidth() - WFF_SIZE * scale) * 0.5f;
        float offsetY = (getHeight() - WFF_SIZE * scale) * 0.5f;

        canvas.drawColor(Color.BLACK);
        drawBitmap(canvas, dialBackground, offsetX, offsetY, scale, 0, 0, 450, 450);
        drawHands(canvas, offsetX, offsetY, scale);
        drawDate(canvas, offsetX, offsetY, scale);
        drawComplications(canvas, offsetX, offsetY, scale);
        drawBitmap(canvas, centerPin, offsetX, offsetY, scale, 203, 203, 44, 44);
    }

    private Bitmap bitmap(int resourceId) {
        return BitmapFactory.decodeResource(getResources(), resourceId);
    }

    private void drawDate(Canvas canvas, float ox, float oy, float scale) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(light);
        paint.setColor(Color.rgb(255, 224, 158));
        paint.setTextSize(26f * scale);
        paint.setLetterSpacing(0.08f);
        drawCenteredText(canvas, "SUN", ox, oy, scale, 160, 95, 132, 34);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.6f * scale);
        paint.setColor(Color.rgb(226, 171, 91));
        canvas.drawLine(ox + 165f * scale, oy + 132f * scale, ox + 285f * scale, oy + 132f * scale, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(thin);
        paint.setTextSize(29f * scale);
        paint.setColor(Color.rgb(255, 224, 158));
        drawCenteredText(canvas, "APR 26", ox, oy, scale, 137, 130, 176, 41);
        paint.setLetterSpacing(0f);
    }

    private void drawComplications(Canvas canvas, float ox, float oy, float scale) {
        drawBitmap(canvas, stepsIcon, ox, oy, scale, 112, 173, 31, 25);
        drawBitmap(canvas, batteryIcon, ox, oy, scale, 314, 172, 20, 29);
        drawBitmap(canvas, heartIcon, ox, oy, scale, 213, 280, 24, 21);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(light);
        paint.setColor(Color.WHITE);

        paint.setTextSize(38f * scale);
        drawCenteredText(canvas, String.format(Locale.US, "%d", SAMPLE_STEPS), ox, oy, scale, 66, 196, 136, 43);

        paint.setColor(Color.rgb(255, 242, 157));
        paint.setTextSize(23f * scale);
        drawCenteredText(canvas, "STEPS", ox, oy, scale, 68, 233, 120, 31);

        paint.setColor(Color.WHITE);
        paint.setTextSize(38f * scale);
        drawCenteredText(canvas, String.format(Locale.US, "%d%%", SAMPLE_BATTERY), ox, oy, scale, 263, 196, 124, 43);

        paint.setColor(Color.rgb(255, 242, 157));
        paint.setTextSize(21f * scale);
        drawCenteredText(canvas, "BATTERY", ox, oy, scale, 262, 233, 126, 31);

        paint.setColor(Color.WHITE);
        paint.setTextSize(37f * scale);
        drawCenteredText(canvas, String.format(Locale.US, "%d", SAMPLE_HEART_RATE), ox, oy, scale, 180, 302, 90, 40);

        paint.setColor(Color.rgb(255, 242, 157));
        paint.setTextSize(21f * scale);
        drawCenteredText(canvas, "BPM", ox, oy, scale, 190, 336, 70, 28);
    }

    private void drawHands(Canvas canvas, float ox, float oy, float scale) {
        Calendar sample = Calendar.getInstance();
        sample.set(Calendar.HOUR_OF_DAY, 10);
        sample.set(Calendar.MINUTE, 10);
        sample.set(Calendar.SECOND, 32);
        sample.set(Calendar.MILLISECOND, 0);

        float seconds = sample.get(Calendar.SECOND);
        float minutes = sample.get(Calendar.MINUTE) + seconds / 60f;
        float hours = (sample.get(Calendar.HOUR) % 12) + minutes / 60f;

        drawRotatedBitmap(canvas, hourHand, ox, oy, scale, 207, 101, 36, 140, 0.5f, 0.884f, hours * 30f);
        drawRotatedBitmap(canvas, minuteHand, ox, oy, scale, 209, 68, 33, 176, 0.5f, 0.890f, minutes * 6f);
        drawRotatedBitmap(canvas, secondHand, ox, oy, scale, 221, 53, 8, 206, 0.5f, 0.836f, seconds * 6f);
    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap, float ox, float oy, float scale, float x, float y, float width, float height) {
        RectF dest = rect(ox, oy, scale, x, y, width, height);
        canvas.drawBitmap(bitmap, null, dest, paint);
    }

    private void drawRotatedBitmap(
            Canvas canvas,
            Bitmap bitmap,
            float ox,
            float oy,
            float scale,
            float x,
            float y,
            float width,
            float height,
            float pivotX,
            float pivotY,
            float angle) {
        float px = ox + (x + width * pivotX) * scale;
        float py = oy + (y + height * pivotY) * scale;
        canvas.save();
        canvas.rotate(angle, px, py);
        drawBitmap(canvas, bitmap, ox, oy, scale, x, y, width, height);
        canvas.restore();
    }

    private RectF rect(float ox, float oy, float scale, float x, float y, float width, float height) {
        return new RectF(
                ox + x * scale,
                oy + y * scale,
                ox + (x + width) * scale,
                oy + (y + height) * scale);
    }

    private void drawCenteredText(Canvas canvas, String text, float ox, float oy, float scale, float x, float y, float width, float height) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        int textColor = paint.getColor();
        float centerX = ox + (x + width * 0.5f) * scale;
        float centerY = oy + (y + height * 0.5f) * scale;
        float baseline = centerY - (metrics.ascent + metrics.descent) * 0.5f;
        paint.setColor(Color.BLACK);
        canvas.drawText(text, centerX + scale, baseline + scale, paint);
        paint.setColor(textColor);
        canvas.drawText(text, centerX, baseline, paint);
    }
}
