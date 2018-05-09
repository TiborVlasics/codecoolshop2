package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDaoDB implements ProductDao, Queryhandler {

    private String connection_config_path = "src/main/resources/connection.properties";
    private static ProductDaoDB instance = null;
    private static final Logger logger = LoggerFactory.getLogger(ProductDaoDB.class);


    private ProductDaoDB() {
    }

    public static ProductDaoDB getInstance() {
        if (instance == null) {
            instance = new ProductDaoDB();
        }
        return instance;
    }

    public ProductDaoDB(String configPath) {
        this.connection_config_path = configPath;
    }

    @Override
    public void add(Product product) {
        logger.debug("Adding product with name {} to database", product.getName());
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

    @Override
    public Product find(int id) {
        logger.debug("Finding product with id in db", id);
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products " +
                "WHERE id= ?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        logger.debug("Returning product from db with name {}", buildProductsList(results).get(0).getName());

        return buildProductsList(results).get(0);
    }

    @Override
    public void remove(int id) {
        logger.debug("Removing product from db with id {}", id);
        String query = "DELETE FROM products\n" +
                "WHERE id= ?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);

        executeDMLQuery(query, parameters);
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products;";
        List<Map<String, Object>> results = executeSelectQuery(query);
        logger.debug("Returning ({}) products", results.size());
        return buildProductsList(results);
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        logger.debug("Getting products by supplier {}", supplier.getName());
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products WHERE supplier = ?;";
        List<Object> parameters = new ArrayList<>();
        int supplierId = supplier.getId();
        parameters.add(supplierId);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        logger.debug("Returning {} products", results.size());
        return buildProductsList(results);
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        logger.debug("Getting products by {}", productCategory.getName());
        String query = "SELECT id, name, description, default_price, default_currency, product_category, supplier " +
                "FROM products WHERE product_category = ?;";
        List<Object> parameters = new ArrayList<>();
        int productCategoryId = productCategory.getId();
        parameters.add(productCategoryId);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        logger.debug("Returning {} products", results.size());
        return buildProductsList(results);
    }

    public int numberOfProducts() {
        String query = "SELECT COUNT(id) as count FROM products;";
        List<Map<String, Object>> results = executeSelectQuery(query);
        return Integer.parseInt(results.get(0).get("count").toString());
    }

    @Override
    public void removeAllProducts() {
        String query = "DELETE from products;";
        executeDMLQuery(query);
    }

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
