package com.example.cardviewfilehandling_mam.CardView;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cardviewfilehandling_mam.R;
import java.util.ArrayList;
import java.util.List;

public class CardView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        RecyclerView recyclerView = findViewById(R.id.cardid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Students> studentsList = new ArrayList<>();
        int[] images = {R.drawable.img2, R.drawable.img3, R.drawable.img5,R.drawable.img6};

        for (int i = 0; i < 10; i++) {

            studentsList.add(new Students("Student " + (i + 1), images[i % images.length]));
        }

        StudentAdapter adapter = new StudentAdapter(studentsList);
        recyclerView.setAdapter(adapter);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}
