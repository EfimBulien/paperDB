package com.example.practice16;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private EditText nameText, descriptionText, priceText;
    private ImageView imageView;
    private String imageUrl;
    private String selectedName;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        nameText = findViewById(R.id.nameText);
        descriptionText = findViewById(R.id.descriptionText);
        priceText = findViewById(R.id.priceText);
        imageView = findViewById(R.id.imageView);
        Button addImageButton = findViewById(R.id.addImageButton);
        Button addButton = findViewById(R.id.addButton);
        Button updateButton = findViewById(R.id.updateButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        ListView listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getItemNames());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedName = adapter.getItem(position);
            Item item = Paper.book().read(selectedName);
            if (item != null) {
                nameText.setText(item.getName());
                descriptionText.setText(item.getDescription());
                priceText.setText(String.valueOf(item.getPrice()));
                imageUrl = item.getImageUrl();

                if (imageUrl != null) {
                    Picasso.get().load(new File(imageUrl)).into(imageView);
                } else {
                    imageView.setImageResource(0);
                }
            }
        });

        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        addButton.setOnClickListener(v -> {
            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();
            String priceStr = priceText.getText().toString();

            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imageUrl == null) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            Item item = new Item(name, description, price, imageUrl);
            Paper.book().write(name, item);
            Toast.makeText(this, "Товар успешно добавлен", Toast.LENGTH_SHORT).show();
            updateItemList();
            clearInputs();
        });

        updateButton.setOnClickListener(v -> {
            if (selectedName == null) {
                Toast.makeText(
                        this,
                        "Выберите товар для обновления",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();
            String priceStr = priceText.getText().toString();

            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imageUrl == null) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            Item updatedItem = new Item(name, description, price, imageUrl);
            Paper.book().write(selectedName, updatedItem);
            Toast.makeText(this, "Товар успешно обновлен", Toast.LENGTH_SHORT).show();
            updateItemList();
            clearInputs();
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedName == null) {
                Toast.makeText(
                        this,
                        "Выберите товар для удаления",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Товар успешно удален", Toast.LENGTH_SHORT).show();
            Paper.book().delete(selectedName);
            updateItemList();
            clearInputs();
        });
    }

    private void clearInputs() {
        nameText.setText("");
        descriptionText.setText("");
        priceText.setText("");
        imageView.setImageResource(0);
        imageUrl = null;
        selectedName = null;
    }

    private void updateItemList() {
        adapter.clear();
        adapter.addAll(getItemNames());
        adapter.notifyDataSetChanged();
    }

    private List<String> getItemNames() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File directory = new File(getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdir();
            }

            File imageFile = new File(directory, System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = Files.newOutputStream(imageFile.toPath());

            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String localPath = saveImageToInternalStorage(selectedImageUri);

            if (localPath != null) {
                imageUrl = localPath;
                Picasso.get().load(new File(imageUrl)).into(imageView);
            } else {
                Toast.makeText(
                        this,
                        "Ошибка сохранения изображения",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
