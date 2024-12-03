package com.example.paperclothes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.io.IOException;

public class EditProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProductDAO productDAO;
    private Product product;
    private EditText nameEditText, descriptionEditText, priceEditText;
    private EditText sizeEditText, colorEditText, quantityEditText;
    private ImageView productImageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        productDAO = new ProductDAO(this);
        productImageView = findViewById(R.id.productImageView);
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        colorEditText = findViewById(R.id.colorEditText);
        quantityEditText = findViewById(R.id.quantityEditText);

        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        product = productDAO.getProductById(productId);

        if (product != null) {
            nameEditText.setText(product.getName());
            descriptionEditText.setText(product.getDescription());
            priceEditText.setText(String.valueOf(product.getPrice()));
            sizeEditText.setText(product.getSize());
            colorEditText.setText(product.getColor());
            quantityEditText.setText(String.valueOf(product.getQuantity()));

            String photoPath = product.getPhotoPath();
            if (photoPath != null && !photoPath.isEmpty()) {
                Picasso.get().load(photoPath).into(productImageView);
            } else {
                Toast.makeText(this,"Изображение не выбрано",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Продукт не найден", Toast.LENGTH_SHORT).show();
            finish();
        }

        productImageView.setOnClickListener(v -> openImageChooser());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                productImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveProduct(View view) {
        if (validateInputs()) {
            product.setName(nameEditText.getText().toString());
            product.setDescription(descriptionEditText.getText().toString());
            product.setPrice(Double.parseDouble(priceEditText.getText().toString()));
            product.setSize(sizeEditText.getText().toString());
            product.setColor(colorEditText.getText().toString());
            product.setQuantity(Integer.parseInt(quantityEditText.getText().toString()));

            if (imageUri != null) {
                product.setPhotoPath(imageUri.toString());
            } else {
                product.setPhotoPath(product.getPhotoPath());
            }

            productDAO.updateProduct(product);
            Toast.makeText(this, "Продукт обновлен", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean validateInputs() {
        if (nameEditText.getText().toString().isEmpty() ||
                descriptionEditText.getText().toString().isEmpty() ||
                priceEditText.getText().toString().isEmpty() ||
                sizeEditText.getText().toString().isEmpty () ||
                colorEditText.getText().toString().isEmpty() ||
                quantityEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void deleteProduct(View view) {
        if (product != null) {
            boolean isDeleted = productDAO.deleteProduct(product.getId());
            if (isDeleted) {
                Toast.makeText(this, "Продукт удален", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка удаления продукта", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Продукт не найден", Toast.LENGTH_SHORT).show();
        }
    }
}