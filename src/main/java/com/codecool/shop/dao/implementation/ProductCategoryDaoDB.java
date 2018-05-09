package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductCategoryDaoDB implements ProductCategoryDao, Queryhandler {

    private String connectionConfigPath = "src/main/resources/connection.properties";
    private static ProductCategoryDaoDB instance = null;
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryDaoDB.class);

    public static ProductCategoryDaoDB getInstance() {
        if (instance == null) {
            instance = new ProductCategoryDaoDB();
        }
        return instance;
    }

    public ProductCategoryDaoDB(String connectionConfigPath) {
        this.connectionConfigPath = connectionConfigPath;
    }

    public ProductCategoryDaoDB() { }

    @Override
    public void add(ProductCategory category) {
        logger.debug("Adding product category {} to db", category.getName());
        if (category == null) {
            throw new IllegalArgumentException("Null category can not be added.");
        } else if ("".equals(category.getName())){
            throw new ValueException("Category must have a name.");
        } else if ("".equals(category.getDepartment())){
            throw new ValueException("Category must have a department.");
        } else if ("".equals(category.getDescription())){
            throw new ValueException("Category must have a description.");
        }

        String query = "INSERT INTO product_categories (name, description, department) VALUES" +
                " (?, ?, ?);";
        List<Object> parameters = new ArrayList<>();
        parameters.add(category.getName());
        parameters.add(category.getDescription());
        parameters.add(category.getDepartment());
        executeDMLQuery(query, parameters);
    }

    @Override
    public ProductCategory find(int id) {
        logger.debug("Finding category by id {}", id);
        String query = "SELECT * FROM product_categories WHERE id=?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        List<Map<String, Object>> resultList = executeSelectQuery(query, parameters);

        ProductCategory result = null;

        if (resultList.size() == 1) {
            for (Map<String, Object> resultSet : resultList) {
                String name = resultSet.get("name").toString();
                String description = resultSet.get("description").toString();
                String department = resultSet.get("department").toString();
                result = new ProductCategory(name, department, description);
                result.setId(id);
            }
        }
        logger.debug("Found {} categories", result);
        return result;
    }

    @Override
    public void remove(int id) {
        logger.debug("Removing item with id {} from db", id);
        String query = "DELETE FROM product_categories WHERE id=?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);

        String tempQuery = "SELECT * FROM product_categories WHERE id=?;";
        List<Map<String, Object>> resultList = executeSelectQuery(tempQuery, parameters);
        if (resultList.size() == 0){
            throw new IllegalArgumentException("There is no product category with such id in the database.");
        }
        Integer result = executeDMLQuery(query, parameters);
    }

    @Override
    public void removeAll() {
        String query = "DELETE from product_categories;";
        executeDMLQuery(query);
    }

    @Override
    public Integer findIdByName(String name) {
        logger.debug("Finding id by name {}", name);
        String query = "SELECT * FROM product_categories WHERE name=?;";
        List<Object> parameters = new ArrayList<>();
        parameters.add(name);
        List<Map<String, Object>> resultList = executeSelectQuery(query, parameters);

        Integer result = null;
        try {
            result = Integer.parseInt(resultList.get(0).get("id").toString());
        } catch (IndexOutOfBoundsException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        logger.debug("Found {}", result);
        return result;
    }

    @Override
    public ProductCategory getDefaultCategory() {
        return new ProductCategory("All", "", "");
    }

    @Override
    public List<Product> filterProducts(List<Product> products, ProductCategory category) {
        logger.debug("Filtering products by category {}", category.getName());
        if ((category.toString()).equals(getDefaultCategory().toString())) {
            return products;
        }
        List<Product> temp = new ArrayList<>();
        for (Product product : products) {
            if (product.getProductCategory().toString().equals(category.toString())) {
                temp.add(product);
            }
        }
        logger.debug("Found {} products", temp.size());
        return temp;
    }

    @Override
    public List<ProductCategory> getAll() {
        logger.debug("Getting all categories from db");
        String query = "SELECT * FROM product_categories;";
        List<Map<String, Object>> resultList = executeSelectQuery(query);

        List<ProductCategory> results = new ArrayList<>();

        for (Map<String, Object> resultSet : resultList) {
            String id = resultSet.get("id").toString();
            String name = resultSet.get("name").toString();
            String description = resultSet.get("description").toString();
            String department = resultSet.get("department").toString();
            ProductCategory temp = new ProductCategory(name, department, description);
            temp.setId(Integer.parseInt(id));
            results.add(temp);
        }
        logger.debug("Got {} categories", results.size());
        return results;
    }

    @Override
    public String getConnectionConfigPath() {
        return connectionConfigPath;
    }
}
