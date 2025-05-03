package com.example.mlkitapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.ArrayList;
import java.util.List;

public class FaceDetectionOverlay extends View {
    private List<Face> faces;
    private int imageWidth;
    private int imageHeight;
    private boolean isFacingFront;
    private boolean isPaused = false;
    private boolean showLandmarks = true;
    private boolean showContours = false;

    private final Paint facePaint;
    private final Paint landmarkPaint;
    private final Paint textPaint;
    private final Paint pausedTextPaint;
    private final Paint contourPaint;
    private final Paint smilePaint;
    private final Paint eyeOpenPaint;
    private final Paint eyeClosedPaint;

    public FaceDetectionOverlay(Context context) {
        this(context, null);
    }

    public FaceDetectionOverlay(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceDetectionOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        faces = new ArrayList<>();
        
        // Initialize paint objects for drawing
        facePaint = new Paint();
        facePaint.setColor(Color.GREEN);
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(5);

        landmarkPaint = new Paint();
        landmarkPaint.setColor(Color.RED);
        landmarkPaint.setStyle(Paint.Style.FILL);
        landmarkPaint.setStrokeWidth(8);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setShadowLayer(5, 0, 0, Color.BLACK);

        pausedTextPaint = new Paint();
        pausedTextPaint.setColor(Color.RED);
        pausedTextPaint.setTextSize(70);
        pausedTextPaint.setTextAlign(Paint.Align.CENTER);
        pausedTextPaint.setShadowLayer(5, 0, 0, Color.BLACK);
        
        contourPaint = new Paint();
        contourPaint.setColor(Color.CYAN);
        contourPaint.setStyle(Paint.Style.STROKE);
        contourPaint.setStrokeWidth(4);
        
        smilePaint = new Paint();
        smilePaint.setColor(Color.YELLOW);
        smilePaint.setStyle(Paint.Style.STROKE);
        smilePaint.setStrokeWidth(4);
        
        eyeOpenPaint = new Paint();
        eyeOpenPaint.setColor(Color.GREEN);
        eyeOpenPaint.setStyle(Paint.Style.STROKE);
        eyeOpenPaint.setStrokeWidth(4);
        
        eyeClosedPaint = new Paint();
        eyeClosedPaint.setColor(Color.RED);
        eyeClosedPaint.setStyle(Paint.Style.STROKE);
        eyeClosedPaint.setStrokeWidth(4);
    }

    public void updateFaces(List<Face> faces, int imageWidth, int imageHeight, boolean isFacingFront) {
        if (isPaused) return;
        
        this.faces = faces;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.isFacingFront = isFacingFront;
        invalidate(); // Trigger redraw
    }
    
    public void setShowLandmarks(boolean showLandmarks) {
        this.showLandmarks = showLandmarks;
        invalidate(); // Redraw the overlay
    }
    
    public void setShowContours(boolean showContours) {
        this.showContours = showContours;
        invalidate(); // Redraw the overlay
    }
    
    public void pauseAnalysis() {
        isPaused = true;
        invalidate(); // Trigger redraw to show paused state
    }
    
    public void resumeAnalysis() {
        isPaused = false;
        invalidate(); // Trigger redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (faces == null || faces.isEmpty()) {
            return;
        }
        
        // If paused, show paused text
        if (isPaused) {
            canvas.drawText("PAUSED", getWidth() / 2f, getHeight() / 2f, pausedTextPaint);
        }

        float scaleX = getWidth() / (float) (isFacingFront ? imageWidth : imageWidth);
        float scaleY = getHeight() / (float) imageHeight;

