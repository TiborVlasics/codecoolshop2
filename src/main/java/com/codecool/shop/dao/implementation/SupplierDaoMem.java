package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.Supplier;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access model for suppliers
 *
 * A Data access model, which contains a list of Supplier objects, and has methods to access
 * and modify that list
 */
public class SupplierDaoMem implements SupplierDao {

    private List<Supplier> data = new ArrayList<>();
    private static SupplierDaoMem instance = null;
    private Supplier defaultSupplier;

    /**
     * A private Constructor prevents any other class from instantiating.
     */
    private SupplierDaoMem() {
        defaultSupplier = new Supplier("All", "");
    }

    /**
     * @return Instance of the class
     */
    public static SupplierDaoMem getInstance() {
        if (instance == null) {
            instance = new SupplierDaoMem();
        }
        return instance;
    }

    /**
     * Adds a supplier to Data
     * @param supplier Supplier object
     */
    @Override
    public void add(Supplier supplier) {
        supplier.setId(data.size() + 1);
        data.add(supplier);
    }

    /**
     *
     * @param id Id of suppplier
     * @return one Supplier object
     */
    @Override
    public Supplier find(int id) {
        return data.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    /**
     * Removes supplier from data
     * @param id id of supplier
     */
    @Override
    public void remove(int id) {
        data.remove(find(id));
    }

    /**
     * Removes all suppliers from the data
     */
    @Override
    public void removeAll() {
        data.clear();
    }

    /**
     * Gets all suppliers
     * @return a List of Supplier objects
     */
    @Override
    public List<Supplier> getAll() {
        return data;
    }

    /**
     * Gets the supplier's id by it's name
     *
     * @param name name of the supplier
     * @return id of the supplier (int)
     */
    @Override
    public Integer findIdByName(String name){
        for ( Supplier supp : data){
            if (name.equals(supp.getName())){
                return supp.getId();
            }
        }
        return null;
    }

    @Override
    public Supplier getDefaultSupplier(){
        return defaultSupplier;
    }

    /**
     * Gets all products by the given supplier
     *
     * @param products A list with all of the products
     * @param supplier A supplier object
     * @return a List of Products by the given Supplier
     */
    @Override
    public List<Product> filterProducts(List<Product> products, Supplier supplier){
        if (supplier.equals(defaultSupplier)) {
            return products;
        }
        List<Product> temp = new ArrayList<>();
        for (Product product : products) {
            if (product.getSupplier().equals(supplier)) {
                temp.add(product);
            }
        }
        return temp;
    }
}
