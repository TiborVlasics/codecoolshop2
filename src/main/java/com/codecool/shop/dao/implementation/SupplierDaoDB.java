package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SupplierDaoDB implements SupplierDao, Queryhandler {
    private String connection_config_path = "src/main/resources/connection.properties";
    private static SupplierDaoDB instance = null;
    private static final Logger logger = LoggerFactory.getLogger(SupplierDaoDB.class);


    public SupplierDaoDB(String connection_config_path) {
        this.connection_config_path = connection_config_path;
    }

    public SupplierDaoDB() {
    }

    public static SupplierDaoDB getInstance() {
        if (instance == null) {
            instance = new SupplierDaoDB();
        }
        return instance;
    }

    @Override
    public void add(Supplier supplier) {
        logger.debug("Adding supplier to db with name {}", supplier.getName());
        if (supplier == null) {
            throw new IllegalArgumentException();
        }
        String query = "INSERT INTO suppliers (name, description) VALUES (?, ?)";
        List<Object> parameters = Stream.of(supplier.getName(), supplier.getDescription()).collect(Collectors.toList());
        logger.debug("Adding supplier to db with name {}", supplier.getName());
        executeDMLQuery(query, parameters);
    }

    @Override
    public Supplier find(int id) {
        logger.debug("Selecting supplier with id {} from db", id);
        String query = "SELECT * FROM suppliers WHERE id = ?";
        List<Object> parameters = Stream.of(id).collect(Collectors.toList());
        List<Map<String, Object>> result = executeSelectQuery(query, parameters);
        if(result.size() == 0) {
            return null;
        }
        Supplier supplier = new Supplier((String) result.get(0).get("name"), (String) result.get(0).get("description"));
        supplier.setId((Integer) result.get(0).get("id"));
        logger.debug("Returning {}", supplier.getName());
        return supplier;
    }

    @Override
    public void remove(int id) {
        logger.debug("Removing supplier with id {} from db", id);
        String query = "DELETE FROM suppliers WHERE id = ?";
        List<Object> parameters = Stream.of(id).collect(Collectors.toList());
        executeDMLQuery(query, parameters);
    }

    @Override
    public Integer findIdByName(String name) {
        logger.debug("Finding id by name {} in db", name);
        String query = "SELECT * FROM suppliers WHERE name = ?";
        List<Object> parameters = Stream.of(name).collect(Collectors.toList());
        List<Map<String, Object>> result = executeSelectQuery(query, parameters);
        if (result.size() == 0) {
            return null;
        }
        int id = (Integer) result.get(0).get("id");
        logger.debug("Found: {}", id);
        return id;
    }

    @Override
    public Supplier getDefaultSupplier() {
        return new Supplier("All", "");
    }

    @Override
    public List<Product> filterProducts(List<Product> products, Supplier supplier) {
        logger.debug("Filtering products by supplier {} from db", supplier.getName());
        if (supplier.toString().equals(getDefaultSupplier().toString())) {
            return products;
        }
        List<Product> temp = new ArrayList<>();
        for (Product product : products) {
            if (product.getSupplier().toString().equals(supplier.toString())) {
                temp.add(product);
            }
        }
        logger.debug("Returning {} products", temp.size());
        return temp;
    }

    @Override
    public List<Supplier> getAll() {
        String query = "SELECT * FROM suppliers;";
        List<Supplier> allSupplierList = new ArrayList<>();
        List<Map<String, Object>> result = executeSelectQuery(query);
        for (Map<String, Object> row : result) {
            Supplier supplier = new Supplier((String) row.get("name"), (String) row.get("description"));
            supplier.setId((Integer) result.get(0).get("id"));
            allSupplierList.add(supplier);
        }
        logger.debug("Returning {} products from db", allSupplierList.size());
        return allSupplierList;
    }

    @Override
    public void removeAll() {
        String query = "DELETE FROM suppliers;";
        executeDMLQuery(query);
    }

    @Override
    public String getConnectionConfigPath() {
        return connection_config_path;
    }
}