        // Draw each detected face
        for (Face face : faces) {
            drawFaceBox(canvas, face, scaleX, scaleY);
            
            if (showLandmarks) {
                drawFaceLandmarks(canvas, face, scaleX, scaleY);
            }
            
            if (showContours) {
                drawFaceContours(canvas, face, scaleX, scaleY);
            }
        }
    }

    private void drawFaceBox(Canvas canvas, Face face, float scaleX, float scaleY) {
        // Mirror if front-facing camera
        float left = face.getBoundingBox().left;
        if (isFacingFront) {
            left = imageWidth - left - face.getBoundingBox().width();
        }
        
        RectF adjustedBoundingBox = new RectF(
                left * scaleX,
                face.getBoundingBox().top * scaleY,
                (left + face.getBoundingBox().width()) * scaleX,
                (face.getBoundingBox().top + face.getBoundingBox().height()) * scaleY
        );
        
        // Draw bounding box
        canvas.drawRect(adjustedBoundingBox, facePaint);
        
        // Draw tracking ID and smile probability
        String faceInfo = "";
        if (face.getTrackingId() != null) {
            faceInfo += "ID: " + face.getTrackingId();
        }
        
        if (face.getSmilingProbability() != null) {
            faceInfo += (faceInfo.isEmpty() ? "" : " | ") + 
                    String.format("Smile: %.1f%%", face.getSmilingProbability() * 100);
        }
        
        // Draw face info text
        if (!faceInfo.isEmpty()) {
            canvas.drawText(faceInfo, 
                    adjustedBoundingBox.left, 
                    adjustedBoundingBox.top - 10, 
                    textPaint);
        }
    }

    private void drawFaceLandmarks(Canvas canvas, Face face, float scaleX, float scaleY) {
        // Draw key face landmarks if detected
        drawLandmark(canvas, face, FaceLandmark.LEFT_EYE, scaleX, scaleY);
        drawLandmark(canvas, face, FaceLandmark.RIGHT_EYE, scaleX, scaleY);
        drawLandmark(canvas, face, FaceLandmark.LEFT_EAR, scaleX, scaleY);
        drawLandmark(canvas, face, FaceLandmark.RIGHT_EAR, scaleX, scaleY);
        drawLandmark(canvas, face, FaceLandmark.NOSE_BASE, scaleX, scaleY);
        drawLandmark(canvas, face, FaceLandmark.MOUTH_LEFT, scaleX, scaleY);
        drawLandmark(canvas, face, FaceLandmark.MOUTH_RIGHT, scaleX, scaleY);
        drawLandmark(canvas, face, FaceLandmark.MOUTH_BOTTOM, scaleX, scaleY);
        
        // Draw eye circles with different colors based on eye open probability
        drawEyeCircle(canvas, face, FaceLandmark.LEFT_EYE, face.getLeftEyeOpenProbability(), scaleX, scaleY);
        drawEyeCircle(canvas, face, FaceLandmark.RIGHT_EYE, face.getRightEyeOpenProbability(), scaleX, scaleY);
        
        // Draw mouth with different color based on smile probability
        drawMouth(canvas, face, scaleX, scaleY);
    }
    
    private void drawEyeCircle(Canvas canvas, Face face, int landmarkId, Float openProbability, float scaleX, float scaleY) {
        FaceLandmark landmark = face.getLandmark(landmarkId);
        if (landmark == null) return;
        
        // Get position
        float x = landmark.getPosition().x;
        if (isFacingFront) {
            x = imageWidth - x;
        }
        float cx = x * scaleX;
        float cy = landmark.getPosition().y * scaleY;
        
        // Choose paint based on eye open probability
        Paint paint = eyeOpenPaint;
        if (openProbability != null) {
            paint = (openProbability > 0.5f) ? eyeOpenPaint : eyeClosedPaint;
        }
        
        // Draw eye circle
        float radius = 15 * Math.min(scaleX, scaleY);
        canvas.drawCircle(cx, cy, radius, paint);
    }
    
    private void drawMouth(Canvas canvas, Face face, float scaleX, float scaleY) {
        FaceLandmark leftMouth = face.getLandmark(FaceLandmark.MOUTH_LEFT);
        FaceLandmark rightMouth = face.getLandmark(FaceLandmark.MOUTH_RIGHT);
        FaceLandmark bottomMouth = face.getLandmark(FaceLandmark.MOUTH_BOTTOM);
        
        if (leftMouth == null || rightMouth == null || bottomMouth == null) return;
        
        // Mirror coordinates for front-facing camera
        float leftX = leftMouth.getPosition().x;
        float rightX = rightMouth.getPosition().x;
        if (isFacingFront) {
            leftX = imageWidth - leftX;
            rightX = imageWidth - rightX;
        }
        
        float leftMouthX = leftX * scaleX;
        float leftMouthY = leftMouth.getPosition().y * scaleY;
        float rightMouthX = rightX * scaleX;
        float rightMouthY = rightMouth.getPosition().y * scaleY;
        float bottomMouthX = bottomMouth.getPosition().x;
        if (isFacingFront) {
            bottomMouthX = imageWidth - bottomMouthX;
        }
        bottomMouthX *= scaleX;
        float bottomMouthY = bottomMouth.getPosition().y * scaleY;
        
        // Adjust smile curve height based on smile probability
        float smileHeight = 0;
        if (face.getSmilingProbability() != null) {
            smileHeight = face.getSmilingProbability() * 20;
            smilePaint.setColor(Color.HSVToColor(new float[]{
                    120 * face.getSmilingProbability(), 1f, 1f
            }));
        }
        
        // Draw mouth curve
        RectF mouthRect = new RectF(
                leftMouthX - 5,
                Math.min(leftMouthY, rightMouthY) - 10,
                rightMouthX + 5,
                bottomMouthY + 10 + smileHeight
        );
        
        float startAngle = 0;
        float sweepAngle = 180;
        canvas.drawArc(mouthRect, startAngle, sweepAngle, false, smilePaint);
    }

    private void drawLandmark(Canvas canvas, Face face, int landmarkId, float scaleX, float scaleY) {
        FaceLandmark landmark = face.getLandmark(landmarkId);
        if (landmark == null) return;
        
        float x = landmark.getPosition().x;
        if (isFacingFront) {
            x = imageWidth - x;
        }
        float scaledX = x * scaleX;
        float scaledY = landmark.getPosition().y * scaleY;
        
        canvas.drawCircle(scaledX, scaledY, 8, landmarkPaint);
    }

    private void drawFaceContours(Canvas canvas, Face face, float scaleX, float scaleY) {
        // Draw face contours
        drawContour(canvas, face, FaceContour.FACE, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.LEFT_EYEBROW_TOP, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.LEFT_EYEBROW_BOTTOM, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.RIGHT_EYEBROW_TOP, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.RIGHT_EYEBROW_BOTTOM, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.LEFT_EYE, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.RIGHT_EYE, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.UPPER_LIP_TOP, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.UPPER_LIP_BOTTOM, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.LOWER_LIP_TOP, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.LOWER_LIP_BOTTOM, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.NOSE_BRIDGE, scaleX, scaleY);
        drawContour(canvas, face, FaceContour.NOSE_BOTTOM, scaleX, scaleY);
    }

    private void drawContour(Canvas canvas, Face face, int contourId, float scaleX, float scaleY) {
        FaceContour contour = face.getContour(contourId);
        if (contour == null) return;
        
        List<PointF> points = contour.getPoints();
        if (points.isEmpty()) return;
        
        // For the first point
        PointF firstPoint = points.get(0);
        float lastX = firstPoint.x;
        if (isFacingFront) {
            lastX = imageWidth - lastX;
        }
        float lastY = firstPoint.y;
        
        // Connect all points with lines
        for (int i = 1; i < points.size(); i++) {
            PointF point = points.get(i);
            float x = point.x;
            if (isFacingFront) {
                x = imageWidth - x;
            }
            float y = point.y;
            
            canvas.drawLine(
                    lastX * scaleX, lastY * scaleY,
                    x * scaleX, y * scaleY,
                    contourPaint);
            
            lastX = x;
            lastY = y;
        }
        
        // Close the contour if needed (like for eyes or face)
        if (contourId == FaceContour.FACE || 
                contourId == FaceContour.LEFT_EYE || 
                contourId == FaceContour.RIGHT_EYE) {
            PointF firstP = points.get(0);
            float firstX = firstP.x;
            if (isFacingFront) {
                firstX = imageWidth - firstX;
            }
            float firstY = firstP.y;
            
            canvas.drawLine(
                    lastX * scaleX, lastY * scaleY,
                    firstX * scaleX, firstY * scaleY,
                    contourPaint);
        }
    }
}