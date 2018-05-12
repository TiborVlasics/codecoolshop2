package com.codecool.shop.dao;

import com.codecool.shop.model.Supplier;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;

import java.util.List;

/**
 * Data object model for product handling
 *
 *<p>Handles product data</p>
 *
 */
public interface ProductDao {

    /**
     * Adds product to database/memory
     * @param product a product object with name, Supplier etc.
     */
    void add(Product product);
    Product find(int id);
    void remove(int id);
    void removeAllProducts();
    List<Product> getAll();
    List<Product> getBy(Supplier supplier);
    List<Product> getBy(ProductCategory productCategory);

}
