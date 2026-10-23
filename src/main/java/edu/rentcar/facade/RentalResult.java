package edu.rentcar.facade;

import edu.rentcar.model.ReservationStatus;
import java.math.BigDecimal;

public record RentalResult(String reservationId, ReservationStatus status,
                           BigDecimal total, String message) { }
