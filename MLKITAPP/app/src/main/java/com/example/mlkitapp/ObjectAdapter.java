package com.example.mlkitapp;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.objects.DetectedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectAdapter extends RecyclerView.Adapter<ObjectAdapter.ViewHolder> {
    private List<DetectedObject> objects = new ArrayList<>();
    private List<Integer> colorMap = new ArrayList<>();
    private static final Random RANDOM = new Random();
    private ObjectClickListener objectClickListener;
    
    // Available colors for object indicators
    private static final int[] COLORS = {
            Color.rgb(255, 64, 129), // Pink
            Color.rgb(33, 150, 243), // Blue
            Color.rgb(255, 193, 7),  // Amber
            Color.rgb(0, 150, 136),  // Teal
            Color.rgb(139, 195, 74), // Light Green
            Color.rgb(103, 58, 183), // Deep Purple
            Color.rgb(255, 87, 34),  // Deep Orange
            Color.rgb(3, 169, 244),  // Light Blue
            Color.rgb(0, 188, 212),  // Cyan
            Color.rgb(76, 175, 80),  // Green
    };
    
    // Interface for click events
    public interface ObjectClickListener {
        void onObjectClick(DetectedObject object);
    }
    
    // Setter for click listener
    public void setObjectClickListener(ObjectClickListener listener) {
        this.objectClickListener = listener;
    }

    public void updateObjects(List<DetectedObject> newObjects) {
        // Update color map to match new objects list size
        while (colorMap.size() < newObjects.size()) {
            colorMap.add(COLORS[RANDOM.nextInt(COLORS.length)]);
        }
        
        this.objects = newObjects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_object_detection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetectedObject object = objects.get(position);
        int color = colorMap.get(position % colorMap.size());
        holder.bind(object, color);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View colorIndicator;
        private final TextView labelText;
        private final TextView confidenceValue;
        private final TextView locationText;
        private final TextView sizeText;
        private final ImageButton detailsButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.view_color_indicator);
            labelText = itemView.findViewById(R.id.tv_object_label);
            confidenceValue = itemView.findViewById(R.id.tv_confidence_value);
            locationText = itemView.findViewById(R.id.tv_object_location);
            sizeText = itemView.findViewById(R.id.tv_object_size);
            detailsButton = itemView.findViewById(R.id.btn_object_details);
        }

        void bind(DetectedObject object, int color) {
            colorIndicator.setBackgroundColor(color);
            
            String label = "Unknown";
            int confidence = 0;
            
            if (!object.getLabels().isEmpty()) {
                label = object.getLabels().get(0).getText();
                confidence = (int)(object.getLabels().get(0).getConfidence() * 100);
            }
            
            labelText.setText(label);
            confidenceValue.setText(itemView.getContext().getString(
                    R.string.confidence_value_format, confidence));
            
            Rect boundingBox = object.getBoundingBox();
            locationText.setText(itemView.getContext().getString(
                    R.string.object_location_format, 
                    boundingBox.left, boundingBox.top));
            
            sizeText.setText(itemView.getContext().getString(
                    R.string.object_size_format,
                    boundingBox.width(), boundingBox.height()));
                    
            // Set click listener for the details button
            detailsButton.setOnClickListener(v -> {
                if (objectClickListener != null) {
                    objectClickListener.onObjectClick(object);
                }
            });
            
            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                if (objectClickListener != null) {
                    objectClickListener.onObjectClick(object);
                }
            });
        }
    }
}