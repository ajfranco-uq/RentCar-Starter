package edu.rentcar.service;

import edu.rentcar.model.Customer;
import java.util.Set;

/** In-memory pre-payment verification. The blacklist is for demonstration only. */
public final class VerificationService {
    private final Set<String> blacklistedCustomerIds = Set.of("CUS-1003", "CUS-1006", "CUS-1009");

    public boolean isBlacklisted(String customerId) {
        return blacklistedCustomerIds.contains(customerId);
    }

    public void verifyCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Select a customer.");
        }
        if (isBlacklisted(customer.getId())) {
            throw new IllegalArgumentException(
                    "Reservation blocked: this customer is on the verification blacklist.");
        }
    }

    public Set<String> getBlacklistedCustomerIds() {
        return blacklistedCustomerIds;
    }
}
