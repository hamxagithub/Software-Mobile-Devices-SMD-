package com.example.assignmentno1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.assignmentno1.Product;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }

        Product product = getItem(position);

        TextView name = convertView.findViewById(R.id.product_name);
        TextView price = convertView.findViewById(R.id.product_price);
        ImageView image = convertView.findViewById(R.id.product_image);

        name.setText(product.getName());
        price.setText(product.getPrice());
        image.setImageResource(product.getImageResId());

        return convertView;
    }
}
