package com.example.mlkitapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextRecognitionActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "TextRecognitionAct";
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int MAX_PREVIEW_WIDTH = 1280;
    private static final int MAX_PREVIEW_HEIGHT = 720;

    private static final int OPTIMAL_IMAGE_WIDTH = 2048; // Increased from 1024
    private static final int OPTIMAL_IMAGE_HEIGHT = 1536; // Increased from 768

    private static final float MIN_LUX_FOR_NO_FLASH = 50.0f;
    private boolean shouldUseFlash = false;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private final AtomicBoolean isProcessingImage = new AtomicBoolean(false);

    private final Executor imageProcessingExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(android.os.Looper.getMainLooper());

    private WeakReference<SurfaceView> viewFinderRef;
    private ImageButton captureButton;
    private LinearLayout copyButton;
    private LinearLayout shareButton;
    private LinearLayout editButton;
    private TextView textResult;
    private BoundingBoxView overlay;
    private View progressOverlay;
    private ImageButton flashButton;

    private TextRecognizer textRecognizer;
    private String recognizedText = "";

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private volatile boolean isCapturing = false;
    private volatile boolean isCameraReady = false;
    private long lastToastTime = 0;
    private long lastCaptureTime = 0;
    private static final long MIN_CAPTURE_INTERVAL = 1500;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private int cameraRetryCount = 0;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private final Semaphore cameraOpenCloseLock = new Semaphore(1);

    private static final int CAMERA_STATE_CLOSED = 0;
    private static final int CAMERA_STATE_OPENING = 1;
    private static final int CAMERA_STATE_OPENED = 2;
    private static final int CAMERA_STATE_CLOSING = 3;
    private static final int CAMERA_STATE_ERROR = 4;

    private volatile int cameraState = CAMERA_STATE_CLOSED;

    private SensorManager sensorManager;
    private LightSensorListener lightSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        initializeViews();
        initializeSensors();
        initializeTextRecognizer();
        setupClickListeners();

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, CAMERA_PERMISSION_REQUEST);
        }
    }

    private void initializeViews() {
        SurfaceView viewFinder = findViewById(R.id.viewFinder);
        viewFinderRef = new WeakReference<>(viewFinder);
        captureButton = findViewById(R.id.btn_capture);
        copyButton = findViewById(R.id.btn_copy);
        shareButton = findViewById(R.id.btn_share);
        editButton = findViewById(R.id.btn_edit);
        textResult = findViewById(R.id.text_result);
        overlay = findViewById(R.id.overlay);
        progressOverlay = findViewById(R.id.progress_overlay);
        flashButton = findViewById(R.id.btn_flash);

        copyButton.setEnabled(false);
        shareButton.setEnabled(false);
        editButton.setEnabled(false);

        if (viewFinder != null) {
            SurfaceHolder holder = viewFinder.getHolder();
            if (holder != null) {
                holder.setKeepScreenOn(true);
                holder.addCallback(this);
            } else {
                Log.e(TAG, "SurfaceHolder is null");
                Toast.makeText(this, "Camera initialization error", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensorListener = new LightSensorListener();
    }

    private void initializeTextRecognizer() {
        try {
            textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            imageProcessingExecutor.execute(() -> {
                try {
                    Bitmap dummyBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    InputImage dummyInput = InputImage.fromBitmap(dummyBitmap, 0);
                    textRecognizer.process(dummyInput).addOnCompleteListener(task -> {
                        dummyBitmap.recycle();
                        Log.d(TAG, "ML Kit warm-up complete");
                    });
                } catch (Exception e) {
                    Log.w(TAG, "ML Kit warm-up failed: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize text recognizer: " + e.getMessage());
            Toast.makeText(this, "Error initializing text recognizer. Please restart the app.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupClickListeners() {
        captureButton.setOnClickListener(view -> {
            long currentTime = SystemClock.elapsedRealtime();
            if (currentTime - lastCaptureTime < MIN_CAPTURE_INTERVAL) {
                return;
            }

            if (!isCapturing && isCameraReady) {
                lastCaptureTime = currentTime;
                capturePhoto();
            } else if (isCapturing) {
                if (System.currentTimeMillis() - lastToastTime > 1000) {
                    lastToastTime = System.currentTimeMillis();
                    Toast.makeText(TextRecognitionActivity.this,
                            "Processing previous capture...", Toast.LENGTH_SHORT).show();
                }
            } else if (System.currentTimeMillis() - lastToastTime > 1000) {
                lastToastTime = System.currentTimeMillis();
                Toast.makeText(TextRecognitionActivity.this,
                        "Camera not ready yet", Toast.LENGTH_SHORT).show();
            }
        });

        copyButton.setOnClickListener(v -> copyToClipboard(recognizedText));
        shareButton.setOnClickListener(v -> shareText(recognizedText));
        editButton.setOnClickListener(v -> showEditTextDialog(recognizedText));

        flashButton.setOnClickListener(v -> toggleFlash());
    }

    private void toggleFlash() {
        if (captureRequestBuilder == null || cameraCaptureSession == null) {
            return;
        }

        shouldUseFlash = !shouldUseFlash;
        updateFlashMode();

        flashButton.setImageResource(shouldUseFlash ?
                android.R.drawable.ic_menu_gallery : android.R.drawable.ic_menu_gallery);

        if (System.currentTimeMillis() - lastToastTime > 800) {
            Toast.makeText(this, shouldUseFlash ? "Flash turned on" : "Flash turned off",
                    Toast.LENGTH_SHORT).show();
            lastToastTime = System.currentTimeMillis();
        }
    }

    private void showEditTextDialog(String textToEdit) {
        if (textToEdit.isEmpty()) {
            Toast.makeText(this, "No text to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_text, null);
        builder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.edit_text);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        Button saveButton = dialogView.findViewById(R.id.btn_save);

        editText.setText(textToEdit);

        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            recognizedText = editText.getText().toString();
            textResult.setText(recognizedText);
            dialog.dismiss();
            Toast.makeText(this, "Text updated", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private class LightSensorListener implements SensorEventListener {
        private float lastLightLevel = 0f;
        private long lastReadingTime = 0;
        private static final long MIN_READING_INTERVAL = 1000;

        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTime = SystemClock.elapsedRealtime();

            if (currentTime - lastReadingTime > MIN_READING_INTERVAL) {
                lastReadingTime = currentTime;
                lastLightLevel = event.values[0];

                if (isCameraReady && cameraCaptureSession != null) {
                    boolean newShouldUseFlash = lastLightLevel < MIN_LUX_FOR_NO_FLASH;
                    if (newShouldUseFlash != shouldUseFlash) {
                        shouldUseFlash = newShouldUseFlash;
                        updateFlashMode();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public float getLastLightLevel() {
            return lastLightLevel;
        }
    }

    private boolean isLowLightCondition() {
        if (lightSensorListener != null) {
            return lightSensorListener.getLastLightLevel() < MIN_LUX_FOR_NO_FLASH;
        }
        return false;
    }

    private boolean allPermissionsGranted() {
        for (String permission : CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (allPermissionsGranted()) {
                SurfaceView viewFinder = viewFinderRef.get();
                if (viewFinder != null && viewFinder.getHolder() != null) {
                    surfaceCreated(viewFinder.getHolder());
                }
            } else {
                Toast.makeText(this, "Camera permission is required for this feature",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if (cameraState == CAMERA_STATE_CLOSED) {
            openCamera();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (captureRequestBuilder != null && cameraCaptureSession != null) {
            try {
                cameraCaptureSession.stopRepeating();
            } catch (CameraAccessException | IllegalStateException e) {
                Log.e(TAG, "Failed to stop repeating: " + e.getMessage());
            }
        }

        if (cameraState == CAMERA_STATE_OPENED) {
            createCameraPreview();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        closeCamera();
    }

    private void openCamera() {
        if (cameraState != CAMERA_STATE_CLOSED) {
            Log.w(TAG, "Camera already in state: " + cameraState);
            return;
        }

        cameraState = CAMERA_STATE_OPENING;

        if (backgroundHandler == null) {
            startBackgroundThread();
        }

        cameraRetryCount = 0;
        tryOpenCamera();
    }

    private void tryOpenCamera() {
        if (cameraRetryCount >= MAX_RETRY_ATTEMPTS) {
            Log.e(TAG, "Failed to open camera after " + MAX_RETRY_ATTEMPTS + " attempts");
            runOnUiThread(() -> {
                Toast.makeText(this, "Failed to open camera. Please restart the app.",
                        Toast.LENGTH_LONG).show();
                cameraState = CAMERA_STATE_ERROR;
            });
            return;
        }

        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening");
            }

            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (manager == null) {
                throw new RuntimeException("Camera manager is null");
            }

            String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length == 0) {
                throw new RuntimeException("No cameras available on this device");
            }

            cameraId = selectOptimalCamera(manager, cameraIds);

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (map == null) {
                throw new RuntimeException("Cannot get available preview sizes");
            }

            Size[] availableSizes = map.getOutputSizes(SurfaceHolder.class);

            if (availableSizes == null || availableSizes.length == 0) {
                throw new RuntimeException("No available preview sizes");
            }

            final SurfaceView viewFinder = viewFinderRef.get();
            if (viewFinder == null) {
                throw new RuntimeException("SurfaceView has been garbage collected");
            }

            imageDimension = chooseOptimalSize(availableSizes,
                    viewFinder.getWidth(), viewFinder.getHeight(),
                    MAX_PREVIEW_WIDTH, MAX_PREVIEW_HEIGHT);

            Log.d(TAG, "Selected camera preview size: " + imageDimension.getWidth() + "x" + imageDimension.getHeight());

            setupImageReader();

            SurfaceHolder holder = viewFinder.getHolder();
            if (holder == null) {
                throw new RuntimeException("Surface holder is null");
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Camera permission not granted");
                cameraState = CAMERA_STATE_CLOSED;
                cameraOpenCloseLock.release();
                return;
            }

            isCameraReady = false;
            manager.openCamera(cameraId, stateCallback, backgroundHandler);

        } catch (CameraAccessException e) {
            cameraOpenCloseLock.release();
            Log.e(TAG, "Failed to access camera: " + e.getMessage());
            cameraRetryCount++;
            retryOpeningCamera();
        } catch (InterruptedException e) {
            cameraOpenCloseLock.release();
            Log.e(TAG, "Interrupted while locking: " + e.getMessage());
            Thread.currentThread().interrupt();
            cameraState = CAMERA_STATE_ERROR;
        } catch (Exception e) {
            cameraOpenCloseLock.release();
            Log.e(TAG, "Unexpected error opening camera: " + e.getMessage(), e);
            cameraRetryCount++;
            retryOpeningCamera();
        }
    }

    private String selectOptimalCamera(CameraManager manager, String[] cameraIds) throws CameraAccessException {
        String selectedCameraId = cameraIds[0];

        for (String cameraId : cameraIds) {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            Integer afAvailable = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES).length > 0 ? 1 : 0;

            if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK && afAvailable == 1) {
                selectedCameraId = cameraId;
                break;
            }
        }

        if (selectedCameraId == null) {
            selectedCameraId = cameraIds[0];
            Log.w(TAG, "No optimal camera found, using camera: " + selectedCameraId);
        }

        return selectedCameraId;
    }

    private void setupImageReader() {
        if (imageDimension == null) {
            Log.e(TAG, "Cannot setup ImageReader, image dimension is null");
            return;
        }

        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }

        imageReader = ImageReader.newInstance(
                OPTIMAL_IMAGE_WIDTH, OPTIMAL_IMAGE_HEIGHT,
                android.graphics.ImageFormat.JPEG, 2);

        imageReader.setOnImageAvailableListener(reader -> {
            try {
                Image image = reader.acquireLatestImage();
                if (image != null) {
                    if (!isProcessingImage.get()) {
                        isProcessingImage.set(true);
                        processImage(image);
                    } else {
                        image.close();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing image: " + e.getMessage());
                isCapturing = false;
                isProcessingImage.set(false);
                hideProgressOverlay();
            }
        }, backgroundHandler);
    }

    private void retryOpeningCamera() {
        if (backgroundHandler != null) {
            backgroundHandler.postDelayed(this::tryOpenCamera, 1000);
        } else {
            cameraState = CAMERA_STATE_ERROR;
            runOnUiThread(() -> Toast.makeText(this,
                    "Camera initialization failed", Toast.LENGTH_SHORT).show());
        }
    }

    private Size chooseOptimalSize(Size[] choices, int viewWidth, int viewHeight,
                                   int maxWidth, int maxHeight) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();

        float targetRatio = (float) viewWidth / viewHeight;
        float tolerance = 0.1f;

        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight) {
                float aspectRatio = (float) option.getWidth() / option.getHeight();
                if (Math.abs(aspectRatio - targetRatio) < tolerance) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        if (!bigEnough.isEmpty()) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        }

        if (!notBigEnough.isEmpty()) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        }

        Log.e(TAG, "Couldn't find suitable preview size, using default");
        return choices[0];
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            cameraDevice = camera;
            cameraState = CAMERA_STATE_OPENED;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            camera.close();
            cameraDevice = null;
            isCameraReady = false;
            cameraState = CAMERA_STATE_CLOSED;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraOpenCloseLock.release();
            camera.close();
            cameraDevice = null;
            isCameraReady = false;
            cameraState = CAMERA_STATE_ERROR;

            if (cameraRetryCount < MAX_RETRY_ATTEMPTS) {
                cameraRetryCount++;
                Log.e(TAG, "Camera error: " + error + ", retrying (" + cameraRetryCount + "/" + MAX_RETRY_ATTEMPTS + ")");
                cameraState = CAMERA_STATE_CLOSED;
                retryOpeningCamera();
            } else {
                final String errorMessage;
                switch (error) {
                    case ERROR_CAMERA_DEVICE: errorMessage = "Fatal camera device error"; break;
                    case ERROR_CAMERA_DISABLED: errorMessage = "Camera disabled by policy"; break;
                    case ERROR_CAMERA_IN_USE: errorMessage = "Camera already in use"; break;
                    case ERROR_MAX_CAMERAS_IN_USE: errorMessage = "Max cameras in use"; break;
                    default: errorMessage = "Camera error: " + error;
                }

                runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                        errorMessage, Toast.LENGTH_SHORT).show());
            }
        }
    };

    private void createCameraPreview() {
        final SurfaceView viewFinder = viewFinderRef.get();
        if (viewFinder == null) {
            Log.e(TAG, "SurfaceView has been garbage collected");
            return;
        }

        SurfaceHolder surfaceHolder = viewFinder.getHolder();
        if (surfaceHolder == null || surfaceHolder.getSurface() == null || !surfaceHolder.getSurface().isValid()) {
            Log.e(TAG, "Surface holder is null or invalid");
            return;
        }

        try {
            if (cameraDevice == null) {
                Log.e(TAG, "Cannot create preview, camera device is null");
                return;
            }

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surfaceHolder.getSurface());

            configureOptimalCameraSettings(captureRequestBuilder);

            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(surfaceHolder.getSurface());
            if (imageReader != null) {
                outputSurfaces.add(imageReader.getSurface());
            }

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    try {
                        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
                        isCameraReady = true;
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "Failed to start camera preview: " + e.getMessage());
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Camera already closed: " + e.getMessage());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "Camera capture session configuration failed");
                    runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                            "Failed to configure camera", Toast.LENGTH_SHORT).show());
                    isCameraReady = false;
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera access exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error creating camera preview: " + e.getMessage());
        }
    }

    private void configureOptimalCameraSettings(CaptureRequest.Builder requestBuilder) {
        requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
        requestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
        requestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
                CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON);
        requestBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
                CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON);
        requestBuilder.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_HIGH_QUALITY);
        requestBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE,
                CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY);

        try {
            MeteringRectangle[] meteringRectangles = new MeteringRectangle[] {
                new MeteringRectangle(0, 0, 1000, 1000, MeteringRectangle.METERING_WEIGHT_MAX)
            };

            requestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, meteringRectangles);
            requestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, meteringRectangles);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set focus regions: " + e.getMessage());
        }

        updateFlashMode();
    }

    private void updateFlashMode() {
        if (captureRequestBuilder == null) {
            return;
        }

        try {
            if (shouldUseFlash) {
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            } else {
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            }

            if (cameraCaptureSession != null && isCameraReady) {
                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
            }
        } catch (CameraAccessException | IllegalStateException e) {
            Log.e(TAG, "Failed to update flash mode: " + e.getMessage());
        }
    }

    private void capturePhoto() {
        if (cameraDevice == null || !isCameraReady || isCapturing) {
            Log.e(TAG, "Cannot capture, camera not ready or already capturing");
            return;
        }

        isCapturing = true;
        showProgressOverlay();

        try {
            runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                    "Capturing...", Toast.LENGTH_SHORT).show());

            final CaptureRequest.Builder captureBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            if (imageReader == null || imageReader.getSurface() == null) {
                setupImageReader();
                if (imageReader == null) {
                    Log.e(TAG, "Failed to setup image reader");
                    isCapturing = false;
                    hideProgressOverlay();
                    return;
                }
            }

            captureBuilder.addTarget(imageReader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
            captureBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);
            captureBuilder.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_HIGH_QUALITY);
            captureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            captureBuilder.set(CaptureRequest.FLASH_MODE,
                    shouldUseFlash ? CaptureRequest.FLASH_MODE_TORCH : CaptureRequest.FLASH_MODE_OFF);

            cameraCaptureSession.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                private boolean focusComplete = false;

                private void process(CaptureResult result) {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);

                    if (!focusComplete &&
                        (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                         afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED ||
                         afState == null)) {

                        focusComplete = true;
                        Log.d(TAG, "Focus completed, taking high-quality image");

                        try {
                            CaptureRequest.Builder captureStillBuilder =
                                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                            captureStillBuilder.addTarget(imageReader.getSurface());
                            captureStillBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                            captureStillBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);
                            captureStillBuilder.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_HIGH_QUALITY);
                            captureStillBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY);

                            int stillRotation = getWindowManager().getDefaultDisplay().getRotation();
                            captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(stillRotation));

                            captureStillBuilder.set(CaptureRequest.FLASH_MODE,
                                shouldUseFlash ? CaptureRequest.FLASH_MODE_TORCH : CaptureRequest.FLASH_MODE_OFF);

                            cameraCaptureSession.capture(captureStillBuilder.build(), null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "Failed to take still picture after focus: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull CaptureResult partialResult) {
                    process(partialResult);
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    process(result);
                    Log.d(TAG, "Image focus phase completed");
                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session,
                                            @NonNull CaptureRequest request,
                                            @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    isCapturing = false;
                    hideProgressOverlay();
                    Log.e(TAG, "Image capture failed");
                    runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                            "Failed to capture image", Toast.LENGTH_SHORT).show());
                }
            }, backgroundHandler);

        } catch (CameraAccessException e) {
            isCapturing = false;
            hideProgressOverlay();
            Log.e(TAG, "Camera access exception during capture: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                    "Failed to capture image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            isCapturing = false;
            hideProgressOverlay();
            Log.e(TAG, "Exception during capture: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                    "Failed to capture image", Toast.LENGTH_SHORT).show());
        }
    }

    private void showProgressOverlay() {
        runOnUiThread(() -> progressOverlay.setVisibility(View.VISIBLE));
    }

    private void hideProgressOverlay() {
        runOnUiThread(() -> progressOverlay.setVisibility(View.GONE));
    }

    private void processImage(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inMutable = true;
            options.inSampleSize = 1;

            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            if (bitmap == null) {
                Log.e(TAG, "Failed to decode image");
                isCapturing = false;
                isProcessingImage.set(false);
                hideProgressOverlay();
                runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                        "Failed to process image", Toast.LENGTH_SHORT).show());
                return;
            }

            final Bitmap enhancedBitmap = enhanceBitmapForTextRecognition(bitmap);

            runOnUiThread(() -> Toast.makeText(TextRecognitionActivity.this,
                    "Processing text...", Toast.LENGTH_SHORT).show());

            final int rotation = getWindowManager().getDefaultDisplay().getRotation();
            final int rotationDegrees = ORIENTATIONS.get(rotation);

            imageProcessingExecutor.execute(() -> {
                try {
                    InputImage inputImage = InputImage.fromBitmap(enhancedBitmap, rotationDegrees);

                    final int bitmapWidth = enhancedBitmap.getWidth();
                    final int bitmapHeight = enhancedBitmap.getHeight();

                    mainHandler.post(() -> {
                        try {
                            overlay.updateScaleFactors(bitmapWidth, bitmapHeight);
                            overlay.setDeviceRotation(rotationDegrees);
                        } catch (Exception e) {
                            Log.e(TAG, "Error updating overlay: " + e.getMessage());
                        }
                    });

                    textRecognizer.process(inputImage)
                            .addOnSuccessListener(visionText -> {
                                mainHandler.post(() -> {
                                    try {
                                        isCapturing = false;
                                        hideProgressOverlay();
                                        recognizedText = visionText.getText();

                                        if (recognizedText.isEmpty()) {
                                            displayRecognizedTextWithHighVisibility("No text detected");
                                            overlay.clear();
                                            Toast.makeText(TextRecognitionActivity.this,
                                                    "No text detected", Toast.LENGTH_SHORT).show();

                                            copyButton.setEnabled(false);
                                            shareButton.setEnabled(false);
                                            editButton.setEnabled(false);
                                            return;
                                        }

                                        displayRecognizedTextWithHighVisibility(recognizedText);

                                        List<Rect> boundingBoxes = new ArrayList<>();

                                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                                            Rect blockFrame = block.getBoundingBox();
                                            if (blockFrame != null) {
                                                boundingBoxes.add(blockFrame);
                                            }
                                        }

                                        overlay.setBoundingBoxes(boundingBoxes);
                                        overlay.setLineWidth(5);

                                        copyButton.setEnabled(true);
                                        shareButton.setEnabled(true);
                                        editButton.setEnabled(true);

                                        Toast.makeText(TextRecognitionActivity.this,
                                                "Text recognized successfully", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error updating UI with text results: " + e.getMessage());
                                        hideProgressOverlay();
                                        Toast.makeText(TextRecognitionActivity.this,
                                                "Error displaying results", Toast.LENGTH_SHORT).show();
                                    } finally {
                                        isProcessingImage.set(false);
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                mainHandler.post(() -> {
                                    try {
                                        isCapturing = false;
                                        hideProgressOverlay();
                                        Log.e(TAG, "Text recognition failed: " + e.getMessage());
                                        textResult.setText("Text recognition failed");
                                        overlay.clear();
                                        Toast.makeText(TextRecognitionActivity.this,
                                                "Text recognition failed", Toast.LENGTH_SHORT).show();

                                        copyButton.setEnabled(false);
                                        shareButton.setEnabled(false);
                                        editButton.setEnabled(false);
                                    } catch (Exception ex) {
                                        Log.e(TAG, "Error updating UI after recognition failure: " + ex.getMessage());
                                    } finally {
                                        isProcessingImage.set(false);
                                    }
                                });
                            })
                            .addOnCompleteListener(task -> {
                                try {
                                    enhancedBitmap.recycle();
                                    bitmap.recycle();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error recycling bitmaps: " + e.getMessage());
                                }
                            });
                } catch (Exception e) {
                    Log.e(TAG, "Error in text recognition process: " + e.getMessage());
                    mainHandler.post(() -> {
                        isCapturing = false;
                        hideProgressOverlay();
                        Toast.makeText(TextRecognitionActivity.this,
                                "Text recognition error", Toast.LENGTH_SHORT).show();
                        isProcessingImage.set(false);
                    });

                    try {
                        enhancedBitmap.recycle();
                        bitmap.recycle();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error recycling bitmaps after exception: " + ex.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            isCapturing = false;
            hideProgressOverlay();
            Log.e(TAG, "Error processing image data: " + e.getMessage());
            runOnUiThread(() -> {
                Toast.makeText(TextRecognitionActivity.this,
                        "Failed to process image", Toast.LENGTH_SHORT).show();
                isProcessingImage.set(false);
            });
        }
    }

    private Bitmap enhanceBitmapForTextRecognition(Bitmap original) {
        if (original == null) return null;

        try {
            Bitmap enhanced = original.copy(Bitmap.Config.ARGB_8888, true);

            enhanced = denoiseImage(enhanced);
            enhanced = enhanceContrast(enhanced);
            enhanced = sharpenImage(enhanced);
            enhanced = correctPerspective(enhanced);

            return enhanced;
        } catch (Exception e) {
            Log.e(TAG, "Error enhancing bitmap: " + e.getMessage());
            return original;
        }
    }

    private Bitmap denoiseImage(Bitmap input) {
        try {
            int width = input.getWidth();
            int height = input.getHeight();

            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();

            paint.setMaskFilter(new BlurMaskFilter(1.0f, BlurMaskFilter.Blur.NORMAL));
            canvas.drawBitmap(input, 0, 0, paint);

            return output;
        } catch (Exception e) {
            Log.e(TAG, "Error applying noise reduction: " + e.getMessage());
            return input;
        }
    }

    private Bitmap enhanceContrast(Bitmap input) {
        try {
            Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();

            float contrastLevel = isLowLightCondition() ? 1.7f : 1.5f;
            int brightnessLevel = isLowLightCondition() ? 30 : 10;

            ColorMatrix cm = new ColorMatrix(new float[] {
                contrastLevel, 0, 0, 0, brightnessLevel,
                0, contrastLevel, 0, 0, brightnessLevel,
                0, 0, contrastLevel, 0, brightnessLevel,
                0, 0, 0, 1, 0
            });

            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(input, 0, 0, paint);

            return output;
        } catch (Exception e) {
            Log.e(TAG, "Error enhancing contrast: " + e.getMessage());
            return input;
        }
    }

    private Bitmap sharpenImage(Bitmap input) {
        try {
            Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());

            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();

            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(1.3f);

            float[] sharpen = {
                1.5f, -0.2f, -0.2f, 0, 0,
                -0.2f, 1.5f, -0.2f, 0, 0,
                -0.2f, -0.2f, 1.5f, 0, 0,
                0, 0, 0, 1, 0
            };
            cm.set(sharpen);

            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(input, 0, 0, paint);

            return output;
        } catch (Exception e) {
            Log.e(TAG, "Error applying sharpening: " + e.getMessage());
            return input;
        }
    }

    private Bitmap correctPerspective(Bitmap input) {
        try {
            return input;
        } catch (Exception e) {
            Log.e(TAG, "Error correcting perspective: " + e.getMessage());
            return input;
        }
    }

    private void displayRecognizedTextWithHighVisibility(String text) {
        if (text == null || text.isEmpty()) {
            textResult.setText("No text detected");
            return;
        }

        textResult.setText(text);
        textResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textResult.setTextColor(Color.BLACK);

        textResult.setBackgroundColor(Color.argb(30, 255, 255, 255));
        textResult.setPadding(20, 10, 20, 10);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (overlay != null) {
            overlay.setDeviceRotation(ORIENTATIONS.get(rotation));
        }

        if (cameraState == CAMERA_STATE_OPENED && cameraCaptureSession != null) {
            closeCamera();
            backgroundHandler.postDelayed(this::openCamera, 500);
        }
    }

    private void copyToClipboard(String text) {
        if (text.isEmpty()) {
            Toast.makeText(this, "No text to copy", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("Recognized Text", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to copy to clipboard: " + e.getMessage());
            Toast.makeText(this, "Failed to copy text", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareText(String text) {
        if (text.isEmpty()) {
            Toast.makeText(this, "No text to share", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(shareIntent, "Share text via"));
        } catch (Exception e) {
            Log.e(TAG, "Failed to share text: " + e.getMessage());
            Toast.makeText(this, "Failed to share text", Toast.LENGTH_SHORT).show();
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join(500);
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e(TAG, "Failed to stop background thread: " + e.getMessage());
            }
        }
    }

    private void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            cameraState = CAMERA_STATE_CLOSING;

            if (cameraCaptureSession != null) {
                try {
                    cameraCaptureSession.stopRepeating();
                    cameraCaptureSession.close();
                } catch (Exception e) {
                    Log.w(TAG, "Exception closing capture session: " + e.getMessage());
                } finally {
                    cameraCaptureSession = null;
                }
            }

            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }

            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }

            isCameraReady = false;
            cameraState = CAMERA_STATE_CLOSED;
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted while closing camera: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (textRecognizer == null) {
            initializeTextRecognizer();
        }

        startBackgroundThread();

        SurfaceView viewFinder = viewFinderRef.get();
        if (viewFinder != null && viewFinder.getHolder().getSurface().isValid()) {
            if (cameraState == CAMERA_STATE_CLOSED) {
                openCamera();
            }
        }

        if (sensorManager != null) {
            Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                sensorManager.registerListener(
                        lightSensorListener,
                        lightSensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                );
            }
        }

        if (overlay != null) {
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            overlay.setDeviceRotation(ORIENTATIONS.get(rotation));
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();

        if (sensorManager != null) {
            sensorManager.unregisterListener(lightSensorListener);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textRecognizer != null) {
            try {
                textRecognizer.close();
                textRecognizer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error closing text recognizer: " + e.getMessage());
            }
        }

        if (imageProcessingExecutor instanceof java.util.concurrent.ExecutorService) {
            try {
                ((java.util.concurrent.ExecutorService) imageProcessingExecutor).shutdown();
            } catch (Exception e) {
                Log.e(TAG, "Error shutting down executor: " + e.getMessage());
            }
        }
    }
}