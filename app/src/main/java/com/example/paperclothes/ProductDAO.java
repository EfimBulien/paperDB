package com.example.paperclothes;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.paperdb.Paper;

public class ProductDAO implements Serializable {
    private static final String PRODUCTS_KEY = "products";
    private static int nextProductId = 1;

    public ProductDAO(Context context) {
        Paper.init(context);
    }

    public void createProduct(Product product) {
        List<Product> products = getAllProducts();
        product.setId(nextProductId++);
        products.add(product);
        Paper.book().write(PRODUCTS_KEY, products);
    }

    public List<Product> getAllProducts() {
        return Paper.book().read(PRODUCTS_KEY, new ArrayList<>());
    }

    public void updateProduct(Product product) {
        List<Product> products = getAllProducts();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                break;
            }
        }
        Paper.book().write(PRODUCTS_KEY, products);
    }

    public boolean deleteProduct(int productId) {
        List<Product> products = getAllProducts();
        boolean isRemoved = products.removeIf(product -> product.getId() == productId);
        if (isRemoved) {
            Paper.book().write(PRODUCTS_KEY, products);
        }
        return isRemoved;
    }


    public Product getProductById(int productId) {
        List<Product> products = getAllProducts();
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
}