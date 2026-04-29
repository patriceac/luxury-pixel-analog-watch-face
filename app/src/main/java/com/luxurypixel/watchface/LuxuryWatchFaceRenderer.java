package com.luxurypixel.watchface;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

final class LuxuryWatchFaceRenderer {
    private static final int BLACK = Color.rgb(0, 0, 0);
    private static final int DIAL_BLACK = Color.rgb(9, 9, 8);
    private static final int DEEP_BLACK = Color.rgb(2, 2, 2);
    private static final int GOLD = Color.rgb(226, 171, 91);
    private static final int GOLD_DARK = Color.rgb(120, 78, 30);
    private static final int GOLD_LIGHT = Color.rgb(255, 224, 158);
    private static final int IVORY = Color.rgb(255, 242, 185);
    private static final int SILVER = Color.rgb(235, 236, 232);
    private static final int STEEL = Color.rgb(143, 148, 149);

    private static final Typeface THIN = Typeface.create("sans-serif-thin", Typeface.NORMAL);
    private static final Typeface LIGHT = Typeface.create("sans-serif-light", Typeface.NORMAL);
    private static final Typeface REGULAR = Typeface.create("sans-serif", Typeface.NORMAL);

    private LuxuryWatchFaceRenderer() {
    }

    static void draw(
            Canvas canvas,
            int width,
            int height,
            Calendar time,
            int batteryPercent,
            int steps,
            int heartRate,
            boolean ambient) {
        draw(canvas, width, height, time, batteryPercent, steps, heartRate, ambient, true, true);
    }

    static void drawDialOnly(Canvas canvas, int width, int height) {
        draw(canvas, width, height, Calendar.getInstance(), 78, 7645, 72, false, false, false);
    }

    static void drawWffBackground(Canvas canvas, int width, int height) {
        draw(canvas, width, height, Calendar.getInstance(), 78, 7645, 72, false, false, false, true);
    }

    private static void draw(
            Canvas canvas,
            int width,
            int height,
            Calendar time,
            int batteryPercent,
            int steps,
            int heartRate,
            boolean ambient,
            boolean includeData,
            boolean includeHands) {
        draw(canvas, width, height, time, batteryPercent, steps, heartRate, ambient, includeData, includeHands, false);
    }

    private static void draw(
            Canvas canvas,
            int width,
            int height,
            Calendar time,
            int batteryPercent,
            int steps,
            int heartRate,
            boolean ambient,
            boolean includeData,
            boolean includeHands,
            boolean includeStaticComplications) {
        float size = Math.min(width, height);
        float cx = width * 0.5f;
        float cy = height * 0.5f;
        float radius = size * 0.492f;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.ROUND);

