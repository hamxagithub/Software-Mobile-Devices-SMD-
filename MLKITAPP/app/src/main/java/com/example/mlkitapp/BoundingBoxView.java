package com.example.mlkitapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An optimized view for drawing bounding boxes over detected text.
 * This version uses thread-safe collections and handles device rotations.
 */
public class BoundingBoxView extends View {

    private final Paint boxPaint;
    private final Paint textBackgroundPaint;
    private final Paint textPaint;

    // Use thread-safe collection to prevent concurrent modification issues
    private final List<Rect> boxes = new CopyOnWriteArrayList<>();

    // Variables for scaling factors
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private int imageWidth = 1080; // Default value
    private int imageHeight = 1920; // Default value
    private int deviceRotation = 0; // 0, 90, 180, 270 degrees

    // Animation properties
    private boolean animateBoxes = true;
    private Handler animationHandler;
    private float animationProgress = 0f;
    private static final long ANIMATION_DURATION = 300; // ms

    public BoundingBoxView(Context context) {
        this(context, null);
    }

    public BoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Hardware acceleration improves drawing performance
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Initialize the paint for drawing boxes
        boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(6.0f);
        boxPaint.setAntiAlias(true);

        // Initialize paint for text background
        textBackgroundPaint = new Paint();
        textBackgroundPaint.setColor(Color.parseColor("#80000000")); // Semi-transparent black
        textBackgroundPaint.setStyle(Paint.Style.FILL);

