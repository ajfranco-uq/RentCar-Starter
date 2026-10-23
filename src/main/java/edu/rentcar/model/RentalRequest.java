package edu.rentcar.model;

import edu.rentcar.pricing.decorator.RentService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Immutable request built and validated at the UI boundary. */
public final class RentalRequest {
    private final String customerId;
    private final String vehicleId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final RentalMode mode;
    private final List<RentService> services;
    private final String paymentToken;

    private RentalRequest(Builder b) {
        customerId = b.customerId;
        vehicleId = b.vehicleId;
        startDate = b.startDate;
        endDate = b.endDate;
        mode = b.mode;
        services = List.copyOf(b.services);
        paymentToken = b.paymentToken;
    }

    public String getCustomerId() { return customerId; }
    public String getVehicleId() { return vehicleId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public RentalMode getMode() { return mode; }
    public List<RentService> getServices() { return services; }
    public String getPaymentToken() { return paymentToken; }

    public static class Builder {
        private String customerId;
        private String vehicleId;
        private LocalDate startDate;
        private LocalDate endDate;
        private RentalMode mode;
        private List<RentService> services = new ArrayList<>();
        private String paymentToken;

        public Builder customerId(String value) { customerId = value; return this; }
        public Builder vehicleId(String value) { vehicleId = value; return this; }
        public Builder dates(LocalDate start, LocalDate end) { startDate = start; endDate = end; return this; }
        public Builder mode(RentalMode value) { mode = value; return this; }
        public Builder services(List<RentService> value) { services = new ArrayList<>(value); return this; }
        public Builder paymentToken(String value) { paymentToken = value; return this; }

        public RentalRequest build() {
            requireText(customerId, "Customer");
            requireText(vehicleId, "Vehicle");
            requireText(paymentToken, "Payment token");
            if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
                throw new IllegalArgumentException("End date must be after start date.");
            }
            if (startDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Start date cannot be in the past.");
            }
            if (mode == null) throw new IllegalArgumentException("Select a rental mode.");
            return new RentalRequest(this);
        }

        private static void requireText(String value, String field) {
            if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required.");
        }
    }
}
