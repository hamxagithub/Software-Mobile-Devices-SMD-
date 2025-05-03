package com.example.mid2_sir_tahir.CardViewExample;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.mid2_sir_tahir.R;
import com.example.mid2_sir_tahir.databinding.ActivityCardScrollingBinding;

import java.util.ArrayList;
import java.util.List;

public class CardScrollingActivity extends AppCompatActivity {

    private ActivityCardScrollingBinding binding;
    public List<MyModel> list;
    public MycardViewAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCardScrollingBinding.inflate(getLayoutInflater());
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
        recyclerView =findViewById(R.id.recycler1);
        list=new ArrayList<>();
        adapter=new MycardViewAdapter(this,list);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacing(2,12,true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        Insert();
    }
    private class GridSpacing extends RecyclerView.ItemDecoration{
        int count ,spacing;
        boolean Edgeinclude;

        public GridSpacing(int count, int spacing, boolean edgeinclude) {
            this.count = count;
            this.spacing = spacing;
            Edgeinclude = edgeinclude;
        }
    }
    public void Insert()
    {
        int [] image=new int[]{
                R.drawable.a,R.drawable.a,
                R.drawable.a,R.drawable.a,
                R.drawable.a,R.drawable.a,
                R.drawable.a,R.drawable.a
        };
        MyModel obj=new MyModel("First",10,image[0]);
        list.add(obj);
        obj=new MyModel("Second",20,image[1]);
        list.add(obj);
        obj=new MyModel("Third",30,image[2]);
        list.add(obj);
        obj=new MyModel("Forth",40,image[3]);
        list.add(obj);



    }
}