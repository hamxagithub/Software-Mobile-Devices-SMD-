package com.example.mlkitapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.face.Face;

import java.util.ArrayList;
import java.util.List;

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.FaceViewHolder> {

    private List<Face> faces;

    public FaceAdapter() {
        this.faces = new ArrayList<>();
    }

    @NonNull
    @Override
    public FaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_face, parent, false);
        return new FaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaceViewHolder holder, int position) {
        Face face = faces.get(position);
        holder.bind(face, position);
    }

    @Override
    public int getItemCount() {
        return faces.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFaces(List<Face> newFaces) {
        this.faces = new ArrayList<>(newFaces);
        notifyDataSetChanged();
    }

    public List<Face> getFaces() {
        return faces;
    }

    static class FaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFaceId;
        private final TextView tvSmileProbability;
        private final TextView tvRightEyeOpen;
        private final TextView tvLeftEyeOpen;
        private final TextView tvFaceRotation;
        private final TextView tvTrackingId;
        private final ProgressBar pbSmile;
        private final ProgressBar pbRightEye;
        private final ProgressBar pbLeftEye;
        private final ImageView ivRightEye;
        private final ImageView ivLeftEye;

        public FaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFaceId = itemView.findViewById(R.id.tv_face_id);
            tvSmileProbability = itemView.findViewById(R.id.tv_smile_probability);
            tvRightEyeOpen = itemView.findViewById(R.id.tv_right_eye_open);
            tvLeftEyeOpen = itemView.findViewById(R.id.tv_left_eye_open);
            tvFaceRotation = itemView.findViewById(R.id.tv_face_rotation);
            tvTrackingId = itemView.findViewById(R.id.tv_tracking_id);
            pbSmile = itemView.findViewById(R.id.pb_smile);
            pbRightEye = itemView.findViewById(R.id.pb_right_eye);
            pbLeftEye = itemView.findViewById(R.id.pb_left_eye);
            ivRightEye = itemView.findViewById(R.id.iv_right_eye);
            ivLeftEye = itemView.findViewById(R.id.iv_left_eye);
        }

        public void bind(Face face, int position) {
            // Set face ID
            tvFaceId.setText(String.format("Face #%d", position + 1));
            
            // Set tracking ID if available
            Integer trackingId = face.getTrackingId();
            tvTrackingId.setText(trackingId != null ? 
                    String.format("Tracking ID: %d", trackingId) : 
                    "Tracking ID: Not available");

            // Set smile probability if available
            if (face.getSmilingProbability() != null) {
                float smileProb = face.getSmilingProbability();
                int smilePercent = Math.round(smileProb * 100);
                tvSmileProbability.setText(String.format("Smile: %d%%", smilePercent));
                pbSmile.setProgress(smilePercent);
            } else {
                tvSmileProbability.setText("Smile: NA");
                pbSmile.setProgress(0);
            }

            // Set right eye open probability if available
            if (face.getRightEyeOpenProbability() != null) {
                float rightEyeProb = face.getRightEyeOpenProbability();
                int rightEyePercent = Math.round(rightEyeProb * 100);
                tvRightEyeOpen.setText(String.format("Right eye: %d%%", rightEyePercent));
                pbRightEye.setProgress(rightEyePercent);
                
                // Change icon tint based on eye open state
                if (rightEyePercent < 50) {
                    ivRightEye.setColorFilter(itemView.getResources()
                            .getColor(android.R.color.darker_gray));
                } else {
                    ivRightEye.setColorFilter(itemView.getResources()
                            .getColor(android.R.color.holo_green_dark));
                }
            } else {
                tvRightEyeOpen.setText("Right eye: NA");
                pbRightEye.setProgress(0);
            }

            // Set left eye open probability if available
            if (face.getLeftEyeOpenProbability() != null) {
                float leftEyeProb = face.getLeftEyeOpenProbability();
                int leftEyePercent = Math.round(leftEyeProb * 100);
                tvLeftEyeOpen.setText(String.format("Left eye: %d%%", leftEyePercent));
                pbLeftEye.setProgress(leftEyePercent);
                
                // Change icon tint based on eye open state
                if (leftEyePercent < 50) {
                    ivLeftEye.setColorFilter(itemView.getResources()
                            .getColor(android.R.color.darker_gray));
                } else {
                    ivLeftEye.setColorFilter(itemView.getResources()
                            .getColor(android.R.color.holo_green_dark));
                }
            } else {
                tvLeftEyeOpen.setText("Left eye: NA");
                pbLeftEye.setProgress(0);
            }

            // Set face rotation values
            float rotX = face.getHeadEulerAngleX();  // Head tilt (up/down)
            float rotY = face.getHeadEulerAngleY();  // Head rotation (left/right)
            float rotZ = face.getHeadEulerAngleZ();  // Head tilt sideways
            
            tvFaceRotation.setText(String.format("Rotation: X:%.1f° Y:%.1f° Z:%.1f°", 
                    rotX, rotY, rotZ));
        }
    }
}