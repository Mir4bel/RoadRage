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

    public static final String[] EMOJIS     = {"😊", "😌", "😐", "😴", "😣"};
    public static final String[] MOOD_NAMES = {"Happy", "Calm", "Neutral", "Tired", "Stressed"};
    private static final int COUNT = EMOJIS.length;

    // scrollOffset: continuous float where integers = an emoji is exactly at center
    private float scrollOffset = 0f;
    private float lastTouchX   = 0f;
    private ValueAnimator snapAnimator;

    private final Paint emojiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    public interface MoodSelectedCallback {
        void onMoodSelected(int index, String moodName, String emoji);
    }
    private MoodSelectedCallback callback;

    public MoodWheelView(Context context)                        { super(context);       init(); }
    public MoodWheelView(Context context, AttributeSet attrs)    { super(context, attrs); init(); }

    private void init() {
        emojiPaint.setTextAlign(Paint.Align.CENTER);

        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(dpToPx(2.5f));
        ringPaint.setColor(Color.parseColor("#006A6C")); // teal selection ring
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w  = getWidth();
        float h  = getHeight();
        float cx = w / 2f;

        float spacing = w * 0.23f;   // horizontal distance between emoji slots
        float arcDip  = h * 0.55f;   // how steeply the arc dips (higher = rounder arc)

        for (int i = 0; i < COUNT; i++) {
            // relPos: distance from the current scroll center (-2 to +2 for 5 items)
            float rel = i - scrollOffset;
            // Wrap into [-COUNT/2, COUNT/2) so the list is circular
            while (rel >  COUNT / 2f) rel -= COUNT;
            while (rel <= -COUNT / 2f) rel += COUNT;

            if (Math.abs(rel) > 2.6f) continue; // off-screen, skip

            float x = cx + rel * spacing;
            // Parabolic arc: center emoji sits highest, others dip down
            float y = h * 0.42f + rel * rel * arcDip;

            // Scale: center = full size + fully opaque, edges = small + faint
            float proximity   = 1f - Math.min(1f, Math.abs(rel) * 0.52f);
            float emojiSizePx = spToPx(16 + 24 * proximity);
            float alpha       = 0.22f + 0.78f * proximity;

            emojiPaint.setTextSize(emojiSizePx);
            emojiPaint.setAlpha((int)(255 * alpha));
            canvas.drawText(EMOJIS[i], x, y, emojiPaint);

            // Teal ring around whichever emoji is closest to center
            if (Math.abs(rel) < 0.28f) {
                float ringR = emojiSizePx * 0.68f;
                canvas.drawCircle(x, y - emojiSizePx * 0.38f, ringR, ringPaint);
            }
        }
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
                // Convert pixel drag to emoji-slot units
                scrollOffset -= dx / (getWidth() * 0.23f);
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
        callback.onMoodSelected(idx, MOOD_NAMES[idx], EMOJIS[idx]);
    }

    public int selectedIndex() {
        int idx = Math.round(scrollOffset) % COUNT;
        return idx < 0 ? idx + COUNT : idx;
    }

    public String getSelectedMood()  { return MOOD_NAMES[selectedIndex()]; }
    public String getSelectedEmoji() { return EMOJIS[selectedIndex()]; }

    public void setOnMoodSelectedCallback(MoodSelectedCallback cb) { this.callback = cb; }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    private float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}