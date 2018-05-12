package com.codecool.shop.model;

public class Order {

    public enum Status {
        IN_PROGRESS,
        PAID
    }

    private String name;
    private String email;
    private Address billingAddress;
    private Address shippingAddress;
    private int phone;
    private Status status;
    private ShoppingCart orderShoppingCart;


    /**
     * Constructor
     * <p>All parameters needed</p>
     *
     * @param name user's name
     * @param email user's email
     * @param billingAddress user's billing address
     * @param shippingAddress user's shipping address
     * @param phone user's phone number
     * @param shoppingCart Shopping cart object with products
     */
    public Order(String name, String email, Address billingAddress, Address shippingAddress, int phone, ShoppingCart shoppingCart) {
        this.orderShoppingCart = shoppingCart;
        this.name = name;
        this.email = email;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.phone = phone;
        status = Status.IN_PROGRESS;
    }

    /**
     * Sets the order from IN_PROGRESS to PAID
     * <p>Used after user payment</p>
     */
    public void pay() {
        status = Status.PAID;
    }

    /**
     * @return name given with order
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name sets the name field a given string
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return email given with the order
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email a string that will modify the order's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return address given with the order (string)
     */
    public Address getBillingAddress() {
        return billingAddress;
    }

    /**
     * sets the bulling address
     * @param billingAddress billing address of user who made the order
     */
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }


    /**
     *
     * @return shipping address of the user who made the order
     */
    public Address getShippingAddress() {
        return shippingAddress;
    }

    /**
     * sets the order's shipping address
     * @param shippingAddress shipping address of the user
     */
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     *
     * @return user's phone number
     */
    public int getPhone() {
        return phone;
    }


    /**
     *
     * @param phone user's phone number
     */
    public void setPhone(int phone) {
        this.phone = phone;
    }

    /**
     *
     * @return status of the order (in_progress or paid)
     */
    public Status getStatus() {
        return status;
    }

}
