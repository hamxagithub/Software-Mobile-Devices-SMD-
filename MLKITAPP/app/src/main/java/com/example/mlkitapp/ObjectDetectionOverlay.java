package com.example.mlkitapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.objects.DetectedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ObjectDetectionOverlay extends View {
    private static final String TAG = "ObjectDetectionOverlay";
    private List<DetectedObject> objects = new ArrayList<>();
    private final Paint boxPaint;
    private final Paint textBackgroundPaint;
    private final Paint textPaint;
    private final Paint highlightPaint; // New paint for highlighting objects
    private final int[] colors = {
            Color.rgb(255, 64, 129),  // Pink
            Color.rgb(33, 150, 243),  // Blue
            Color.rgb(255, 193, 7),   // Amber
            Color.rgb(0, 150, 136),   // Teal
            Color.rgb(139, 195, 74),  // Light Green
            Color.rgb(103, 58, 183),  // Deep Purple
            Color.rgb(255, 87, 34),   // Deep Orange
            Color.rgb(3, 169, 244)    // Light Blue
    };
    
    // Store previous object positions for smoother transitions
    private final Map<Integer, RectF> previousPositions = new HashMap<>();
    
    private int previewWidth;
    private int previewHeight;
    private boolean isAnalyzing = true;
    private boolean isTrackingMode = false;
    private float confidenceThreshold = 0.5f;
    private boolean singleObjectMode = false;
    private boolean showLabels = true;
    private boolean showConfidence = true;
    
    // View orientation variables
    private int viewRotation = 0; // 0, 90, 180, or 270 degrees
    private boolean isFrontCamera = false;
    private Matrix transformationMatrix = new Matrix();

    public ObjectDetectionOverlay(Context context) {
        this(context, null);
    }

    public ObjectDetectionOverlay(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObjectDetectionOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        boxPaint = new Paint();
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5.0f);
        boxPaint.setAntiAlias(true);

        textBackgroundPaint = new Paint();
        textBackgroundPaint.setColor(Color.BLACK);
        textBackgroundPaint.setAlpha(160);
        textBackgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36.0f);
        textPaint.setAntiAlias(true);
        
        highlightPaint = new Paint();
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setAlpha(80);
    }

    public void updateObjects(List<DetectedObject> detectedObjects, int width, int height) {
        if (!isAnalyzing) {
            return;
        }
        
        Log.d(TAG, "Updating objects: received " + detectedObjects.size() + 
              " objects, screen: " + getWidth() + "x" + getHeight() + 
              ", preview: " + width + "x" + height);

        this.previewWidth = width;
        this.previewHeight = height;

        // Update transformation matrix based on rotation and camera
        updateTransformationMatrix();
        
        // Filter objects by confidence threshold
        List<DetectedObject> filteredObjects = new ArrayList<>();
        for (DetectedObject object : detectedObjects) {
            List<DetectedObject.Label> labels = object.getLabels();
            if (!labels.isEmpty()) {
                float maxConfidence = 0;
                for (DetectedObject.Label label : labels) {
                    maxConfidence = Math.max(maxConfidence, label.getConfidence());
                }
                if (maxConfidence >= confidenceThreshold) {
                    filteredObjects.add(object);
                }
            }
        }

        // If in single object mode, keep only the highest confidence object
        if (singleObjectMode && filteredObjects.size() > 1) {
            DetectedObject bestObject = null;
            float bestConfidence = 0;

            for (DetectedObject object : filteredObjects) {
                for (DetectedObject.Label label : object.getLabels()) {
                    if (label.getConfidence() > bestConfidence) {
                        bestConfidence = label.getConfidence();
                        bestObject = object;
                    }
                }
            }

            filteredObjects.clear();
            if (bestObject != null) {
                filteredObjects.add(bestObject);
            }
        }
        
        // Clean up tracking IDs that are no longer present
        if (isTrackingMode) {
            List<Integer> currentIds = new ArrayList<>();
            for (DetectedObject obj : filteredObjects) {
                if (obj.getTrackingId() != null) {
                    currentIds.add(obj.getTrackingId());
                }
            }
            
            List<Integer> keysToRemove = new ArrayList<>();
            for (Integer id : previousPositions.keySet()) {
                if (!currentIds.contains(id)) {
                    keysToRemove.add(id);
                }
            }
            
            for (Integer id : keysToRemove) {
                previousPositions.remove(id);
            }
        }

        this.objects = filteredObjects;
        invalidate();
    }

    private void updateTransformationMatrix() {
        transformationMatrix.reset();
        
        // Calculate the scaling factors
        float scaleX = (float) getWidth() / previewWidth;
        float scaleY = (float) getHeight() / previewHeight;
        
        // Apply scaling
        transformationMatrix.postScale(scaleX, scaleY);
        
        // Apply rotation if needed
        if (viewRotation != 0) {
            transformationMatrix.postRotate(viewRotation, getWidth() / 2f, getHeight() / 2f);
        }
        
        // Mirror image if using front camera
        if (isFrontCamera) {
            transformationMatrix.postScale(-1, 1, getWidth() / 2f, getHeight() / 2f);
        }
    }
    
    // Call this when camera changes
    public void setCameraFacing(boolean isFront) {
        this.isFrontCamera = isFront;
        updateTransformationMatrix();
        invalidate();
    }
    
    // Call this when device orientation changes
    public void setViewRotation(int rotation) {
        this.viewRotation = rotation;
        updateTransformationMatrix();
        invalidate();
    }

    public void pauseAnalysis() {
        isAnalyzing = false;
    }

    public void resumeAnalysis() {
        isAnalyzing = true;
    }

    public void setTrackingMode(boolean isTrackingMode) {
        this.isTrackingMode = isTrackingMode;
        if (!isTrackingMode) {
            previousPositions.clear();
        }
    }

    public void setConfidenceThreshold(float confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }

    public void setSingleObjectMode(boolean singleObjectMode) {
        this.singleObjectMode = singleObjectMode;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        invalidate();
    }

    public void setShowConfidence(boolean showConfidence) {
        this.showConfidence = showConfidence;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (objects.isEmpty()) {
            return;
        }

        // Calculate scale factors
        float scaleX = (float) getWidth() / previewWidth;
        float scaleY = (float) getHeight() / previewHeight;

        // Draw each object
        for (int i = 0; i < objects.size(); i++) {
            DetectedObject object = objects.get(i);
            
            // Set box color based on tracking mode or cycle through colors
            int colorIndex = isTrackingMode && object.getTrackingId() != null ? 
                Math.abs(object.getTrackingId() % colors.length) : i % colors.length;
            int color = colors[colorIndex];
            
            boxPaint.setColor(color);
            highlightPaint.setColor(color);
            
            // Get the bounding box and apply transformations
            Rect boundingBox = object.getBoundingBox();
            RectF scaledBox = new RectF(
                    boundingBox.left * scaleX,
                    boundingBox.top * scaleY,
                    boundingBox.right * scaleX,
                    boundingBox.bottom * scaleY
            );
            
            // If tracking is enabled and we have previous position, animate the transition
            if (isTrackingMode && object.getTrackingId() != null) {
                Integer trackingId = object.getTrackingId();
                if (previousPositions.containsKey(trackingId)) {
                    RectF prevBox = previousPositions.get(trackingId);
                    
                    // Smooth transition (70% new position, 30% previous position)
                    scaledBox.left = 0.7f * scaledBox.left + 0.3f * prevBox.left;
                    scaledBox.top = 0.7f * scaledBox.top + 0.3f * prevBox.top;
                    scaledBox.right = 0.7f * scaledBox.right + 0.3f * prevBox.right;
                    scaledBox.bottom = 0.7f * scaledBox.bottom + 0.3f * prevBox.bottom;
                }
                
                // Save the current position for next frame
                previousPositions.put(trackingId, new RectF(scaledBox));
            }
            
            // Apply highlight within the bounding box
            canvas.drawRect(scaledBox, highlightPaint);
            
            // Draw the bounding box border
            canvas.drawRect(scaledBox, boxPaint);
            
            if (showLabels) {
                // Find the label with highest confidence
                String labelText = "Unknown";
                float confidence = 0f;
                
                List<DetectedObject.Label> labels = object.getLabels();
                if (labels != null && !labels.isEmpty()) {
                    for (DetectedObject.Label label : labels) {
                        if (label.getConfidence() > confidence) {
                            confidence = label.getConfidence();
                            String text = label.getText();
                            if (text != null && !text.isEmpty()) {
                                labelText = text;
                            }
                        }
                    }
                }
                
                // Prepare the label text
                String displayText = showConfidence ? 
                    String.format(Locale.US, "%s (%.1f%%)", labelText, confidence * 100) : 
                    labelText;
                
                // Measure text for background
                Rect textBounds = new Rect();
                textPaint.getTextBounds(displayText, 0, displayText.length(), textBounds);
                
                // Draw text background
                float textBackgroundLeft = Math.max(0, scaledBox.left);
                float textBackgroundTop = Math.max(0, scaledBox.top - textBounds.height() - 16);
                
                canvas.drawRect(
                    textBackgroundLeft,
                    textBackgroundTop,
                    Math.min(getWidth(), textBackgroundLeft + textBounds.width() + 16),
                    scaledBox.top,
                    textBackgroundPaint
                );
                
                // Draw label text
                canvas.drawText(
                    displayText, 
                    textBackgroundLeft + 8, 
                    scaledBox.top - 8, 
                    textPaint
                );
                
                // If tracking is enabled, show the tracking ID
                if (isTrackingMode && object.getTrackingId() != null) {
                    String trackingText = "ID: " + object.getTrackingId();
                    
                    // Draw ID at the bottom of the box
                    Rect idBounds = new Rect();
                    textPaint.getTextBounds(trackingText, 0, trackingText.length(), idBounds);
                    
                    canvas.drawRect(
                        scaledBox.left,
                        scaledBox.bottom,
                        scaledBox.left + idBounds.width() + 16,
                        scaledBox.bottom + idBounds.height() + 16,
                        textBackgroundPaint
                    );
                    
                    canvas.drawText(
                        trackingText,
                        scaledBox.left + 8,
                        scaledBox.bottom + idBounds.height() + 8,
                        textPaint
                    );
                }
            }
        }
    }
}