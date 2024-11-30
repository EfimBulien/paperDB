package com.example.paperclothes;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class EditProductActivity extends AppCompatActivity {
    private ProductDAO productDAO;
    private Product product;
    private EditText nameEditText, descriptionEditText, priceEditText;
    private EditText sizeEditText, colorEditText, quantityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        productDAO = new ProductDAO(this);
        ImageView productImageView = findViewById(R.id.productImageView);
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        colorEditText = findViewById(R.id.colorEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        product = productDAO.getProductById(productId);

        if (product != null) {
            nameEditText.setText(product.getName());
            descriptionEditText.setText(product.getDescription());
            priceEditText.setText(String.valueOf(product.getPrice()));
            sizeEditText.setText(product.getSize());
            colorEditText.setText(product.getColor());
            quantityEditText.setText(String.valueOf(product.getQuantity()));
            Picasso.get().load(product.getPhotoPath()).into(productImageView);
        } else {
            Toast.makeText(this, "Продукт не найден", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void saveProduct(View view) {
        product.setName(nameEditText.getText().toString());
        product.setDescription(descriptionEditText.getText().toString());
        product.setPrice(Double.parseDouble(priceEditText.getText().toString()));
        product.setSize(sizeEditText.getText().toString());
        product.setColor(colorEditText.getText().toString());
        product.setQuantity(Integer.parseInt(quantityEditText.getText().toString()));
        productDAO.updateProduct(product);
        Toast.makeText(this, "Продукт обновлен", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void deleteProduct(View view) {
        if (product != null) {
            boolean isDeleted = productDAO.deleteProduct(product.getId());
            if (isDeleted) {
                Toast.makeText(this, "Продукт удален", Toast.LENGTH_SHORT).show();
                finish(); // Закрыть текущую activity и вернуться к предыдущей
            } else {
                Toast.makeText(this, "Ошибка удаления продукта", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Продукт не найден", Toast.LENGTH_SHORT).show();
        }
    }

}