        canvas.drawColor(BLACK);
        drawDialBase(canvas, paint, cx, cy, radius);
        drawOuterTicksAndNumbers(canvas, paint, cx, cy, radius);
        drawConcentricDial(canvas, paint, cx, cy, radius);
        drawHourMarkers(canvas, paint, cx, cy, radius);
        if (includeStaticComplications) {
            drawStaticComplications(canvas, paint, cx, cy, radius);
        }
        if (includeData) {
            drawDate(canvas, paint, cx, cy, radius, time);
            drawComplications(canvas, paint, cx, cy, radius, steps, batteryPercent, heartRate);
        }
        if (includeHands) {
            drawHands(canvas, paint, cx, cy, radius, time);
            drawCenterPin(canvas, paint, cx, cy, radius);
        }
    }

    private static void drawDialBase(Canvas canvas, Paint paint, float cx, float cy, float r) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx, cy, r,
                new int[]{Color.rgb(28, 27, 24), DIAL_BLACK, DEEP_BLACK, BLACK},
                new float[]{0f, 0.55f, 0.87f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(r * 0.012f);
        paint.setColor(Color.rgb(31, 31, 29));
        canvas.drawCircle(cx, cy, r * 0.995f, paint);
        paint.setStrokeWidth(r * 0.004f);
        paint.setColor(Color.rgb(75, 75, 70));
        canvas.drawCircle(cx, cy, r * 0.972f, paint);

        paint.setStrokeWidth(r * 0.0018f);
        for (int i = 0; i < 160; i++) {
            float angle = i * 2.25f;
            float inner = r * (0.64f + ((i % 7) * 0.008f));
            float outer = r * 0.90f;
            paint.setColor((i % 5 == 0) ? argb(36, GOLD) : argb(28, Color.WHITE));
            canvas.drawLine(pointX(cx, inner, angle), pointY(cy, inner, angle),
                    pointX(cx, outer, angle), pointY(cy, outer, angle), paint);
        }
    }

    private static void drawConcentricDial(Canvas canvas, Paint paint, float cx, float cy, float r) {
        float innerR = r * 0.64f;
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx, cy, innerR,
                new int[]{Color.rgb(34, 33, 31), Color.rgb(12, 12, 11), Color.rgb(5, 5, 5)},
                new float[]{0f, 0.62f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, innerR, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        for (float groove = r * 0.035f; groove <= innerR; groove += Math.max(1.8f, r * 0.014f)) {
            float alpha = 88f - (groove / innerR) * 35f;
            paint.setColor(argb((int) alpha, Color.rgb(95, 95, 88)));
            paint.setStrokeWidth(Math.max(0.7f, r * 0.003f));
            canvas.drawCircle(cx, cy, groove, paint);
            paint.setColor(argb(42, BLACK));
            paint.setStrokeWidth(Math.max(0.8f, r * 0.004f));
            canvas.drawCircle(cx, cy, groove + r * 0.004f, paint);
        }

        paint.setColor(argb(120, Color.rgb(62, 58, 49)));
        paint.setStrokeWidth(r * 0.0045f);
        canvas.drawCircle(cx, cy, innerR, paint);
    }

    private static void drawOuterTicksAndNumbers(Canvas canvas, Paint paint, float cx, float cy, float r) {
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < 60; i++) {
            boolean five = i % 5 == 0;
            boolean quarter = i % 15 == 0;
            float angle = i * 6f;
            float outer = r * 0.955f;
            float inner = five ? r * 0.908f : r * 0.925f;

            paint.setStrokeWidth(quarter ? r * 0.010f : five ? r * 0.008f : r * 0.0045f);
            paint.setColor(five ? GOLD_LIGHT : SILVER);
            canvas.drawLine(pointX(cx, inner, angle), pointY(cy, inner, angle),
                    pointX(cx, outer, angle), pointY(cy, outer, angle), paint);

            if (!five) {
                paint.setStrokeWidth(r * 0.0025f);
                paint.setColor(argb(i % 2 == 0 ? 180 : 110, GOLD_LIGHT));
                canvas.drawLine(pointX(cx, r * 0.878f, angle), pointY(cy, r * 0.878f, angle),
                        pointX(cx, r * 0.902f, angle), pointY(cy, r * 0.902f, angle), paint);
            }
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(LIGHT);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(r * 0.073f);
        for (int minute = 5; minute <= 60; minute += 5) {
            float angle = (minute % 60) * 6f;
            String label = minute == 60 ? "60" : String.format(Locale.US, "%02d", minute);
            float x = pointX(cx, r * 0.835f, angle);
            float y = pointY(cy, r * 0.835f, angle);
            float rotation = readableRotation(angle);
            drawShadowedText(canvas, paint, label, x, y + textCenterOffset(paint), rotation, IVORY, r * 0.006f);
        }
    }

    private static void drawHourMarkers(Canvas canvas, Paint paint, float cx, float cy, float r) {
        for (int hour = 0; hour < 12; hour++) {
            float angle = hour * 30f;
            boolean doubleMarker = hour == 0 || hour == 3 || hour == 9;
            drawBatonMarker(canvas, paint, cx, cy, r, angle, doubleMarker);
        }
    }

    private static void drawBatonMarker(
            Canvas canvas,
            Paint paint,
            float cx,
            float cy,
            float r,
            float angle,
            boolean doubleMarker) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.rotate(angle);

        if (doubleMarker) {
            float offset = r * 0.026f;
            drawBatonPiece(canvas, paint, r, -offset, r * 0.021f);
            drawBatonPiece(canvas, paint, r, offset, r * 0.021f);
        } else {
            drawBatonPiece(canvas, paint, r, 0f, r * 0.032f);
        }

        canvas.restore();
    }

    private static void drawBatonPiece(Canvas canvas, Paint paint, float r, float xOffset, float halfWidth) {
        float outer = -r * 0.775f;
        float inner = -r * 0.630f;
        float bevel = r * 0.018f;

        Path shadow = markerPath(xOffset + r * 0.006f, outer + r * 0.008f, inner + r * 0.008f, halfWidth, bevel);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(argb(160, BLACK));
        canvas.drawPath(shadow, paint);

        Path trim = markerPath(xOffset, outer, inner, halfWidth, bevel);
        paint.setShader(new LinearGradient(
                xOffset - halfWidth, outer,
                xOffset + halfWidth, inner,
                new int[]{GOLD_LIGHT, GOLD_DARK, GOLD_LIGHT},
                new float[]{0f, 0.52f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawPath(trim, paint);
        paint.setShader(null);

        Path face = markerPath(xOffset, outer + bevel * 0.55f, inner - bevel * 0.2f, halfWidth * 0.48f, bevel * 0.62f);
        paint.setShader(new LinearGradient(
                xOffset - halfWidth * 0.45f, outer,
                xOffset + halfWidth * 0.45f, inner,
                new int[]{Color.WHITE, STEEL, Color.rgb(255, 249, 219)},
                new float[]{0f, 0.55f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawPath(face, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(r * 0.0025f);
        paint.setColor(argb(170, Color.WHITE));
        canvas.drawPath(face, paint);
    }

    private static Path markerPath(float x, float outer, float inner, float halfWidth, float bevel) {
        Path path = new Path();
        path.moveTo(x - halfWidth * 0.55f, outer);
        path.lineTo(x + halfWidth * 0.55f, outer);
        path.lineTo(x + halfWidth, outer + bevel);
        path.lineTo(x + halfWidth * 0.48f, inner - bevel * 0.2f);
        path.lineTo(x, inner + bevel);
        path.lineTo(x - halfWidth * 0.48f, inner - bevel * 0.2f);
        path.lineTo(x - halfWidth, outer + bevel);
        path.close();
        return path;
    }

    private static void drawDate(Canvas canvas, Paint paint, float cx, float cy, float r, Calendar time) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.US);
        String day = dayFormat.format(time.getTime()).toUpperCase(Locale.US);
        String date = dateFormat.format(time.getTime()).toUpperCase(Locale.US);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(LIGHT);
        paint.setLetterSpacing(0.12f);
        paint.setTextSize(r * 0.085f);
        drawShadowedText(canvas, paint, day, cx, cy - r * 0.410f, 0f, GOLD_LIGHT, r * 0.006f);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(r * 0.004f);
        paint.setColor(GOLD);
        canvas.drawLine(cx - r * 0.145f, cy - r * 0.352f, cx + r * 0.145f, cy - r * 0.352f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(THIN);
        paint.setTextSize(r * 0.096f);
        drawShadowedText(canvas, paint, date, cx, cy - r * 0.270f, 0f, GOLD_LIGHT, r * 0.006f);
        paint.setLetterSpacing(0f);
    }

    private static void drawComplications(
            Canvas canvas,
            Paint paint,
            float cx,
            float cy,
            float r,
            int steps,
            int battery,
            int heartRate) {
        float bigR = r * 0.198f;
        float leftX = cx - r * 0.455f;
        float rightX = cx + r * 0.455f;
        float bigY = cy + r * 0.020f;
        drawLargeComplication(canvas, paint, leftX, bigY, bigR, "steps", String.valueOf(steps), "STEPS", 0.78f);
        drawLargeComplication(canvas, paint, rightX, bigY, bigR, "battery", battery + "%", "BATTERY", battery / 100f);
        drawHeartComplication(canvas, paint, cx, cy + r * 0.405f, r * 0.128f, heartRate);
    }

    private static void drawStaticComplications(Canvas canvas, Paint paint, float cx, float cy, float r) {
        float bigR = r * 0.198f;
        float bigY = cy + r * 0.020f;
        drawStaticLargeComplication(canvas, paint, cx - r * 0.455f, bigY, bigR, "steps", "STEPS", 0.78f);
        drawStaticLargeComplication(canvas, paint, cx + r * 0.455f, bigY, bigR, "battery", "BATTERY", 0.78f);
        drawStaticHeartComplication(canvas, paint, cx, cy + r * 0.405f, r * 0.128f);
    }

    private static void drawStaticLargeComplication(
            Canvas canvas,
            Paint paint,
            float cx,
            float cy,
            float r,
            String icon,
            String label,
            float progress) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx - r * 0.2f, cy - r * 0.25f, r * 1.15f,
                new int[]{Color.rgb(32, 31, 29), Color.rgb(6, 6, 5), BLACK},
                new float[]{0f, 0.62f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(r * 0.055f);
        paint.setColor(argb(75, GOLD_LIGHT));
        RectF arcBounds = new RectF(cx - r * 0.84f, cy - r * 0.84f, cx + r * 0.84f, cy + r * 0.84f);
        canvas.drawArc(arcBounds, 132f, 276f, false, paint);
        paint.setColor(GOLD_LIGHT);
        canvas.drawArc(arcBounds, 132f, Math.max(24f, 276f * Math.min(1f, progress)), false, paint);
        paint.setStrokeCap(Paint.Cap.BUTT);

        paint.setStrokeWidth(r * 0.021f);
        paint.setColor(argb(210, GOLD_DARK));
        canvas.drawCircle(cx, cy, r * 0.965f, paint);
        paint.setStrokeWidth(r * 0.012f);
        paint.setColor(GOLD_LIGHT);
        canvas.drawCircle(cx, cy, r * 0.985f, paint);
        paint.setStrokeWidth(r * 0.006f);
        paint.setColor(argb(180, SILVER));
        canvas.drawCircle(cx, cy, r * 0.990f, paint);

        paint.setStyle(Paint.Style.FILL);
        if ("battery".equals(icon)) {
            drawLightning(canvas, paint, cx, cy - r * 0.48f, r * 0.40f);
        } else {
            drawShoe(canvas, paint, cx, cy - r * 0.48f, r * 0.42f);
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(REGULAR);
        paint.setTextSize(r * 0.220f);
        drawShadowedText(canvas, paint, label, cx, cy + r * 0.560f, 0f, IVORY, r * 0.012f);
    }

    private static void drawStaticHeartComplication(Canvas canvas, Paint paint, float cx, float cy, float r) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx - r * 0.22f, cy - r * 0.26f, r * 1.1f,
                new int[]{Color.rgb(34, 33, 31), Color.rgb(8, 8, 7), BLACK},
                new float[]{0f, 0.66f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(r * 0.055f);
        paint.setColor(GOLD_LIGHT);
        canvas.drawCircle(cx, cy, r * 0.985f, paint);
        paint.setStrokeWidth(r * 0.018f);
        paint.setColor(argb(150, SILVER));
        canvas.drawCircle(cx, cy, r * 0.935f, paint);

        drawHeart(canvas, paint, cx, cy - r * 0.55f, r * 0.42f);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(REGULAR);
        paint.setTextSize(r * 0.315f);
        drawShadowedText(canvas, paint, "BPM", cx, cy + r * 0.600f, 0f, IVORY, r * 0.010f);
    }

    private static void drawLargeComplication(
            Canvas canvas,
            Paint paint,
            float cx,
            float cy,
            float r,
            String icon,
            String value,
            String label,
            float progress) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx - r * 0.2f, cy - r * 0.25f, r * 1.15f,
                new int[]{Color.rgb(32, 31, 29), Color.rgb(6, 6, 5), BLACK},
                new float[]{0f, 0.62f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(r * 0.055f);
        paint.setColor(argb(75, GOLD_LIGHT));
        RectF arcBounds = new RectF(cx - r * 0.84f, cy - r * 0.84f, cx + r * 0.84f, cy + r * 0.84f);
        canvas.drawArc(arcBounds, 132f, 276f, false, paint);
        paint.setColor(GOLD_LIGHT);
        canvas.drawArc(arcBounds, 132f, Math.max(24f, 276f * Math.min(1f, progress)), false, paint);
        paint.setStrokeCap(Paint.Cap.BUTT);

        paint.setStrokeWidth(r * 0.021f);
        paint.setColor(argb(210, GOLD_DARK));
        canvas.drawCircle(cx, cy, r * 0.965f, paint);
        paint.setStrokeWidth(r * 0.012f);
        paint.setColor(GOLD_LIGHT);
        canvas.drawCircle(cx, cy, r * 0.985f, paint);
        paint.setStrokeWidth(r * 0.006f);
        paint.setColor(argb(180, SILVER));
        canvas.drawCircle(cx, cy, r * 0.990f, paint);

        paint.setStyle(Paint.Style.FILL);
        if ("battery".equals(icon)) {
            drawLightning(canvas, paint, cx, cy - r * 0.48f, r * 0.40f);
        } else {
            drawShoe(canvas, paint, cx, cy - r * 0.48f, r * 0.42f);
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(LIGHT);
        paint.setTextSize(r * 0.430f);
        drawShadowedText(canvas, paint, value, cx, cy + r * 0.160f, 0f, Color.WHITE, r * 0.018f);

        paint.setTypeface(REGULAR);
        paint.setTextSize(r * 0.220f);
        drawShadowedText(canvas, paint, label, cx, cy + r * 0.560f, 0f, IVORY, r * 0.012f);
    }

    private static void drawHeartComplication(Canvas canvas, Paint paint, float cx, float cy, float r, int heartRate) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx - r * 0.22f, cy - r * 0.26f, r * 1.1f,
                new int[]{Color.rgb(34, 33, 31), Color.rgb(8, 8, 7), BLACK},
                new float[]{0f, 0.66f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(r * 0.055f);
        paint.setColor(GOLD_LIGHT);
        canvas.drawCircle(cx, cy, r * 0.985f, paint);
        paint.setStrokeWidth(r * 0.018f);
        paint.setColor(argb(150, SILVER));
        canvas.drawCircle(cx, cy, r * 0.935f, paint);

        drawHeart(canvas, paint, cx, cy - r * 0.55f, r * 0.42f);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(LIGHT);
        paint.setTextSize(r * 0.690f);
        drawShadowedText(canvas, paint, String.valueOf(heartRate), cx, cy + r * 0.180f, 0f, Color.WHITE, r * 0.018f);

        paint.setTypeface(REGULAR);
        paint.setTextSize(r * 0.315f);
        drawShadowedText(canvas, paint, "BPM", cx, cy + r * 0.600f, 0f, IVORY, r * 0.010f);
    }

    private static void drawHands(Canvas canvas, Paint paint, float cx, float cy, float r, Calendar time) {
        float millis = time.get(Calendar.MILLISECOND) / 1000f;
        float seconds = time.get(Calendar.SECOND) + millis;
        float minutes = time.get(Calendar.MINUTE) + seconds / 60f;
        float hours = (time.get(Calendar.HOUR) % 12) + minutes / 60f;

        drawNeedle(canvas, paint, cx, cy, r, hours * 30f, r * 0.56f, r * 0.052f, r * 0.095f);
        drawNeedle(canvas, paint, cx, cy, r, minutes * 6f, r * 0.755f, r * 0.042f, r * 0.080f);
        drawSecondHand(canvas, paint, cx, cy, r, seconds * 6f);
    }

    private static void drawNeedle(
            Canvas canvas,
            Paint paint,
            float cx,
            float cy,
            float r,
            float angle,
            float length,
            float halfWidth,
            float tail) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.rotate(angle);

        Path shadow = handPath(length, halfWidth * 1.08f, tail);
        shadow.offset(r * 0.012f, r * 0.012f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(argb(155, BLACK));
        canvas.drawPath(shadow, paint);

        Path gold = handPath(length, halfWidth, tail);
        paint.setShader(new LinearGradient(
                -halfWidth, -length,
                halfWidth, tail,
                new int[]{GOLD_LIGHT, GOLD_DARK, GOLD_LIGHT},
                new float[]{0f, 0.52f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawPath(gold, paint);
        paint.setShader(null);

        Path steel = handPath(length * 0.925f, halfWidth * 0.48f, tail * 0.45f);
        paint.setShader(new LinearGradient(
                -halfWidth * 0.46f, -length,
                halfWidth * 0.46f, tail,
                new int[]{Color.WHITE, STEEL, Color.rgb(255, 251, 230)},
                new float[]{0f, 0.52f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawPath(steel, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(r * 0.005f);
        paint.setColor(argb(220, Color.WHITE));
        canvas.drawLine(0f, -length * 0.82f, 0f, -length * 0.20f, paint);

        canvas.restore();
    }

    private static Path handPath(float length, float halfWidth, float tail) {
        Path path = new Path();
        path.moveTo(0f, -length);
        path.lineTo(halfWidth * 0.72f, -length * 0.83f);
        path.lineTo(halfWidth, tail * 0.20f);
        path.lineTo(halfWidth * 0.33f, tail);
        path.lineTo(-halfWidth * 0.33f, tail);
        path.lineTo(-halfWidth, tail * 0.20f);
        path.lineTo(-halfWidth * 0.72f, -length * 0.83f);
        path.close();
        return path;
    }

    private static void drawSecondHand(Canvas canvas, Paint paint, float cx, float cy, float r, float angle) {
        canvas.save();
        canvas.translate(cx, cy);
        canvas.rotate(angle);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(r * 0.010f);
        paint.setColor(argb(155, BLACK));
        canvas.drawLine(r * 0.012f, r * 0.110f, r * 0.012f, -r * 0.720f, paint);
        paint.setStrokeWidth(r * 0.0065f);
        paint.setColor(GOLD_LIGHT);
        canvas.drawLine(0f, r * 0.110f, 0f, -r * 0.720f, paint);
        paint.setStrokeWidth(r * 0.0025f);
        paint.setColor(Color.WHITE);
        canvas.drawLine(-r * 0.003f, r * 0.080f, -r * 0.003f, -r * 0.700f, paint);
        paint.setStrokeCap(Paint.Cap.BUTT);
        canvas.restore();
    }

    private static void drawCenterPin(Canvas canvas, Paint paint, float cx, float cy, float r) {
        paint.setStyle(Paint.Style.FILL);
        paint.setMaskFilter(new BlurMaskFilter(r * 0.02f, BlurMaskFilter.Blur.NORMAL));
        paint.setColor(argb(150, BLACK));
        canvas.drawCircle(cx + r * 0.012f, cy + r * 0.012f, r * 0.075f, paint);
        paint.setMaskFilter(null);

        paint.setShader(new RadialGradient(
                cx - r * 0.02f, cy - r * 0.02f, r * 0.092f,
                new int[]{GOLD_LIGHT, GOLD, GOLD_DARK, BLACK},
                new float[]{0f, 0.45f, 0.78f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r * 0.075f, paint);
        paint.setShader(null);

        paint.setColor(SILVER);
        canvas.drawCircle(cx, cy, r * 0.040f, paint);
        paint.setColor(GOLD_DARK);
        canvas.drawCircle(cx, cy, r * 0.026f, paint);
        paint.setColor(DEEP_BLACK);
        canvas.drawCircle(cx, cy, r * 0.012f, paint);
    }

    private static void drawLightning(Canvas canvas, Paint paint, float cx, float cy, float size) {
        Path bolt = new Path();
        bolt.moveTo(cx + size * 0.10f, cy - size * 0.55f);
        bolt.lineTo(cx - size * 0.24f, cy + size * 0.03f);
        bolt.lineTo(cx + size * 0.02f, cy + size * 0.03f);
        bolt.lineTo(cx - size * 0.10f, cy + size * 0.55f);
        bolt.lineTo(cx + size * 0.30f, cy - size * 0.10f);
        bolt.lineTo(cx + size * 0.05f, cy - size * 0.10f);
        bolt.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(GOLD_LIGHT);
        canvas.drawPath(bolt, paint);
    }

    private static void drawShoe(Canvas canvas, Paint paint, float cx, float cy, float size) {
        Path shoe = new Path();
        shoe.moveTo(cx - size * 0.45f, cy + size * 0.10f);
        shoe.cubicTo(cx - size * 0.22f, cy + size * 0.42f, cx + size * 0.16f, cy + size * 0.48f, cx + size * 0.42f, cy + size * 0.20f);
        shoe.cubicTo(cx + size * 0.30f, cy + size * 0.08f, cx + size * 0.08f, cy + size * 0.03f, cx - size * 0.05f, cy - size * 0.25f);
        shoe.cubicTo(cx - size * 0.18f, cy - size * 0.15f, cx - size * 0.32f, cy - size * 0.02f, cx - size * 0.45f, cy + size * 0.10f);
        shoe.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(GOLD_LIGHT);
        canvas.drawPath(shoe, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size * 0.035f);
        paint.setColor(GOLD_DARK);
        canvas.drawLine(cx - size * 0.10f, cy - size * 0.10f, cx + size * 0.02f, cy + size * 0.02f, paint);
        canvas.drawLine(cx - size * 0.22f, cy + size * 0.02f, cx - size * 0.08f, cy + size * 0.14f, paint);
    }

    private static void drawHeart(Canvas canvas, Paint paint, float cx, float cy, float size) {
        Path heart = new Path();
        heart.moveTo(cx, cy + size * 0.42f);
        heart.cubicTo(cx - size * 0.60f, cy - size * 0.04f, cx - size * 0.50f, cy - size * 0.54f, cx - size * 0.14f, cy - size * 0.46f);
        heart.cubicTo(cx, cy - size * 0.42f, cx, cy - size * 0.25f, cx, cy - size * 0.22f);
        heart.cubicTo(cx, cy - size * 0.25f, cx, cy - size * 0.42f, cx + size * 0.14f, cy - size * 0.46f);
        heart.cubicTo(cx + size * 0.50f, cy - size * 0.54f, cx + size * 0.60f, cy - size * 0.04f, cx, cy + size * 0.42f);
        heart.close();

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(
                cx - size * 0.42f, cy - size * 0.46f,
                cx + size * 0.38f, cy + size * 0.42f,
                new int[]{GOLD_LIGHT, GOLD, GOLD_DARK},
                null,
                Shader.TileMode.CLAMP));
        canvas.drawPath(heart, paint);
        paint.setShader(null);
    }

    private static void drawShadowedText(
            Canvas canvas,
            Paint paint,
            String text,
            float x,
            float y,
            float rotation,
            int color,
            float shadowRadius) {
        canvas.save();
        canvas.rotate(rotation, x, y);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(BLACK);
        canvas.drawText(text, x + shadowRadius, y + shadowRadius, paint);
        paint.setColor(color);
        canvas.drawText(text, x, y, paint);
        canvas.restore();
    }

    private static float readableRotation(float angle) {
        float rotation = angle;
        if (rotation > 90f && rotation < 270f) {
            rotation -= 180f;
        }
        if (rotation > 180f) {
            rotation -= 360f;
        }
        return rotation;
    }

    private static float textCenterOffset(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return -(metrics.ascent + metrics.descent) * 0.5f;
    }

    private static float pointX(float cx, float distance, float clockDegrees) {
        return cx + (float) Math.sin(Math.toRadians(clockDegrees)) * distance;
    }

    private static float pointY(float cy, float distance, float clockDegrees) {
        return cy - (float) Math.cos(Math.toRadians(clockDegrees)) * distance;
    }

    private static int argb(int alpha, int color) {
        return Color.argb(
                Math.max(0, Math.min(255, alpha)),
                Color.red(color),
                Color.green(color),
                Color.blue(color));
    }
}
