package mira.pharaon.adu.ac.ae.roadrage_group5;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class MoodWheelView extends View {

    public static final String[] EMOJIS =
            {"😊", "😌", "😐", "😴", "😣"};

    public static final String[] MOOD_NAMES =
            {"Happy", "Calm", "Neutral", "Tired", "Stressed"};

    private static final int[] MOOD_COLORS = {
            Color.parseColor("#FFD54F"),
            Color.parseColor("#90CAF9"),
            Color.parseColor("#BDBDBD"),
            Color.parseColor("#B39DDB"),
            Color.parseColor("#EF9A9A")
    };

    private static final int COUNT = EMOJIS.length;

    private float scrollOffset = 0f;
    private float lastTouchX = 0f;

    private ValueAnimator snapAnimator;

    private final Paint emojiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public interface MoodSelectedCallback {
        void onMoodSelected(int index, String moodName, String emoji);
    }

    private MoodSelectedCallback callback;

    public MoodWheelView(Context context) {
        super(context);
        init();
    }

    public MoodWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        emojiPaint.setTextAlign(Paint.Align.CENTER);
        emojiPaint.setColor(Color.BLACK);

        circlePaint.setStyle(Paint.Style.FILL);

        pointerPaint.setColor(Color.parseColor("#5A3C2A"));
        pointerPaint.setStrokeWidth(dpToPx(4));

        shadowPaint.setStyle(Paint.Style.FILL);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float w = getWidth();
        float h = getHeight();

        float centerX = w / 2f;

        float spacing = w * 0.29f;

        float arcDepth = h * 0.38f;

        for (int i = 0; i < COUNT; i++) {

            float rel = i - scrollOffset;

            while (rel > COUNT / 2f) rel -= COUNT;
            while (rel <= -COUNT / 2f) rel += COUNT;

            float x = centerX + rel * spacing;
            float y = h * 0.42f + rel * rel * arcDepth * 0.18f;

            float proximity =
                    1f - Math.min(1f, Math.abs(rel) * 0.45f);

            // center MUCH bigger
            float emojiSizePx =
                    spToPx(18 + 52 * proximity);

            float alpha =
                    0.35f + 0.65f * proximity;

            // ===== FIX: color restored per emoji =====
            circlePaint.setColor(
                    Color.argb((int)(alpha * 140),
                            Color.red(MOOD_COLORS[i]),
                            Color.green(MOOD_COLORS[i]),
                            Color.blue(MOOD_COLORS[i]))
            );

            // subtle shadow only for far items
            if (Math.abs(rel) > 2.3f) {
                shadowPaint.setShadowLayer(
                        22,
                        0,
                        10,
                        Color.argb(70, 0, 0, 0)
                );

                canvas.drawCircle(
                        x,
                        y,
                        emojiSizePx * 0.75f,
                        shadowPaint
                );

                shadowPaint.clearShadowLayer();
            }

            // background circle
            canvas.drawCircle(
                    x,
                    y,
                    emojiSizePx * 0.75f,
                    circlePaint
            );

            // ===== FIX: NO STATE LEAK =====
            emojiPaint.setColor(Color.BLACK);
            emojiPaint.setAlpha((int)(255 * alpha));
            emojiPaint.setTextSize(emojiSizePx);

            canvas.drawText(
                    EMOJIS[i],
                    x,
                    y + (emojiSizePx * 0.35f),
                    emojiPaint
            );
        }

        float px = centerX;
        float py = h * 0.82f;

        canvas.drawLine(px, py, px - 18, py + 28, pointerPaint);
        canvas.drawLine(px, py, px + 18, py + 28, pointerPaint);
        canvas.drawLine(px - 18, py + 28, px + 18, py + 28, pointerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                if (snapAnimator != null) snapAnimator.cancel();
                lastTouchX = event.getX();
                return true;

            case MotionEvent.ACTION_MOVE:

                float dx = event.getX() - lastTouchX;

                scrollOffset -= dx / (getWidth() * 0.18f);

                lastTouchX = event.getX();

                invalidate();
                notifyCallback();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                snapToNearest();
                return true;
        }

        return false;
    }

    private void snapToNearest() {

        float target = Math.round(scrollOffset);

        snapAnimator = ValueAnimator.ofFloat(scrollOffset, target);
        snapAnimator.setDuration(220);
        snapAnimator.setInterpolator(new DecelerateInterpolator());

        snapAnimator.addUpdateListener(a -> {
            scrollOffset = (float) a.getAnimatedValue();
            invalidate();
        });

        snapAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scrollOffset = target;
                notifyCallback();
            }
        });

        snapAnimator.start();
    }

    private void notifyCallback() {
        if (callback == null) return;

        int idx = selectedIndex();

        callback.onMoodSelected(
                idx,
                MOOD_NAMES[idx],
                EMOJIS[idx]
        );
    }

    public int selectedIndex() {
        int idx = Math.round(scrollOffset) % COUNT;
        return idx < 0 ? idx + COUNT : idx;
    }

    public void setOnMoodSelectedCallback(MoodSelectedCallback cb) {
        callback = cb;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }

    private float spToPx(float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics());
    }
}