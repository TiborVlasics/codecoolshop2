package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.Address;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.ShoppingCart;
import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    /**
     * Loads checkout template on request
     * @param request user's request happens when (s)he moves from shopping cart page
     * @param response gives back the template to the user
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(request.getServletContext());
        WebContext context = new WebContext(request, response, request.getServletContext());
        engine.process("checkout/checkout.html", context, response.getWriter());
    }

    /**
     * It processes the user's data and redirects to the payment page
     * @param request request by the user on hitting the payment button
     * @param response redirects to payment
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("inputName");
        String email = request.getParameter("inputEmail");
        Integer phone = Integer.parseInt(request.getParameter("inputPhoneNumber"));
        String shipAddress = request.getParameter("inputShipAddress");
        String shipCity = request.getParameter("inputShipCity");
        String shipCountry = request.getParameter("inputShipCountry");
        Integer shipZip = Integer.parseInt(request.getParameter("inputShipZip"));
        Address shipAddressObj = new Address(shipAddress, shipCity, shipCountry, shipZip);
        Address billAddressObj;
        boolean sameAddressCheck = request.getParameterMap().containsKey("sameAddressCheck");
        if (sameAddressCheck) {
            billAddressObj = new Address(shipAddress, shipCity, shipCountry, shipZip);
        } else {
            String billAddress = request.getParameter("inputBillAddress");
            String billCity = request.getParameter("inputBillCity");
            String billCountry = request.getParameter("inputBillCountry");
            Integer billZip = Integer.parseInt(request.getParameter("inputBillZip"));
            billAddressObj = new Address(billAddress, billCity, billCountry, billZip);
        }
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("UserObject");
        ShoppingCart shoppingCart = user.shoppingCart;
        Order order = new Order(name, email, billAddressObj, shipAddressObj, phone, shoppingCart);
        user.orders.add(order);
        response.sendRedirect("/payment");
    }
}
