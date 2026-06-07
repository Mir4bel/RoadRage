package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class ScoreBarChartView extends View {

    private final List<Integer> scores = new ArrayList<>();

    private final Paint barPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint xLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final int[]   Y_MARKS     = {0, 25, 50, 75, 100};
    private static final float   AXIS_W_DP   = 36f;
    private static final float   PAD_TOP_DP  = 6f;
    private static final float   PAD_BOT_DP  = 18f;

    public ScoreBarChartView(Context c)                     { super(c);       init(); }
    public ScoreBarChartView(Context c, AttributeSet attrs) { super(c, attrs); init(); }

    private void init() {
        barPaint.setStyle(Paint.Style.FILL);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(dpToPx(0.5f));
        gridPaint.setColor(Color.argb(50, 128, 128, 128));

        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(dpToPx(1));
        axisPaint.setColor(Color.argb(80, 128, 128, 128));

        labelPaint.setTextSize(spToPx(9));
        labelPaint.setTextAlign(Paint.Align.RIGHT);
        labelPaint.setColor(Color.argb(130, 100, 100, 100));

        xLabelPaint.setTextSize(spToPx(8));
        xLabelPaint.setTextAlign(Paint.Align.CENTER);
        xLabelPaint.setColor(Color.argb(90, 100, 100, 100));
    }

    public void setScores(List<Integer> data) {
        scores.clear();
        scores.addAll(data);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();

        float axisW  = dpToPx(AXIS_W_DP);
        float padTop = dpToPx(PAD_TOP_DP);
        float padBot = dpToPx(PAD_BOT_DP);

        float gLeft   = axisW;
        float gTop    = padTop;
        float gRight  = w;
        float gBottom = h - padBot;
        float gH      = gBottom - gTop;
        float gW      = gRight - gLeft;

        for (int mark : Y_MARKS) {
            float y = gBottom - (mark / 100f) * gH;
            canvas.drawLine(gLeft, y, gRight, y, gridPaint);
            canvas.drawText(String.valueOf(mark), axisW - dpToPx(4), y + spToPx(3.5f), labelPaint);
        }

        canvas.drawLine(gLeft, gTop, gLeft, gBottom, axisPaint);

        if (scores.isEmpty()) return;

        int count = scores.size();
        float gap  = dpToPx(4);
        float barW = (gW - gap * (count + 1)) / count;
        float r    = dpToPx(4);

        for (int i = 0; i < count; i++) {
            int score = scores.get(i);
            float barH  = (score / 100f) * gH;
            float left  = gLeft + gap + i * (barW + gap);
            float top   = gBottom - barH;
            float right = left + barW;

            barPaint.setColor(scoreColor(score));
            canvas.drawRoundRect(new RectF(left, top, right, gBottom), r, r, barPaint);

            float xCenter = left + barW / 2f;
            canvas.drawText(String.valueOf(i + 1), xCenter, h - dpToPx(4), xLabelPaint);
        }
    }

    private int scoreColor(int score) {
        if (score >= 80) return Color.parseColor("#00695C");
        if (score >= 60) return Color.parseColor("#1565C0");
        if (score >= 40) return Color.parseColor("#E65100");
        return                  Color.parseColor("#B71C1C");
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    private float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}