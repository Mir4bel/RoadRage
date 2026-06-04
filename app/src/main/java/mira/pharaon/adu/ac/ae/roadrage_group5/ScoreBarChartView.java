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
    private final Paint barPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ScoreBarChartView(Context c)                     { super(c);       init(); }
    public ScoreBarChartView(Context c, AttributeSet attrs) { super(c, attrs); init(); }

    private void init() {
        barPaint.setStyle(Paint.Style.FILL);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dpToPx(0f));
        linePaint.setColor(Color.argb(40, 128, 128, 128));
    }

    public void setScores(List<Integer> data) {
        scores.clear();
        scores.addAll(data);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (scores.isEmpty()) return;
        float w = getWidth(), h = getHeight();

        // Baseline guide at 60 and 80
        for (float mark : new float[]{60, 80}) {
            float y = h - (mark / 100f) * h;
            canvas.drawLine(0, y, w, y, linePaint);
        }

        int count = scores.size();
        float gap     = dpToPx(4);
        float barW    = (w - gap * (count + 1)) / count;
        float cornerR = dpToPx(4);

        for (int i = 0; i < count; i++) {
            int score = scores.get(i);
            float barH = (score / 100f) * h;
            float left = gap + i * (barW + gap);
            RectF rect = new RectF(left, h - barH, left + barW, h);
            barPaint.setColor(scoreColor(score));
            canvas.drawRoundRect(rect, cornerR, cornerR, barPaint);
        }
    }

    private int scoreColor(int score) {
        if (score >= 80) return Color.parseColor("#006A6C"); // teal
        if (score >= 60) return Color.parseColor("#1565C0"); // blue
        if (score >= 40) return Color.parseColor("#E65100"); // orange
        return                  Color.parseColor("#B71C1C"); // red
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}