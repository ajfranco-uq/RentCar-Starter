package edu.rentcar.payment;
import java.math.BigDecimal;
public interface PaymentGateway {
    PaymentStatus pay(String reservationId, String customerId, BigDecimal amount, String paymentToken);
}
