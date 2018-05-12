package com.codecool.shop.controller;

import com.codecool.shop.dao.implementation.*;
import org.json.JSONObject;

import com.codecool.shop.model.User;
import com.codecool.shop.model.ShoppingCart;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.*;

import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * <h3>Main page</h3>
 *
 * <p>Renders the main page with products that user can sort and add to it's shopping cart
 * </p>
 *
 * This class is a web servlet
 * <p>User can sort products by supplier and category
 * Post method adds item to shopping cart, and updates the sum of shopping cart, and number of items
 * with ajax.</p>
 *
 * @author Kristóf, Máté, Tibi and Zsolt
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/"})
public class ProductController extends HttpServlet {

    /**
     * Gives back the main page with data to the user
     *
     * @param req a get request by users
     * @param resp a response by the server
     * @throws IOException it throws it sometimes
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());


        SupplierDao supplierDataStore = SupplierDaoDB.getInstance();
        ProductDao productDataStore = ProductDaoDB.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoDB.getInstance();

        ProductCategory category;
        Supplier supplier;

        String selectedCategory = req.getParameter("select_category");
        if (selectedCategory != null &&
                !selectedCategory.equals(productCategoryDataStore.getDefaultCategory().getName())) {
            category = productCategoryDataStore.find(
                    productCategoryDataStore.findIdByName(
                            req.getParameter("select_category")));
        } else {
            category = productCategoryDataStore.getDefaultCategory();
        }

        String selectedSupplier = req.getParameter("select_supplier");
        if (selectedSupplier != null &&
                !selectedSupplier.equals(supplierDataStore.getDefaultSupplier().getName())) {
            supplier = supplierDataStore.find(
                    supplierDataStore.findIdByName(
                            req.getParameter("select_supplier")));
        } else {
            supplier = supplierDataStore.getDefaultSupplier();
        }

        List<Product> products = supplierDataStore.filterProducts(
                productCategoryDataStore.filterProducts(
                        productDataStore.getAll(), category), supplier);


        if (req.getParameter("ajax") != null) {
            JSONObject json = new JSONObject();
            int numberOfProducts = 0;
            for (Product product : products) {

                json.put("Product" + numberOfProducts, new JSONObject()
                        .put("title", product.getName())
                        .put("description", product.getDescription())
                        .put("id", product.getId())
                        .put("price", product.getPrice())
                        .put("supplier", product.getSupplier().getName()));
                numberOfProducts++;
            }

            resp.setContentType("application/json");
            resp.getWriter().print(json);

        } else {
            ShoppingCart shoppingCart = getShoppingCart(req);

            context.setVariable("total_price", shoppingCart.sumCart());
            context.setVariable("number_of_items", shoppingCart.getNumberOfItems());
            context.setVariable("category_list", productCategoryDataStore.getAll());
            context.setVariable("supplier_list", supplierDataStore.getAll());
            context.setVariable("category", category);
            context.setVariable("supplier", supplier);
            context.setVariable("products", products);

            engine.process("product/index.html", context, resp.getWriter());
        }
    }

    /**
     * Gets called on clicking the add-item button, puts the item to the shopping cart
     * Sends JSON object to javascript to refresh sum of cart and number of items
     *
     * @param request it's a server request
     * @param response it's a response object
     * @throws IOException sometimes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String productId = request.getParameter("id");
        ShoppingCart shoppingCart = getShoppingCart(request);

        shoppingCart.addItem(Integer.parseInt(productId));

        float priceSum = shoppingCart.sumCart();
        int numberOfItems = shoppingCart.getNumberOfItems();

        JSONObject json = new JSONObject();
        json.put("priceSum", priceSum);
        json.put("numberOfItems", numberOfItems);

        response.setContentType("application/json");
        response.getWriter().print(json);
    }

    private ShoppingCart getShoppingCart(HttpServletRequest request) {
        HttpSession session;
        session = request.getSession();
        if (session.isNew()) {
            session.setAttribute("UserObject", new User());
        }
        User user = (User) session.getAttribute("UserObject");
        return user.shoppingCart;
    }

}
