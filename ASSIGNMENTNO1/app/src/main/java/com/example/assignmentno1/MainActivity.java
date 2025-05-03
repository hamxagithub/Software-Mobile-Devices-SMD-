package com.example.assignmentno1;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignmentno1.Product;
import com.example.assignmentno1.ProductAdapter;
import com.example.assignmentno1.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView productList;
    ArrayList<Product> products;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productList = findViewById(R.id.productList);
        products = new ArrayList<>();
        products.add(new Product("Apple iPhone 13", "$799", R.drawable.placeholder));
        products.add(new Product("Samsung Galaxy S21", "$699", R.drawable.placeholder));

        adapter = new ProductAdapter(this, products);
        productList.setAdapter(adapter);
    }
}
