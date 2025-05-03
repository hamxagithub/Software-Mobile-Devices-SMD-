package com.example.mlkitapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.annotation.OptIn(markerClass = ExperimentalGetImage.class)
public class FaceDetectionActivity extends AppCompatActivity {
    private static final String TAG = "FaceDetectionActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private PreviewView previewView;
    private FaceDetectionOverlay faceOverlay;
    private Camera camera;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;
    private boolean showLandmarks = true;
    private boolean showContours = false;
    private boolean enableClassification = true;
    private boolean isPaused = false;
    private boolean isFrontFacing = true;
    
    // UI elements
    private TextView tvFaceCount;
    private FloatingActionButton btnCaptureFrame, btnToggleFeatures, btnFlipCamera, btnSettings, btnShare;
    private RecyclerView rvFaces;
    private FaceAdapter faceAdapter;
    
    // Settings elements
    private NestedScrollView settingsBottomSheet;
    private BottomSheetBehavior<NestedScrollView> bottomSheetBehavior;
    private SwitchMaterial switchLandmarks, switchContours, switchClassification;
    private RadioButton radioPerformanceFast, radioPerformanceAccurate;
    private SeekBar seekbarMinFaceSize;
    private TextView tvMinFaceSize;
    private Button btnApplySettings;
    
    // Image capture
    private ImageCapture imageCapture;
    private File outputDirectory;
    private float minFaceSize = 0.15f;
    private int performanceMode = FaceDetectorOptions.PERFORMANCE_MODE_FAST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        // Initialize UI elements
        initViews();
        setupBottomSheet();
        setupClickListeners();

        // Setup RecyclerView
        faceAdapter = new FaceAdapter();
        rvFaces.setLayoutManager(new LinearLayoutManager(this));
        rvFaces.setAdapter(faceAdapter);

        // Set up ML Kit Face Detector with appropriate options
        createFaceDetector();
        
