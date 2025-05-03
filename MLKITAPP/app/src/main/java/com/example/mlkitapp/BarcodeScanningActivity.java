package com.example.mlkitapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.annotation.OptIn(markerClass = ExperimentalGetImage.class)
public class BarcodeScanningActivity extends AppCompatActivity {
    private static final String TAG = "BarcodeScanActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private PreviewView previewView;
    private CardView resultPanel;
    private TextView tvResultType;
    private TextView tvResultContent;
    private Button btnCopy;
    private Button btnAction;
    private Button btnScanAgain;
    private ImageButton btnToggleFlash;
    private View scanBoxGuide;

    private Camera camera;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private boolean isFlashEnabled = false;
    private boolean isPaused = false;
    
    // Sound and vibration for feedback
    private MediaActionSound sound;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanning);

        // Initialize UI components
        previewView = findViewById(R.id.barcode_preview_view);
        resultPanel = findViewById(R.id.result_panel);
        tvResultType = findViewById(R.id.tv_result_type);
        tvResultContent = findViewById(R.id.tv_result_content);
        btnCopy = findViewById(R.id.btn_copy);
        btnAction = findViewById(R.id.btn_action);
        btnScanAgain = findViewById(R.id.btn_scan_again);
        btnToggleFlash = findViewById(R.id.btn_toggle_flash);
        scanBoxGuide = findViewById(R.id.scan_box_guide);

        // Initialize barcode scanner
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_CODE_93,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_PDF417
                )
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);

        // Initialize sound and vibration
        sound = new MediaActionSound();
        sound.load(MediaActionSound.SHUTTER_CLICK);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        // Set up button listeners
        btnToggleFlash.setOnClickListener(v -> toggleFlash());
        
        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Barcode Content", tvResultContent.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Content copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        
        btnAction.setOnClickListener(v -> performActionBasedOnBarcodeType());
        
        btnScanAgain.setOnClickListener(v -> {
            resultPanel.setVisibility(View.GONE);
            scanBoxGuide.setVisibility(View.VISIBLE);
            isPaused = false;
        });
        
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void toggleFlash() {
        if (camera != null) {
            isFlashEnabled = !isFlashEnabled;
            camera.getCameraControl().enableTorch(isFlashEnabled);
            btnToggleFlash.setImageResource(isFlashEnabled ? 
                    android.R.drawable.ic_menu_view : 
                    android.R.drawable.ic_menu_compass);
        }
    }

    private void performActionBasedOnBarcodeType() {
        String content = tvResultContent.getText().toString();
        String type = tvResultType.getText().toString();
        
        if (content.isEmpty()) {
            return;
        }
        
        Intent intent = null;
        
        if (type.contains("URL")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(content));
        } else if (type.contains("Email")) {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + content));
        } else if (type.contains("Phone")) {
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + content));
        } else if (type.contains("SMS")) {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + content));
        } else if (type.contains("Contact")) {
            // Contact info would need more complex handling
            Toast.makeText(this, "Contact info detected. Copy and save manually.", Toast.LENGTH_SHORT).show();
            return;
        } else if (type.contains("Wi-Fi")) {
            // Wi-Fi would need more complex handling
            Toast.makeText(this, "Wi-Fi credentials detected. Copy and use manually.", Toast.LENGTH_SHORT).show();
            return;
        } else if (type.contains("Calendar")) {
            // Calendar would need more complex handling
            Toast.makeText(this, "Calendar event detected. Copy and create manually.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // For other types, just show a toast
            Toast.makeText(this, "No specific action available for this type", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if there's an app that can handle the intent
        if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No app found to handle this content", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Set up the image analyzer
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageForBarcodeScanning);

                // Select back camera as default
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: ", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @ExperimentalGetImage
    private void processImageForBarcodeScanning(ImageProxy imageProxy) {
        if (isPaused) {
            imageProxy.close();
            return;
        }

        @androidx.camera.core.ExperimentalGetImage
        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees());

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (!barcodes.isEmpty() && !isPaused) {
                        isPaused = true;
                        Barcode barcode = barcodes.get(0);
                        handleBarcodeResult(barcode);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed: ", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void handleBarcodeResult(Barcode barcode) {
        // Alert the user via sound and vibration
        sound.play(MediaActionSound.SHUTTER_CLICK);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        runOnUiThread(() -> {
            // Hide scan box guide
            scanBoxGuide.setVisibility(View.GONE);
            
            // Show result panel
            resultPanel.setVisibility(View.VISIBLE);
            
            // Determine the barcode type and format appropriately
            String typeText = getBarcodeTypeText(barcode);
            tvResultType.setText(typeText);
            
            // Get the barcode content
            String valueText = getBarcodeValueText(barcode);
            tvResultContent.setText(valueText);
            
            // Set the action button text based on barcode type
            String actionText = getActionText(barcode);
            btnAction.setText(actionText);
            btnAction.setVisibility(actionText.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private String getBarcodeTypeText(Barcode barcode) {
        String format;
        switch (barcode.getFormat()) {
            case Barcode.FORMAT_QR_CODE: format = "QR Code"; break;
            case Barcode.FORMAT_AZTEC: format = "AZTEC"; break;
            case Barcode.FORMAT_CODE_128: format = "CODE 128"; break;
            case Barcode.FORMAT_CODE_39: format = "CODE 39"; break;
            case Barcode.FORMAT_CODE_93: format = "CODE 93"; break;
            case Barcode.FORMAT_EAN_8: format = "EAN 8"; break;
            case Barcode.FORMAT_EAN_13: format = "EAN 13"; break;
            case Barcode.FORMAT_UPC_A: format = "UPC A"; break;
            case Barcode.FORMAT_UPC_E: format = "UPC E"; break;
            case Barcode.FORMAT_PDF417: format = "PDF417"; break;
            default: format = "Unknown";
        }
        
        String type;
        switch (barcode.getValueType()) {
            case Barcode.TYPE_CONTACT_INFO: type = "Contact"; break;
            case Barcode.TYPE_EMAIL: type = "Email"; break;
            case Barcode.TYPE_PHONE: type = "Phone"; break;
            case Barcode.TYPE_SMS: type = "SMS"; break;
            case Barcode.TYPE_URL: type = "URL"; break;
            case Barcode.TYPE_WIFI: type = "Wi-Fi"; break;
            case Barcode.TYPE_GEO: type = "Location"; break;
            case Barcode.TYPE_CALENDAR_EVENT: type = "Calendar"; break;
            case Barcode.TYPE_TEXT: type = "Text"; break;
            default: type = "Data";
        }
        
        return type + " (" + format + ")";
    }

    private String getBarcodeValueText(Barcode barcode) {
        switch (barcode.getValueType()) {
            case Barcode.TYPE_CONTACT_INFO:
                Barcode.ContactInfo contactInfo = barcode.getContactInfo();
                if (contactInfo != null) {
                    StringBuilder sb = new StringBuilder();
                    if (contactInfo.getName() != null) {
                        sb.append("Name: ").append(contactInfo.getName().getFormattedName()).append("\n");
                    }
                    if (contactInfo.getPhones() != null && !contactInfo.getPhones().isEmpty()) {
                        sb.append("Phone: ").append(contactInfo.getPhones().get(0).getNumber());
                    }
                    return sb.toString();
                }
                break;
            case Barcode.TYPE_EMAIL:
                return barcode.getEmail() != null ? barcode.getEmail().getAddress() : "";
            case Barcode.TYPE_PHONE:
                return barcode.getPhone() != null ? barcode.getPhone().getNumber() : "";
            case Barcode.TYPE_SMS:
                return barcode.getSms() != null ? barcode.getSms().getPhoneNumber() : "";
            case Barcode.TYPE_URL:
                return barcode.getUrl() != null ? barcode.getUrl().getUrl() : "";
            case Barcode.TYPE_WIFI:
                return barcode.getWifi() != null ? "SSID: " + barcode.getWifi().getSsid() : "";
            case Barcode.TYPE_GEO:
                Barcode.GeoPoint geoPoint = barcode.getGeoPoint();
                if (geoPoint != null) {
                    return "Lat: " + geoPoint.getLat() + ", Lng: " + geoPoint.getLng();
                }
                break;
            case Barcode.TYPE_CALENDAR_EVENT:
                return barcode.getCalendarEvent() != null ? barcode.getCalendarEvent().getSummary() : "";
            case Barcode.TYPE_TEXT:
            default:
                return barcode.getDisplayValue() != null ? barcode.getDisplayValue() : "";
        }
        return "";
    }

    private String getActionText(Barcode barcode) {
        switch (barcode.getValueType()) {
            case Barcode.TYPE_URL: return "Open URL";
            case Barcode.TYPE_EMAIL: return "Send Email";
            case Barcode.TYPE_PHONE: return "Call";
            case Barcode.TYPE_SMS: return "Send SMS";
            case Barcode.TYPE_GEO: return "Open Map";
            case Barcode.TYPE_WIFI: 
            case Barcode.TYPE_CALENDAR_EVENT:
            case Barcode.TYPE_CONTACT_INFO:
            case Barcode.TYPE_TEXT:
            default: return "";
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permissions are required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        barcodeScanner.close();
    }
}