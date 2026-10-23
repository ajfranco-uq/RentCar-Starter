package edu.rentcar.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Reservation {
    private final String id;
    private final String customerId;
    private final String vehicleId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private RentalMode mode;
    private BigDecimal total;
    private ReservationStatus status;
    private String reconciliationReason;

    public Reservation(String id, String customerId, String vehicleId, LocalDate startDate,
                       LocalDate endDate, RentalMode mode, BigDecimal total, ReservationStatus status) {
        this.id = id; this.customerId = customerId; this.vehicleId = vehicleId;
        this.startDate = startDate; this.endDate = endDate; this.mode = mode;
        this.total = total; this.status = status;
    }
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getVehicleId() { return vehicleId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public RentalMode getMode() { return mode; }
    public BigDecimal getTotal() { return total; }
    public void setDetails(RentalMode mode, BigDecimal total) {
        this.mode = mode;
        this.total = total;
    }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public String getReconciliationReason() { return reconciliationReason; }
    public void setReconciliationReason(String reason) { this.reconciliationReason = reason; }
}