        // Initialize paint for text
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);
        textPaint.setAntiAlias(true);

        // Initialize animation handler
        animationHandler = new Handler();
    }

    /**
     * Clears all the boxes with optional animation
     */
    public void clearBoxes() {
        if (boxes.isEmpty()) return;

        if (animateBoxes) {
            animateOut();
        } else {
            boxes.clear();
            invalidate();
        }
    }

    /**
     * Adds a bounding box to be drawn
     */
    public void addBox(Rect box) {
        if (box == null) return;
        boxes.add(box);

        if (animateBoxes) {
            animateIn();
        } else {
            invalidate();
        }
    }

    /**
     * Sets multiple bounding boxes at once
     */
    public void setBoxes(List<Rect> boxList) {
        if (boxList == null) return;

        boxes.clear();

        // Add only valid boxes
        for (Rect box : boxList) {
            if (box != null && box.width() > 0 && box.height() > 0) {
                boxes.add(box);
            }
        }

        if (animateBoxes) {
            animateIn();
        } else {
            invalidate();
        }
    }

    /**
     * Sets the bounding boxes to be displayed.
     * This is an alias for setBoxes() method to maintain compatibility.
     *
     * @param boundingBoxes List of Rect objects representing bounding boxes
     */
    public void setBoundingBoxes(List<Rect> boundingBoxes) {
        setBoxes(boundingBoxes);
    }

    /**
     * Clears all bounding boxes from the view.
     * This is an alias for clearBoxes() method to maintain compatibility.
     */
    public void clear() {
        clearBoxes();
    }

    /**
     * Updates scale factors based on actual image dimensions
     */
    public void updateScaleFactors(int imageWidth, int imageHeight) {
        if (imageWidth > 0 && imageHeight > 0) {
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            recalculateScaleFactors();
        }
    }

    /**
     * Sets the device rotation in degrees (0, 90, 180, 270)
     */
    public void setDeviceRotation(int rotation) {
        this.deviceRotation = rotation;
        recalculateScaleFactors();
    }

    /**
     * Enable or disable animations for box display
     */
    public void setAnimateBoxes(boolean animate) {
        this.animateBoxes = animate;
    }

    /**
     * Sets the stroke width for bounding boxes.
     */
    public void setLineWidth(float width) {
        boxPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * Recalculate scale factors based on view and image dimensions
     */
    private void recalculateScaleFactors() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        if (viewWidth == 0 || viewHeight == 0 || imageWidth == 0 || imageHeight == 0) {
            return;
        }

        // Handle rotation - swap dimensions if needed
        if (deviceRotation == 90 || deviceRotation == 270) {
            scaleX = (float) viewWidth / imageHeight;
            scaleY = (float) viewHeight / imageWidth;
        } else {
            scaleX = (float) viewWidth / imageWidth;
            scaleY = (float) viewHeight / imageHeight;
        }

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        recalculateScaleFactors();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (boxes.isEmpty()) return;

        // Use local variables for thread safety
        float localScaleX = this.scaleX;
        float localScaleY = this.scaleY;

        for (Rect box : boxes) {
            if (box != null) {
                RectF scaledBox;

                // Apply appropriate transformation based on device rotation
                switch (deviceRotation) {
                    case 90:
                        scaledBox = new RectF(
                                getHeight() - box.bottom * localScaleY,
                                box.left * localScaleX,
                                getHeight() - box.top * localScaleY,
                                box.right * localScaleX);
                        break;
                    case 180:
                        scaledBox = new RectF(
                                getWidth() - box.right * localScaleX,
                                getHeight() - box.bottom * localScaleY,
                                getWidth() - box.left * localScaleX,
                                getHeight() - box.top * localScaleY);
                        break;
                    case 270:
                        scaledBox = new RectF(
                                box.top * localScaleY,
                                getWidth() - box.right * localScaleX,
                                box.bottom * localScaleY,
                                getWidth() - box.left * localScaleX);
                        break;
                    default: // 0 degrees
                        scaledBox = new RectF(
                                box.left * localScaleX,
                                box.top * localScaleY,
                                box.right * localScaleX,
                                box.bottom * localScaleY);
                }

                if (animateBoxes) {
                    // Apply animation scaling effect
                    float centerX = scaledBox.centerX();
                    float centerY = scaledBox.centerY();
                    float halfWidth = scaledBox.width() / 2 * animationProgress;
                    float halfHeight = scaledBox.height() / 2 * animationProgress;

                    scaledBox.set(
                            centerX - halfWidth,
                            centerY - halfHeight,
                            centerX + halfWidth,
                            centerY + halfHeight
                    );
                }

                // Draw the box with rounded corners
                canvas.drawRoundRect(scaledBox, 8f, 8f, boxPaint);
            }
        }

        // Draw the count indicator if there are multiple boxes
        if (boxes.size() > 1) {
            String countText = boxes.size() + " texts";
            float textWidth = textPaint.measureText(countText);
            float textHeight = textPaint.getTextSize();

            // Draw background for text
            canvas.drawRect(
                    10,
                    10,
                    20 + textWidth,
                    20 + textHeight,
                    textBackgroundPaint
            );

            // Draw text
            canvas.drawText(countText, 15, 15 + textHeight * 0.75f, textPaint);
        }
    }

    /**
     * Animate boxes fading in
     */
    private void animateIn() {
        animationHandler.removeCallbacksAndMessages(null);
        animationProgress = 0f;

        final long startTime = System.currentTimeMillis();

        animationHandler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                animationProgress = Math.min(1f, (float) elapsed / ANIMATION_DURATION);
                invalidate();

                if (animationProgress < 1f) {
                    animationHandler.postDelayed(this, 16); // ~60fps
                }
            }
        });
    }

    /**
     * Animate boxes fading out
     */
    private void animateOut() {
        animationHandler.removeCallbacksAndMessages(null);
        animationProgress = 1f;

        final long startTime = System.currentTimeMillis();
        final List<Rect> currentBoxes = new ArrayList<>(boxes);

        animationHandler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                animationProgress = Math.max(0f, 1f - (float) elapsed / ANIMATION_DURATION);
                invalidate();

                if (animationProgress > 0f) {
                    animationHandler.postDelayed(this, 16); // ~60fps
                } else {
                    boxes.clear();
                    invalidate();
                }
            }
        });
    }

    /**
     * Clean up resources when view is detached
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animationHandler.removeCallbacksAndMessages(null);
        boxes.clear();
    }
}