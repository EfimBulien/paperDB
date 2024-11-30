package com.example.paperclothes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        productDAO = new ProductDAO(this);
        productRecyclerView = findViewById(R.id.recyclerView);
        loadData();
    }

    private void loadData() {
        List<Product> products = productDAO.getAllProducts();
        productAdapter = new ProductAdapter(products, this::onProductClick);
        productRecyclerView.setAdapter(productAdapter);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onProductClick(int position) {
        int productId = productAdapter.getProductId(position);
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("PRODUCT_ID", productId);
        startActivity(intent);
    }

    public void addProduct(View view) {
        Intent intent = new Intent(this, CreateProductActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}