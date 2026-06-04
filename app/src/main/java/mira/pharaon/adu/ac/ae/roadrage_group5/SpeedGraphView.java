package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class SpeedGraphView extends View {

    private final List<Float> history = new ArrayList<>();
    private static final int   MAX_POINTS   = 60;
    private static final float MAX_SPEED_KH = 160f;

    private final Paint linePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SpeedGraphView(Context context)                     { super(context);       init(); }
    public SpeedGraphView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dpToPx(2.5f));
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint.setStyle(Paint.Style.FILL);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(dpToPx(0.5f));
        gridPaint.setColor(Color.argb(255, 0, 0, 0));
        gridPaint.setPathEffect(new DashPathEffect(new float[]{dpToPx(4), dpToPx(4)}, 0));

        labelPaint.setTextSize(spToPx(13));
        labelPaint.setColor(Color.argb(255, 0, 0, 0));

        dotPaint.setStyle(Paint.Style.FILL);
    }

    /** Called by TripActivity on every GPS update */
    public void addSpeedReading(float speedKmh) {
        history.add(Math.min(speedKmh, MAX_SPEED_KH));
        if (history.size() > MAX_POINTS) history.remove(0);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        drawGrid(canvas, w, h);
        if (history.size() < 2) return;

        // X: right-anchored — latest reading is at x = width
        // Each point occupies w/(MAX_POINTS-1) pixels
        float xStep = (float) w / (MAX_POINTS - 1);
        int   count = history.size();
        float xStart = (MAX_POINTS - count) * xStep; // where the first visible point starts

        // Build fill path
        Path fill = new Path();
        fill.moveTo(xStart, h);
        for (int i = 0; i < count; i++) {
            float x = xStart + i * xStep;
            float y = h - (history.get(i) / MAX_SPEED_KH) * h;
            fill.lineTo(x, y);
        }
        fill.lineTo(xStart + (count - 1) * xStep, h);
        fill.close();

        float latest = history.get(count - 1);
        fillPaint.setColor(speedColor(latest));
        fillPaint.setAlpha(45);
        canvas.drawPath(fill, fillPaint);

        // Draw line segments, each colored by its speed value
        for (int i = 1; i < count; i++) {
            float x1 = xStart + (i - 1) * xStep;
            float y1 = h - (history.get(i - 1) / MAX_SPEED_KH) * h;
            float x2 = xStart + i * xStep;
            float y2 = h - (history.get(i) / MAX_SPEED_KH) * h;
            linePaint.setColor(speedColor(history.get(i)));
            canvas.drawLine(x1, y1, x2, y2, linePaint);
        }

        // Live dot at the current position
        float dotX = xStart + (count - 1) * xStep;
        float dotY = h - (latest / MAX_SPEED_KH) * h;
        dotPaint.setColor(speedColor(latest));
        canvas.drawCircle(dotX, dotY, dpToPx(5), dotPaint);
        dotPaint.setAlpha(55);
        canvas.drawCircle(dotX, dotY, dpToPx(9), dotPaint);
        dotPaint.setAlpha(255);
    }

    private void drawGrid(Canvas canvas, int w, int h) {
        float[] marks = {40, 80, 120};
        for (float s : marks) {
            float y = h - (s / MAX_SPEED_KH) * h;
            canvas.drawLine(0, y, w, y, gridPaint);
            canvas.drawText((int) s + " km/h", dpToPx(6), y - dpToPx(2), labelPaint);
        }
    }

    private int speedColor(float kmh) {
        if (kmh > 100) return Color.parseColor("#F44336");
        if (kmh > 60)  return Color.parseColor("#FF9800");
        if (kmh > 30)  return Color.parseColor("#4CAF50");
        return               Color.parseColor("#2196F3");
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    private float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}