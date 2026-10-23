package edu.rentcar.facade;

import edu.rentcar.model.Customer;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationStatus;
import edu.rentcar.model.Vehicle;
import edu.rentcar.payment.PaymentGateway;
import edu.rentcar.payment.PaymentStatus;
import edu.rentcar.pricing.RentalPriceCalculator;
import edu.rentcar.repository.ReservationRepository;
import edu.rentcar.repository.VehicleRepository;
import edu.rentcar.service.AvailabilityService;
import java.math.BigDecimal;
import java.util.function.Function;

/** Facade coordinates validation, availability, pricing, payment and state transitions. */
public final class RentalFacade {
    private final Function<String, Customer> customerLookup;
    private final VehicleRepository vehicleRepository;
    private final AvailabilityService availabilityService;
    private final ReservationRepository reservationRepository;
    private final RentalPriceCalculator priceCalculator;
    private final PaymentGateway paymentGateway;

    public RentalFacade(Function<String, Customer> customerLookup,
                        VehicleRepository vehicleRepository,
                        AvailabilityService availabilityService,
                        ReservationRepository reservationRepository,
                        RentalPriceCalculator priceCalculator,
                        PaymentGateway paymentGateway) {
        this.customerLookup = customerLookup;
        this.vehicleRepository = vehicleRepository;
        this.availabilityService = availabilityService;
        this.reservationRepository = reservationRepository;
        this.priceCalculator = priceCalculator;
        this.paymentGateway = paymentGateway;
    }

    public RentalResult reserve(RentalRequest request) {
        Customer customer = customerLookup.apply(request.getCustomerId());
        if (customer == null) throw new IllegalArgumentException("Selected customer does not exist.");
        // Customer access verification is intentionally performed by PaymentGatewayProxy
        // when the user submits the reservation, immediately before the payment API call.
        // This allows blocked attempts to be recorded as FAILED in reservation history.

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId());
        if (vehicle == null) throw new IllegalArgumentException("Selected vehicle does not exist.");
        if (!"Available".equalsIgnoreCase(vehicle.getStatus())) {
            throw new IllegalArgumentException("This vehicle is not available for rental.");
        }

        // Validate service selection and calculate the total before creating a history record.
        // Invalid pricing requests must not leave FAILED reservations with a zero amount.
        BigDecimal total = priceCalculator.calculate(request, vehicle);

        // Check vehicle/date range before payment.
        Reservation hold = availabilityService.createHoldIfAvailable(request);
        if (hold == null) {
            return new RentalResult(null, null, BigDecimal.ZERO,
                    "The vehicle is already reserved for part of this period.");
        }

        try {
            // provisional PENDING reservation
            reservationRepository.saveDetails(hold.getId(), request.getMode(), total);
        } catch (RuntimeException ex) {
            // No payment has been attempted, so remove the incomplete hold entirely.
            reservationRepository.removeHold(hold.getId());
            throw ex;
        }

        PaymentStatus payment;
        try {
            //Payment verification and confirmation
            payment = paymentGateway.pay(hold.getId(), customer.getId(), total, request.getPaymentToken());
        } catch (RuntimeException exception) {
            // A runtime failure does not prove that the provider did not process the payment.
            payment = PaymentStatus.UNKNOWN;
        }
        if (payment == PaymentStatus.APPROVED) {
            reservationRepository.updateStatus(hold.getId(), ReservationStatus.CONFIRMED);
            availabilityService.releaseHold(hold.getId(), true);
            return new RentalResult(hold.getId(), ReservationStatus.CONFIRMED, total,
                    "Payment approved. Reservation confirmed.");
        }
        if (payment == PaymentStatus.BLOCKED) {
            reservationRepository.updateStatus(hold.getId(), ReservationStatus.FAILED);
            availabilityService.releaseHold(hold.getId(), false);
            return new RentalResult(hold.getId(), ReservationStatus.FAILED, total,
                    "Customer verification failed. The reservation was recorded as FAILED; no payment request was sent.");
        }
        if (payment == PaymentStatus.DECLINED) {
            reservationRepository.updateStatus(hold.getId(), ReservationStatus.FAILED);
            availabilityService.releaseHold(hold.getId(), false);
            return new RentalResult(hold.getId(), ReservationStatus.FAILED, total,
                    "Payment was declined. The reservation was not confirmed.");
        }

        // Do not release the hold or retry blindly; the provider may have processed the payment.
        reservationRepository.updateStatus(hold.getId(), ReservationStatus.PAYMENT_REVIEW);
        return new RentalResult(hold.getId(), ReservationStatus.PAYMENT_REVIEW, total,
                "Payment outcome is unknown. Reservation is held for manual reconciliation.");
    }
}
