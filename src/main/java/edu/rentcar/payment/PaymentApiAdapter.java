package edu.rentcar.payment;

import com.rentcar.paymentclient.PaymentApiClient;
import com.rentcar.paymentclient.PaymentApiRequest;
import com.rentcar.paymentclient.PaymentApiResponse;
import java.io.IOException;
import java.math.BigDecimal;

/** Adapter translates the application payment contract to payment-client.jar. */
public final class PaymentApiAdapter implements PaymentGateway {
    private final PaymentApiClient client;
    public PaymentApiAdapter(PaymentApiClient client) { this.client = client; }

    @Override
    public PaymentStatus pay(String reservationId, String customerId,
                             BigDecimal amount, String paymentToken) {
        try {
            PaymentApiRequest request = new PaymentApiRequest(
                    reservationId, customerId, amount.doubleValue(), paymentToken);
            PaymentApiResponse response = client.pay(request);
            if (response.isApproved()) return PaymentStatus.APPROVED;
            if (response.isDeclined()) return PaymentStatus.DECLINED;
            // HTTP 5xx or unrecognized response: payment outcome may be uncertain.
            return PaymentStatus.UNKNOWN;
        } catch (IOException exception) {
            return PaymentStatus.UNKNOWN;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return PaymentStatus.UNKNOWN;
        }
    }
}
