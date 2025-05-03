package com.example.mlkitapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.annotation.OptIn(markerClass = ExperimentalGetImage.class)
public class ObjectDetectionActivity extends AppCompatActivity implements ObjectAdapter.ObjectClickListener {
    private static final String TAG = "ObjectDetectionActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS;

    // Define required permissions based on Android version
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    private PreviewView previewView;
    private ObjectDetectionOverlay objectOverlay;
    private Camera camera;
    private ExecutorService cameraExecutor;
    private ObjectDetector objectDetector;
    private boolean isPaused = false;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private ImageCapture imageCapture;
    private Bitmap lastCapturedBitmap;

    // UI elements
    private RecyclerView rvObjects;
    private Button btnCaptureFreeze;
    private ToggleButton toggleTracking;
    private ImageButton btnSettings, btnFlipCamera, btnFlash, btnExport;
    private CardView settingsPanel, exportPanel;
    private Button btnCloseSettings, btnCancelExport, btnConfirmExport;
    private RadioGroup radioDetectionMode, radioExportFormat;
    private RadioButton radioMultiple, radioSingle;
    private SeekBar seekbarThreshold;
    private TextView tvThreshold;
    private Spinner spinnerModelSelection;
    private CheckBox checkboxAutoSave;

    private ObjectAdapter objectAdapter;
    private float confidenceThreshold = 0.5f;
    private boolean isMultipleDetection = true;
    private boolean isTrackingEnabled = false;
    private boolean flashEnabled = false;
    private boolean autoSaveEnabled = false;
    private String selectedModelName = "Base Object Detector";
    private List<DetectedObject> lastDetectedObjects = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);

        // Initialize UI elements
        previewView = findViewById(R.id.preview_view);
        objectOverlay = findViewById(R.id.object_overlay);
        rvObjects = findViewById(R.id.rv_objects);
        btnCaptureFreeze = findViewById(R.id.btn_capture_freeze);
        toggleTracking = findViewById(R.id.toggle_tracking);
        btnSettings = findViewById(R.id.btn_settings);
        btnFlipCamera = findViewById(R.id.btn_flip_camera);
        btnFlash = findViewById(R.id.btn_flash);
        btnExport = findViewById(R.id.btn_export);
        settingsPanel = findViewById(R.id.settings_panel);
        exportPanel = findViewById(R.id.export_panel);
        btnCloseSettings = findViewById(R.id.btn_close_settings);
        btnCancelExport = findViewById(R.id.btn_cancel_export);
        btnConfirmExport = findViewById(R.id.btn_confirm_export);
        radioDetectionMode = findViewById(R.id.radio_detection_mode);
        radioExportFormat = findViewById(R.id.radio_export_format);
        radioMultiple = findViewById(R.id.radio_multiple);
        radioSingle = findViewById(R.id.radio_single);
        seekbarThreshold = findViewById(R.id.seekbar_threshold);
        tvThreshold = findViewById(R.id.tv_threshold);
        spinnerModelSelection = findViewById(R.id.spinner_model_selection);
        checkboxAutoSave = findViewById(R.id.checkbox_auto_save);

        // Setup RecyclerView
        objectAdapter = new ObjectAdapter();
        objectAdapter.setObjectClickListener(this);
        rvObjects.setLayoutManager(new LinearLayoutManager(this));
        rvObjects.setAdapter(objectAdapter);

        // Set up spinner with model options
        ArrayAdapter<CharSequence> modelAdapter = ArrayAdapter.createFromResource(this,
                R.array.object_detection_models, android.R.layout.simple_spinner_item);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelection.setAdapter(modelAdapter);

        // Set up ML Kit Object Detector with default options
        setupObjectDetector();

        setupClickListeners();

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        // Set up button listeners
        btnCaptureFreeze.setOnClickListener(v -> {
            if (isPaused) {
                // Resume analysis
                isPaused = false;
                objectOverlay.resumeAnalysis();
                btnCaptureFreeze.setText(R.string.capture);
            } else {
                // Pause analysis
                isPaused = true;
                objectOverlay.pauseAnalysis();
                btnCaptureFreeze.setText(R.string.resume);

                // If auto-save is enabled, save the current detection
                if (autoSaveEnabled && !lastDetectedObjects.isEmpty()) {
                    captureAndSaveImage();
                }
            }
        });

        toggleTracking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isTrackingEnabled = isChecked;
            objectOverlay.setTrackingMode(isTrackingEnabled);

            // Recreate the object detector with new options
            setupObjectDetector();
        });

        btnSettings.setOnClickListener(v -> {
            settingsPanel.setVisibility(
                    settingsPanel.getVisibility() == View.VISIBLE ?
                            View.GONE : View.VISIBLE);
            if (exportPanel.getVisibility() == View.VISIBLE) {
                exportPanel.setVisibility(View.GONE);
            }
        });

        btnExport.setOnClickListener(v -> {
            exportPanel.setVisibility(
                    exportPanel.getVisibility() == View.VISIBLE ?
                            View.GONE : View.VISIBLE);
            if (settingsPanel.getVisibility() == View.VISIBLE) {
                settingsPanel.setVisibility(View.GONE);
            }
        });

        btnCloseSettings.setOnClickListener(v -> {
            settingsPanel.setVisibility(View.GONE);
            applySettings();
        });

        btnCancelExport.setOnClickListener(v -> {
            exportPanel.setVisibility(View.GONE);
        });

        btnConfirmExport.setOnClickListener(v -> {
            exportPanel.setVisibility(View.GONE);
            exportDetectionResults();
        });

        btnFlipCamera.setOnClickListener(v -> {
            flipCamera();
        });

        btnFlash.setOnClickListener(v -> {
            toggleFlash();
        });

        radioDetectionMode.setOnCheckedChangeListener((group, checkedId) -> {
            isMultipleDetection = checkedId == R.id.radio_multiple;
        });

        checkboxAutoSave.setOnCheckedChangeListener((buttonView, isChecked) -> {
            autoSaveEnabled = isChecked;

            // Check storage permission if auto-save is enabled
            if (isChecked && !hasStoragePermission()) {
                checkboxAutoSave.setChecked(false);
                requestStoragePermission();
            }
        });

        spinnerModelSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedModelName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        seekbarThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                confidenceThreshold = progress / 100f;
                tvThreshold.setText(getString(R.string.confidence_threshold_format, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupObjectDetector() {
        if (objectDetector != null) {
            objectDetector.close();
        }

        // Based on the selected model, create the appropriate detector
        switch (selectedModelName) {
            case "Accurate Object Detector":
                ObjectDetectorOptions.Builder builder = new ObjectDetectorOptions.Builder()
                        .setDetectorMode(isTrackingEnabled ?
                                ObjectDetectorOptions.STREAM_MODE :
                                ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableClassification();

                if (isMultipleDetection) {
                    builder.enableMultipleObjects();
                }

                objectDetector = ObjectDetection.getClient(builder.build());
                break;

            case "Custom Model":
                // Example for custom model - would need adjustment for actual custom model file
                try {
                    LocalModel localModel = new LocalModel.Builder()
                            .setAssetFilePath("custom_object_model.tflite")
                            .build();

                    CustomObjectDetectorOptions.Builder customBuilder =
                            new CustomObjectDetectorOptions.Builder(localModel)
                                    .setDetectorMode(isTrackingEnabled ?
                                            CustomObjectDetectorOptions.STREAM_MODE :
                                            CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                                    .enableClassification()
                                    .setMaxPerObjectLabelCount(3);

                    if (isMultipleDetection) {
                        customBuilder.enableMultipleObjects();
                    }

                    objectDetector = ObjectDetection.getClient(customBuilder.build());
                } catch (Exception e) {
                    Log.e(TAG, "Error loading custom model: " + e.getMessage());
                    Toast.makeText(this, "Error loading custom model. Using default.",
                            Toast.LENGTH_SHORT).show();
                    // Fallback to default model
                    setupDefaultObjectDetector();
                }
                break;

            case "Base Model (Quantized)":
                // For quantized model would typically set a different local model
                // Here we just use the default settings for example purposes
                setupDefaultObjectDetector();
                break;

            case "Base Object Detector":
            default:
                setupDefaultObjectDetector();
                break;
        }
    }

    private void setupDefaultObjectDetector() {
        try {
            // Use a more optimized configuration for object detection
            ObjectDetectorOptions.Builder builder = new ObjectDetectorOptions.Builder()
                    .setDetectorMode(isTrackingEnabled ?
                            ObjectDetectorOptions.STREAM_MODE :
                            ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                    .enableClassification();

            if (isMultipleDetection) {
                builder.enableMultipleObjects();
            }

            objectDetector = ObjectDetection.getClient(builder.build());
            Log.d(TAG, "Object detector initialized with " +
                    (isTrackingEnabled ? "STREAM_MODE" : "SINGLE_IMAGE_MODE") +
                    ", multipleObjects: " + isMultipleDetection);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up object detector: " + e.getMessage());
            Toast.makeText(this, "Error initializing detector: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void applySettings() {
        // Update overlay settings
        objectOverlay.setConfidenceThreshold(confidenceThreshold);
        objectOverlay.setSingleObjectMode(!isMultipleDetection);

        // Recreate the object detector with updated settings
        setupObjectDetector();
    }

    private void flipCamera() {
        lensFacing = (lensFacing == CameraSelector.LENS_FACING_BACK) ?
                CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        startCamera();
    }

    private void toggleFlash() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            flashEnabled = !flashEnabled;
            camera.getCameraControl().enableTorch(flashEnabled);

            // Update button appearance
            btnFlash.setImageResource(flashEnabled ?
                    android.R.drawable.ic_menu_gallery : // Placeholder - replace with actual flash icons
                    android.R.drawable.ic_menu_gallery);
        } else {
            Toast.makeText(this, "Flash not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCamera() {
        if (!allPermissionsGranted()) {
            requestPermissions();
            return;
        }

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
                        .setTargetResolution(new Size(720, 1280))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageForObjectDetection);

                // Image capture use case for taking photos
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                // Select camera based on selected lens facing
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build();

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis, imageCapture);

                // Set initial flash state
                if (camera.getCameraInfo().hasFlashUnit()) {
                    camera.getCameraControl().enableTorch(flashEnabled);
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @ExperimentalGetImage
    private void processImageForObjectDetection(ImageProxy imageProxy) {
        try {
            if (isPaused || imageProxy.getImage() == null) {
                imageProxy.close();
                return;
            }

            InputImage image = InputImage.fromMediaImage(
                    imageProxy.getImage(),
                    imageProxy.getImageInfo().getRotationDegrees()
            );

            Log.d(TAG, "Processing image: " + imageProxy.getWidth() + "x" + imageProxy.getHeight() +
                    ", rotation: " + imageProxy.getImageInfo().getRotationDegrees());

            objectDetector.process(image)
                    .addOnSuccessListener(detectedObjects -> {
                        try {
                            if (isPaused) {
                                imageProxy.close();
                                return;
                            }

                            // Log raw detection results
                            Log.d(TAG, "Object detection completed: " + detectedObjects.size() + " objects found");

                            // Filter based on confidence threshold
                            List<DetectedObject> filteredObjects = new ArrayList<>();
                            for (DetectedObject object : detectedObjects) {
                                if (object.getLabels().isEmpty()) {
                                    Log.d(TAG, "Detected object with no labels (tracking ID: " +
                                            object.getTrackingId() + ")");
                                    continue;
                                }

                                float confidence = object.getLabels().get(0).getConfidence();
                                String label = object.getLabels().get(0).getText();
                                Log.d(TAG, "Object detected: " + label + " with confidence " +
                                        confidence + ", threshold: " + confidenceThreshold);

                                if (confidence >= confidenceThreshold) {
                                    filteredObjects.add(object);
                                    Rect box = object.getBoundingBox();
                                    Log.d(TAG, "Added to filtered objects: " + label +
                                            " at " + box.left + "," + box.top +
                                            " size " + box.width() + "x" + box.height());
                                }
                            }

                            // Update UI with detection results
                            lastDetectedObjects = filteredObjects;
                            objectAdapter.updateObjects(filteredObjects);
                            objectOverlay.updateObjects(
                                    filteredObjects,
                                    imageProxy.getWidth(),
                                    imageProxy.getHeight()
                            );

                            // If no objects detected with sufficient confidence, try adjusting the detector
                            if (filteredObjects.isEmpty() && !detectedObjects.isEmpty() && confidenceThreshold > 0.3f) {
                                Log.d(TAG, "Objects detected but filtered out due to confidence threshold");
                                // Consider showing a suggestion to lower threshold
                                runOnUiThread(() -> {
                                    Toast.makeText(ObjectDetectionActivity.this,
                                            "Try lowering the confidence threshold for more detections",
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        } finally {
                            imageProxy.close();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Object detection failed: " + e.getMessage(), e);
                        Toast.makeText(ObjectDetectionActivity.this,
                                "Detection failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        imageProxy.close();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error processing image for object detection: " + e.getMessage(), e);
            Toast.makeText(this,
                    "Error processing image: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            imageProxy.close();
        }
    }

    @Override
    public void onObjectClick(DetectedObject object) {
        showObjectDetailDialog(object);
    }

    private void showObjectDetailDialog(DetectedObject object) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.object_detail_dialog);

        TextView titleText = dialog.findViewById(R.id.tv_object_title);
        ImageView thumbnailView = dialog.findViewById(R.id.iv_object_thumbnail);
        TextView classText = dialog.findViewById(R.id.tv_object_class);
        ProgressBar confidenceBar = dialog.findViewById(R.id.progress_confidence);
        TextView confidenceText = dialog.findViewById(R.id.tv_confidence);
        TextView boundingBoxText = dialog.findViewById(R.id.tv_bounding_box);
        Button shareButton = dialog.findViewById(R.id.btn_share_object);
        Button closeButton = dialog.findViewById(R.id.btn_close_detail);

        // Set values from the detected object
        String label = "Unknown";
        int confidence = 0;
        if (!object.getLabels().isEmpty()) {
            label = object.getLabels().get(0).getText();
            confidence = (int)(object.getLabels().get(0).getConfidence() * 100);
        }

        titleText.setText(label);
        classText.setText(label);
        confidenceBar.setProgress(confidence);
        confidenceText.setText(getString(R.string.confidence_value_format, confidence));

        Rect boundingBox = object.getBoundingBox();
        boundingBoxText.setText(getString(R.string.bounding_box_format,
                boundingBox.left, boundingBox.top,
                boundingBox.width(), boundingBox.height()));

        // If we have a captured image, try to get the object thumbnail
        if (lastCapturedBitmap != null) {
            try {
                Bitmap thumbnail = Bitmap.createBitmap(
                        lastCapturedBitmap,
                        boundingBox.left,
                        boundingBox.top,
                        boundingBox.width(),
                        boundingBox.height());
                thumbnailView.setImageBitmap(thumbnail);
            } catch (Exception e) {
                Log.e(TAG, "Error creating thumbnail", e);
                // If we can't create the thumbnail, hide the ImageView
                thumbnailView.setVisibility(View.GONE);
            }
        } else {
            thumbnailView.setVisibility(View.GONE);
        }

        shareButton.setOnClickListener(v -> {
            shareObjectInfo(object);
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void shareObjectInfo(DetectedObject object) {
        String label = "Unknown";
        int confidence = 0;
        if (!object.getLabels().isEmpty()) {
            label = object.getLabels().get(0).getText();
            confidence = (int)(object.getLabels().get(0).getConfidence() * 100);
        }

        Rect boundingBox = object.getBoundingBox();
        String objectInfo = "Detected Object: " + label + "\n" +
                "Confidence: " + confidence + "%\n" +
                "Location: X=" + boundingBox.left + ", Y=" + boundingBox.top + "\n" +
                "Size: " + boundingBox.width() + "x" + boundingBox.height() + " pixels";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Object Detection Result");
        shareIntent.putExtra(Intent.EXTRA_TEXT, objectInfo);
        startActivity(Intent.createChooser(shareIntent, "Share Object Info"));
    }

    private void captureAndSaveImage() {
        // Check storage permission first
        if (!hasStoragePermission()) {
            requestStoragePermission();
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "OBJDETECT_" + timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        // Process the image to get bitmap
                        InputImage inputImage = InputImage.fromMediaImage(
                                image.getImage(),
                                image.getImageInfo().getRotationDegrees());

                        // Store bitmap for later use with thumbnails
                        lastCapturedBitmap = inputImage.getBitmapInternal();

                        Toast.makeText(ObjectDetectionActivity.this,
                                "Image captured and saved",
                                Toast.LENGTH_SHORT).show();

                        super.onCaptureSuccess(image);
                        image.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage());
                        Toast.makeText(ObjectDetectionActivity.this,
                                "Failed to capture image: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void exportDetectionResults() {
        // Check storage permission first
        if (!hasStoragePermission()) {
            requestStoragePermission();
            return;
        }

        if (lastDetectedObjects.isEmpty()) {
            Toast.makeText(this, "No objects detected to export", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedExportFormat = radioExportFormat.getCheckedRadioButtonId();

        if (selectedExportFormat == R.id.radio_export_csv) {
            exportAsCSV();
        } else if (selectedExportFormat == R.id.radio_export_json) {
            exportAsJSON();
        } else {
            // Default is image with annotations
            exportAsAnnotatedImage();
        }
    }

    private void exportAsCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("Label,Confidence,X,Y,Width,Height\n");

        for (DetectedObject object : lastDetectedObjects) {
            String label = "Unknown";
            float confidence = 0;
            if (!object.getLabels().isEmpty()) {
                label = object.getLabels().get(0).getText();
                confidence = object.getLabels().get(0).getConfidence();
            }

            Rect box = object.getBoundingBox();
            csv.append(String.format(Locale.US,
                    "%s,%.2f,%d,%d,%d,%d\n",
                    label, confidence,
                    box.left, box.top, box.width(), box.height()));
        }

        saveTextFile(csv.toString(), "csv");
    }

    private void exportAsJSON() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (DetectedObject object : lastDetectedObjects) {
                JSONObject jsonObject = new JSONObject();

                String label = "Unknown";
                float confidence = 0;
                if (!object.getLabels().isEmpty()) {
                    label = object.getLabels().get(0).getText();
                    confidence = object.getLabels().get(0).getConfidence();
                }

                Rect box = object.getBoundingBox();

                jsonObject.put("label", label);
                jsonObject.put("confidence", confidence);

                JSONObject boundingBox = new JSONObject();
                boundingBox.put("x", box.left);
                boundingBox.put("y", box.top);
                boundingBox.put("width", box.width());
                boundingBox.put("height", box.height());

                jsonObject.put("boundingBox", boundingBox);
                jsonArray.put(jsonObject);
            }

            saveTextFile(jsonArray.toString(2), "json");

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON", e);
            Toast.makeText(this, "Error exporting JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportAsAnnotatedImage() {
        // This would need bitmap processing logic to draw annotations
        // For now just show a message
        Toast.makeText(this, "Image export with annotations coming soon", Toast.LENGTH_SHORT).show();
    }

    private void saveTextFile(String content, String extension) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "object_detection_" + timeStamp + "." + extension;

            File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(storageDir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();

            Toast.makeText(this,
                    "Saved to " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e(TAG, "Error saving file", e);
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
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

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ does not require WRITE_EXTERNAL_STORAGE for app-specific files
            return true;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("Camera permission is required for object detection. " +
                        "Storage permission is needed for saving detected objects.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Open app settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Close activity if permissions denied
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // Check if camera permission was granted
            boolean cameraPermissionGranted = false;
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA) &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionGranted = true;
                }
            }

            if (cameraPermissionGranted) {
                // Start camera if camera permission granted
                startCamera();
            } else {
                // Show explanation and settings option if camera permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this,
                            "Camera permission is required for object detection",
                            Toast.LENGTH_LONG).show();
                    // Show dialog explaining permissions
                    showPermissionExplanationDialog();
                } else {
                    // User checked "Don't ask again" - direct to settings
                    showPermissionExplanationDialog();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check permissions again when resuming activity
        if (allPermissionsGranted()) {
            if (camera == null) {
                startCamera();
            }
        } else {
            // Don't request automatically - user might have just denied from settings
            if (hasCameraPermission()) {
                startCamera();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (objectDetector != null) {
            objectDetector.close();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}