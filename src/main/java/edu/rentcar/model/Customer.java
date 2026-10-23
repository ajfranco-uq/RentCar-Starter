package edu.rentcar.model;

public class Customer {
    private final String id;
    private final String fullName;
    private final String email;
    private final boolean authorized;
    private final String paymentToken;

    public Customer(String id, String fullName, String email, boolean authorized) {
        this(id, fullName, email, authorized, "tok_approved");
    }

    public Customer(String id, String fullName, String email, boolean authorized, String paymentToken) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.authorized = authorized;
        this.paymentToken = paymentToken;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isAuthorized() { return authorized; }
    public String getPaymentToken() { return paymentToken; }

    @Override
    public String toString() {
        return fullName + " (" + id + ")";
    }
}
