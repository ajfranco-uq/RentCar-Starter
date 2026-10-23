package edu.rentcar.service;

import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationStatus;
import edu.rentcar.repository.ReservationRepository;
import java.util.List;

/** Coordinates manual release of reservations whose payment outcome is still uncertain.
 * This academic workflow records an operator decision; it does not prove that no charge occurred.
 */
public final class ReconciliationService {
    private final ReservationRepository repository;

    public ReconciliationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public List<Reservation> findPendingReview() {
        return repository.findByStatus(ReservationStatus.PAYMENT_REVIEW);
    }

    public void releaseReservation(String reservationId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter a reason for releasing this reservation.");
        }
        boolean released = repository.releaseForReconciliation(reservationId, reason);
        if (!released) {
            throw new IllegalStateException("Only reservations in PAYMENT_REVIEW can be released.");
        }
    }
}
