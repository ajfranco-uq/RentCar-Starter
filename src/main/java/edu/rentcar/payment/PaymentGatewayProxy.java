package edu.rentcar.payment;

import edu.rentcar.model.Customer;
import edu.rentcar.service.VerificationService;
import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Protection Proxy: verifies customer access before delegating to the real payment gateway.
 * The facade also verifies the customer before creating a reservation; this second check
 * protects the payment gateway if another component invokes it directly.
 */
public final class PaymentGatewayProxy implements PaymentGateway {
    private final Function<String, Customer> customerLookup;
    private final PaymentGateway realGateway;
    private final VerificationService verificationService;

    public PaymentGatewayProxy(Function<String, Customer> customerLookup,
                               PaymentGateway realGateway,
                               VerificationService verificationService) {
        this.customerLookup = customerLookup;
        this.realGateway = realGateway;
        this.verificationService = verificationService;
    }

    @Override
    public PaymentStatus pay(String reservationId, String customerId,
                             BigDecimal amount, String paymentToken) {
        Customer customer = customerLookup.apply(customerId);
        if (customer == null || !customer.isAuthorized()) {
            return PaymentStatus.BLOCKED;
        }

        try {
            verificationService.verifyCustomer(customer);
        } catch (IllegalArgumentException exception) {
            // A blocked customer must never reach the real payment gateway.
            return PaymentStatus.BLOCKED;
        }

        return realGateway.pay(reservationId, customerId, amount, paymentToken);
    }
}
