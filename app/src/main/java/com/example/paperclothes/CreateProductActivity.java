package com.example.paperclothes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class CreateProductActivity extends AppCompatActivity {
    private ProductDAO productDAO;
    private ImageView productImageView;
    private EditText nameEditText, descriptionEditText, priceEditText;
    private EditText sizeEditText, colorEditText, quantityEditText;
    private Uri imageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        imageUri = data.getData();
                        if (imageUri != null) {
                            Picasso.get().load(imageUri).into(productImageView);
                            String fileName = getFileName(imageUri);
                            Toast.makeText(CreateProductActivity.this,
                                    "Выбрано: " + fileName, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        productDAO = new ProductDAO(this);
        productImageView = findViewById(R.id.productImageView);
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        colorEditText = findViewById(R.id.colorEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    public void saveProduct(View view) {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        double price = Double.parseDouble(priceEditText.getText().toString());
        String size = sizeEditText.getText().toString();
        String color = colorEditText.getText().toString();
        int quantity = Integer.parseInt(quantityEditText.getText().toString());
        String photoPath = imageUri.toString();
        Product product = new Product(name, description, price, size, color, quantity, photoPath);
        productDAO.createProduct(product);
        finish();
    }

    private String getFileName(Uri uri) {
        if (uri == null) {
            return null;
        }
        if (Objects.equals(uri.getScheme(), "content")) {
            Cursor cursor = getContentResolver().query(uri, null, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = cursor.getString(nameIndex);
                cursor.close();
                return fileName;
            }
        }
        return uri.getPath();
    }
}