        // Create output directory for captures
        outputDirectory = getOutputDirectory();

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
    }
    
    private void initViews() {
        previewView = findViewById(R.id.preview_view);
        faceOverlay = findViewById(R.id.face_overlay);
        tvFaceCount = findViewById(R.id.tv_face_count);
        btnCaptureFrame = findViewById(R.id.btn_capture_frame);
        btnToggleFeatures = findViewById(R.id.btn_toggle_features);
        btnFlipCamera = findViewById(R.id.btn_flip_camera);
        btnSettings = findViewById(R.id.btn_settings);
        btnShare = findViewById(R.id.btn_share);
        rvFaces = findViewById(R.id.rv_faces);
        settingsBottomSheet = findViewById(R.id.settings_bottom_sheet);
        
        // Settings views
        switchLandmarks = findViewById(R.id.switch_landmarks);
        switchContours = findViewById(R.id.switch_contours);
        switchClassification = findViewById(R.id.switch_classification);
        radioPerformanceFast = findViewById(R.id.radio_performance_fast);
        radioPerformanceAccurate = findViewById(R.id.radio_performance_accurate);
        seekbarMinFaceSize = findViewById(R.id.seekbar_min_face_size);
        tvMinFaceSize = findViewById(R.id.tv_min_face_size);
        btnApplySettings = findViewById(R.id.btn_apply_settings);
    }
    
    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(settingsBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    // When bottom sheet is hidden, you may want to update UI
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Optional: you can add animations based on slide offset
            }
        });
        
        // Set up seekbar for min face size
        seekbarMinFaceSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMinFaceSize.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupClickListeners() {
        // Toggle landmarks display
        btnToggleFeatures.setOnClickListener(v -> {
            showLandmarks = !showLandmarks;
            faceOverlay.setShowLandmarks(showLandmarks);
            Snackbar.make(v, showLandmarks ? "Features visible" : "Features hidden", 
                    Snackbar.LENGTH_SHORT).show();
        });

        // Capture or pause frame
        btnCaptureFrame.setOnClickListener(v -> {
            if (isPaused) {
                // Resume analysis
                isPaused = false;
                faceOverlay.resumeAnalysis();
                btnCaptureFrame.setImageResource(android.R.drawable.ic_menu_camera);
                Snackbar.make(v, "Resumed", Snackbar.LENGTH_SHORT).show();
            } else {
                // Take a photo
                takePicture();
            }
        });
        
        // Flip camera
        btnFlipCamera.setOnClickListener(v -> {
            isFrontFacing = !isFrontFacing;
            startCamera();
            Snackbar.make(v, isFrontFacing ? "Front camera" : "Back camera", 
                    Snackbar.LENGTH_SHORT).show();
        });
        
        // Open settings
        btnSettings.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        
        // Share detected faces data
        btnShare.setOnClickListener(v -> {
            if (faceAdapter.getItemCount() > 0) {
                shareResults();
            } else {
                Snackbar.make(v, "No faces to share", Snackbar.LENGTH_SHORT).show();
            }
        });
        
        // Apply settings button
        btnApplySettings.setOnClickListener(v -> {
            applySettings();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            Snackbar.make(v, "Settings applied", Snackbar.LENGTH_SHORT).show();
        });
    }
    
    private void applySettings() {
        // Read values from UI
        showLandmarks = switchLandmarks.isChecked();
        showContours = switchContours.isChecked();
        enableClassification = switchClassification.isChecked();
        performanceMode = radioPerformanceFast.isChecked() ? 
                FaceDetectorOptions.PERFORMANCE_MODE_FAST : 
                FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE;
        minFaceSize = seekbarMinFaceSize.getProgress() / 100f;
        
        // Update the face overlay
        faceOverlay.setShowLandmarks(showLandmarks);
        faceOverlay.setShowContours(showContours);
        
        // Recreate face detector with new options
        createFaceDetector();
        
        // Restart camera to apply changes
        startCamera();
    }

    private void createFaceDetector() {
        // Build face detector options based on current settings
        FaceDetectorOptions.Builder optionsBuilder = new FaceDetectorOptions.Builder()
                .setPerformanceMode(performanceMode)
                .setMinFaceSize(minFaceSize)
                .enableTracking();
                
        // Apply optional settings
        if (showLandmarks) {
            optionsBuilder.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL);
        }
        
        if (showContours) {
            optionsBuilder.setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL);
        }
        
        if (enableClassification) {
            optionsBuilder.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL);
        }
        
        FaceDetectorOptions options = optionsBuilder.build();
        faceDetector = FaceDetection.getClient(options);
    }
    
    private void takePicture() {
        if (imageCapture == null) {
            Snackbar.make(findViewById(R.id.preview_view), 
                    "Camera not ready yet", Snackbar.LENGTH_SHORT).show();
            return;
        }
        
        // Create output file
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
        File outputFile = new File(outputDirectory, "face-" + timestamp + ".jpg");
        
        // Setup image capture metadata
        ImageCapture.OutputFileOptions outputFileOptions = 
                new ImageCapture.OutputFileOptions.Builder(outputFile).build();
                
        // Take the picture
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Flash animation
                        View flashView = new View(FaceDetectionActivity.this);
                        flashView.setBackgroundColor(ContextCompat.getColor(FaceDetectionActivity.this, 
                                android.R.color.white));
                        flashView.setAlpha(0.7f);
                        ((androidx.constraintlayout.widget.ConstraintLayout) 
                                findViewById(R.id.preview_view).getParent()).addView(flashView);
                        flashView.animate().alpha(0f).setDuration(100).withEndAction(() -> {
                            ((androidx.constraintlayout.widget.ConstraintLayout) 
                                    findViewById(R.id.preview_view).getParent()).removeView(flashView);
                        }).start();
                        
                        // Show success message with the image path
                        Snackbar.make(findViewById(R.id.preview_view),
                                "Image saved: " + outputFile.getName(),
                                Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                        Snackbar.make(findViewById(R.id.preview_view),
                                "Failed to save image", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
    
    private File getOutputDirectory() {
        File mediaDir = new File(getExternalMediaDirs()[0], "FaceDetection");
        if (!mediaDir.exists()) {
            if (!mediaDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory");
                return getExternalMediaDirs()[0];
            }
        }
        return mediaDir;
    }
    
    private void shareResults() {
        // Create a temp bitmap with the detected faces info
        StringBuilder facesInfo = new StringBuilder("Face Detection Results\n\n");
        
        List<Face> faces = faceAdapter.getFaces();
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);
            facesInfo.append("Face #").append(i + 1).append("\n");
            if (face.getSmilingProbability() != null) {
                facesInfo.append("Smiling: ").append(String.format("%.1f%%", 
                        face.getSmilingProbability() * 100)).append("\n");
            }
            if (face.getRightEyeOpenProbability() != null) {
                facesInfo.append("Right eye open: ").append(String.format("%.1f%%", 
                        face.getRightEyeOpenProbability() * 100)).append("\n");
            }
            if (face.getLeftEyeOpenProbability() != null) {
                facesInfo.append("Left eye open: ").append(String.format("%.1f%%", 
                        face.getLeftEyeOpenProbability() * 100)).append("\n");
            }
            facesInfo.append("\n");
        }
        
        // Create a text sharing intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Face Detection Results");
        shareIntent.putExtra(Intent.EXTRA_TEXT, facesInfo.toString());
        startActivity(Intent.createChooser(shareIntent, "Share faces data via"));
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Image analysis use case
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(480, 640))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageForFaceDetection);
                
                // Image capture use case
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();

                // Select camera based on user preference
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(isFrontFacing ? 
                                CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                        .build();

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @ExperimentalGetImage
    private void processImageForFaceDetection(ImageProxy imageProxy) {
        try {
            if (isPaused || imageProxy.getImage() == null) {
                imageProxy.close();
                return;
            }

            InputImage image = InputImage.fromMediaImage(
                    imageProxy.getImage(),
                    imageProxy.getImageInfo().getRotationDegrees()
            );

            faceDetector.process(image)
                    .addOnSuccessListener(faces -> {
                        if (!isPaused) {
                            updateFaceUI(faces);
                            faceOverlay.updateFaces(
                                    faces,
                                    imageProxy.getWidth(),
                                    imageProxy.getHeight(),
                                    isFrontFacing
                            );
                        }
                        imageProxy.close();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Face detection failed", e);
                        imageProxy.close();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error processing image for face detection", e);
            imageProxy.close();
        }
    }

    private void updateFaceUI(List<Face> faces) {
        runOnUiThread(() -> {
            // Update face count text
            tvFaceCount.setText("Faces detected: " + faces.size());
            
            // Update the RecyclerView with all faces
            faceAdapter.updateFaces(faces);
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != 
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, 
                        "Permissions not granted by the user.", 
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        // If bottom sheet is expanded, collapse it instead of going back
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}