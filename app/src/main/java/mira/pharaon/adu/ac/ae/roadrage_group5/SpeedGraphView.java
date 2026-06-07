package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class SpeedGraphView extends View {

    private final List<Float> history    = new ArrayList<>();
    private static final int   MAX_PTS   = 60;
    private static final float MAX_SPEED = 160f;

    private static final float AXIS_L_DP  = 44f;
    private static final float PAD_TOP_DP =  8f;
    private static final float PAD_BOT_DP = 22f;
    private static final float PAD_R_DP   =  6f;

    private final Paint linePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint basePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SpeedGraphView(Context c) { super(c); init(); }
    public SpeedGraphView(Context c, AttributeSet attrs) { super(c, attrs); init(); }

    private void init() {
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dpToPx(2.5f));
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint.setStyle(Paint.Style.FILL);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(dpToPx(0.5f));
        gridPaint.setColor(Color.argb(40, 128, 128, 128));
        gridPaint.setPathEffect(new DashPathEffect(new float[]{dpToPx(4), dpToPx(4)}, 0));

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dpToPx(1f));
        borderPaint.setColor(Color.argb(70, 128, 128, 128));

        labelPaint.setTextSize(spToPx(8.5f));
        labelPaint.setColor(Color.argb(120, 100, 100, 100));
        labelPaint.setTextAlign(Paint.Align.RIGHT);

        basePaint.setStyle(Paint.Style.STROKE);
        basePaint.setStrokeWidth(dpToPx(1.5f));
        basePaint.setColor(Color.argb(80, 100, 100, 100));

        dotPaint.setStyle(Paint.Style.FILL);
    }

    public void addSpeedReading(float speedKmh) {
        history.add(Math.min(speedKmh, MAX_SPEED));
        if (history.size() > MAX_PTS) history.remove(0);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();

        float axisL  = dpToPx(AXIS_L_DP);
        float padT   = dpToPx(PAD_TOP_DP);
        float padB   = dpToPx(PAD_BOT_DP);
        float padR   = dpToPx(PAD_R_DP);

        float gLeft   = axisL;
        float gTop    = padT;
        float gRight  = w - padR;
        float gBottom = h - padB;
        float gH      = gBottom - gTop;
        float gW      = gRight - gLeft;

        float[] speedMarks = {0, 40, 80, 120};
        for (float mark : speedMarks) {
            float y = gBottom - (mark / MAX_SPEED) * gH;

            if (mark == 0) {
                canvas.drawLine(gLeft, y, gRight, y, basePaint);
            } else {
                canvas.drawLine(gLeft, y, gRight, y, gridPaint);
            }

            canvas.drawText(String.valueOf((int) mark), axisL - dpToPx(5), y + spToPx(3.5f), labelPaint);
        }

        canvas.save();
        float axisLabelX = dpToPx(9);
        float axisLabelY = (gTop + gBottom) / 2f;
        canvas.rotate(-90, axisLabelX, axisLabelY);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("km/h", axisLabelX, axisLabelY + spToPx(3.5f), labelPaint);
        canvas.restore();
        labelPaint.setTextAlign(Paint.Align.RIGHT); // restore

        canvas.drawRect(new RectF(gLeft, gTop, gRight, gBottom), borderPaint);

        if (history.size() < 2) {
            float dotX = gLeft + dpToPx(12);
            dotPaint.setColor(Color.parseColor("#2196F3"));
            dotPaint.setAlpha(160);
            canvas.drawCircle(dotX, gBottom, dpToPx(4), dotPaint);
            dotPaint.setAlpha(255);
            return;
        }

        int count = history.size();
        float xStep  = gW / (MAX_PTS - 1);
        float xStart = gLeft + (MAX_PTS - count) * xStep;

        Path fill = new Path();
        float x0 = xStart, y0 = gBottom - (history.get(0) / MAX_SPEED) * gH;
        fill.moveTo(x0, gBottom);
        fill.lineTo(x0, y0);
        for (int i = 1; i < count; i++) {
            float x = xStart + i * xStep;
            float y = gBottom - (history.get(i) / MAX_SPEED) * gH;
            fill.lineTo(x, y);
        }
        fill.lineTo(xStart + (count - 1) * xStep, gBottom);
        fill.close();

        fillPaint.setColor(speedColor(history.get(count - 1)));
        fillPaint.setAlpha(40);
        canvas.drawPath(fill, fillPaint);

        for (int i = 1; i < count; i++) {
            float x1 = xStart + (i - 1) * xStep;
            float y1 = gBottom - (history.get(i - 1) / MAX_SPEED) * gH;
            float x2 = xStart + i * xStep;
            float y2 = gBottom - (history.get(i) / MAX_SPEED) * gH;
            linePaint.setColor(speedColor(history.get(i)));
            canvas.drawLine(x1, y1, x2, y2, linePaint);
        }

        float latest = history.get(count - 1);
        float dotX   = xStart + (count - 1) * xStep;
        float dotY   = gBottom - (latest / MAX_SPEED) * gH;
        dotPaint.setColor(speedColor(latest));
        canvas.drawCircle(dotX, dotY, dpToPx(5), dotPaint);
        dotPaint.setAlpha(55);
        canvas.drawCircle(dotX, dotY, dpToPx(9), dotPaint);
        dotPaint.setAlpha(255);
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