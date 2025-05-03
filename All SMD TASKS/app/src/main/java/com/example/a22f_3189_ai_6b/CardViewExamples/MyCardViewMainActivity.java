package com.example.a22f_3189_ai_6b.CardViewExamples;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.a22f_3189_ai_6b.R;
import com.example.a22f_3189_ai_6b.databinding.ActivityMyCardViewMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MyCardViewMainActivity extends AppCompatActivity {

    private ActivityMyCardViewMainBinding binding;
    public List<MyModelCardView> list;
    public MyAdapterCardView adapter;
    public RecyclerView recyclerView;

    private View view;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyCardViewMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        recyclerView = findViewById(R.id.mycardviewrecyclerview);
        list = new ArrayList<>();
        adapter = new MyAdapterCardView(this, list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new GridSpacingDecoration(2,(12), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        LoadData();
    }

    private void LoadData() {

        int[] myImages = new int[]{
                R.drawable.img,
                R.drawable.img,
                R.drawable.img,
                R.drawable.img,
                R.drawable.img
        };
        MyModelCardView obj = new MyModelCardView("First Card View", 10, myImages[0]);
        list.add(obj);
        obj = new MyModelCardView("First Card View", 10, myImages[1]);
        list.add(obj);
        obj = new MyModelCardView("First Card View", 10, myImages[2]);
        list.add(obj);
        obj = new MyModelCardView("First Card View", 10, myImages[3]);
        list.add(obj);
        obj = new MyModelCardView("First Card View", 10, myImages[4]);
        list.add(obj);
        adapter.notifyDataSetChanged();
    }

    private class GridSpacingDecoration extends RecyclerView.ItemDecoration{
        int count, spacing;
        boolean EdgeInclude;



        public GridSpacingDecoration(int count, int spacing, boolean edgeInclude) {
            this.count = count;
            this.spacing = spacing;
            EdgeInclude = edgeInclude;
        }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, int itemPosition, @NonNull RecyclerView parent) {
            itemPosition = parent.getChildAdapterPosition(view);
            int itemColumn = itemPosition % count;
            if(EdgeInclude){
                outRect.left = spacing = itemColumn * spacing/count;
                if(itemPosition < count){
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            }
        }




    }
}