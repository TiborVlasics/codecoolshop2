package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dao implementation for handling product queries
 * <p>Adding, removing etc.</p>
 *
 * @see com.codecool.shop.dao.implementation.Queryhandler
 */
public class ProductDaoDB implements ProductDao, Queryhandler {

    private String connection_config_path = "src/main/resources/connection.properties";
    private static ProductDaoDB instance = null;

    public ProductDaoDB() {}

    public static ProductDaoDB getInstance() {
        if (instance == null) {
            instance = new ProductDaoDB();
        }
        return instance;
    }

    /**
     *Constructor
     * <p>You need this if you want to join to the test database
     * or any other databases with a specific path, different than our main db</p>
     * @param configPath to join to different databases
     */
    public ProductDaoDB(String configPath) {
        this.connection_config_path = configPath;
    }

    /**
     * Executes an insert query with the given product
     * @param product an object with name, id, etc.
     */
    @Override
    public void add(Product product) {

        String query = "INSERT INTO products (name, description, default_price, default_currency, product_category, supplier)" +
                "VALUES (?, ?, ?, ?, ?, ?);";
        ProductCategoryDaoDB productCategoryDaoDB = new ProductCategoryDaoDB();
        int productId = productCategoryDaoDB.findIdByName(product.getProductCategory().getName());
        SupplierDaoDB supplierDaoDB = new SupplierDaoDB();
        int supplierId = supplierDaoDB.findIdByName(product.getSupplier().getName());
        List<Object> parameters = new ArrayList<>();
        parameters.add(product.getName());
        parameters.add(product.getDescription());
        parameters.add(product.getDefaultPrice());
        parameters.add(product.getDefaultCurrency().toString());
        parameters.add(productId);
        parameters.add(supplierId);

        executeDMLQuery(query, parameters);
    }

    /**
     * Executes a query with given id, to find a product
     * @param id product id
     * @return a product object
     */
    @Override
    public Product find(int id) {

        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products " +
                "WHERE id= ?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        return buildProductsList(results).get(0);

    }

    /**
     * <p>Removes a product from the product table of the database
     * with the given id</p>
     * @param id of the product to be removed
     */
    @Override
    public void remove(int id) {
        String query = "DELETE FROM products\n" +
                "WHERE id= ?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        executeDMLQuery(query, parameters);
    }

    /**
     * gets all products from database
     * @return all products in database, converted to object
     */
    @Override
    public List<Product> getAll() {
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products;";
        List<Map<String, Object>> results = executeSelectQuery(query);
        return buildProductsList(results);
    }

    /**
     * Get all products from db with the gives supplier
     * @param supplier a mentor object, who has a name, id, and different products
     * @return all products with the given supplier
     */
    @Override
    public List<Product> getBy(Supplier supplier) {
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products WHERE supplier = ?;";
        List<Object> parameters = new ArrayList<>();
        int supplierId = supplier.getId();
        parameters.add(supplierId);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        return buildProductsList(results);
    }

    /**
     * Get all products from db with the gives category
     * @param productCategory an object, with name, id, etc.
     * @return all products with the given category
     */
    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products WHERE product_category = ?;";
        List<Object> parameters = new ArrayList<>();
        int productCategoryId = productCategory.getId();
        parameters.add(productCategoryId);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        return buildProductsList(results);
    }

    /**
     * @return the number of products from the db's product table
     */
    public int numberOfProducts() {
        String query = "SELECT COUNT(id) as count FROM products;";
        List<Map<String, Object>> results = executeSelectQuery(query);
        return Integer.parseInt(results.get(0).get("count").toString());
    }

    /**
     * Removes all products from the db's product table
     */
    @Override
    public void removeAllProducts() {
        String query = "DELETE from products;";
        executeDMLQuery(query);
    }

    /**
     * This method makes a list of product objects from the data we get back from database
     * used by all the methods in the class, which are returning data
     * @param results the data returned by the query
     * @return list of products
     */
    private List<Product> buildProductsList(List<Map<String, Object>> results) {
        List<Product> products = new ArrayList<>();
        for (Map<String, Object> result : results) {
            int id = (int) result.get("id");
            String name = (String) result.get("name");
            String description = (String) result.get("description");
            int defaultPrice = (int) result.get("default_price");
            String defaultCurrency = (String) result.get("default_currency");
            int productCategoryId = (int) result.get("product_category");
            int supplierId = (int) result.get("supplier");
            ProductCategory productCategory = new ProductCategoryDaoDB(connection_config_path).find(productCategoryId);
            Supplier supplier = new SupplierDaoDB().find(supplierId);
            Product product = new Product(name, defaultPrice, defaultCurrency, description, productCategory, supplier);
            product.setId(id);
            products.add(product);
        }
        return products;
    }

    @Override
    public String getConnectionConfigPath() {
        return connection_config_path;
    }

}
