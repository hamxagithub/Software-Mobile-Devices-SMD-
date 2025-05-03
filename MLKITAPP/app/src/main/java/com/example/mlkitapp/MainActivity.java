package com.example.mlkitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private CardView cardTextRecognition, cardFaceDetection, cardBarcodeScanning, cardObjectDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize CardViews
        cardTextRecognition = findViewById(R.id.card_text_recognition);
        cardFaceDetection = findViewById(R.id.card_face_detection);
        cardBarcodeScanning = findViewById(R.id.card_barcode_scanning);
        cardObjectDetection = findViewById(R.id.card_object_detection);
        
        // Text Recognition implementation
        cardTextRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TextRecognitionActivity.class);
                startActivity(intent);
            }
        });

        // Face Detection implementation
        cardFaceDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FaceDetectionActivity.class);
                startActivity(intent);
            }
        });

        // Barcode Scanning implementation
        cardBarcodeScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeScanningActivity.class);
                startActivity(intent);
            }
        });

        // Object Detection implementation
        cardObjectDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ObjectDetectionActivity.class);
                startActivity(intent);
            }
        });
    }
}