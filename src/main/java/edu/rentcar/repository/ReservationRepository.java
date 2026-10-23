package edu.rentcar.repository;

import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/** In-memory repository. Atomic hold creation protects this single JVM only. */
public final class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1001);

    public synchronized Reservation tryCreateHold(String customerId, String vehicleId,
            LocalDate start, LocalDate end) {
        for (Reservation current : reservations) {
            if (!current.getVehicleId().equals(vehicleId)) continue;
            ReservationStatus status = current.getStatus();
            if (status != ReservationStatus.PENDING && status != ReservationStatus.CONFIRMED
                    && status != ReservationStatus.PAYMENT_REVIEW) continue;
            boolean overlaps = start.isBefore(current.getEndDate())
                    && current.getStartDate().isBefore(end);
            if (overlaps) return null;
        }
        Reservation hold = new Reservation("RES-" + sequence.getAndIncrement(), customerId,
                vehicleId, start, end, null, java.math.BigDecimal.ZERO, ReservationStatus.PENDING);
        reservations.add(hold);
        return hold;
    }

    public synchronized void saveDetails(String id, edu.rentcar.model.RentalMode mode,
                                         java.math.BigDecimal total) {
        findRequired(id).setDetails(mode, total);
    }

    /** Removes a hold that failed before payment was attempted. */
    public synchronized void removeHold(String id) {
        reservations.removeIf(reservation -> reservation.getId().equals(id));
    }

    public synchronized void updateStatus(String id, ReservationStatus status) {
        findRequired(id).setStatus(status);
    }

    /** Releases an uncertain reservation only after an explicit reconciliation action. */
    public synchronized boolean releaseForReconciliation(String id, String reason) {
        Reservation reservation = findRequired(id);
        if (reservation.getStatus() != ReservationStatus.PAYMENT_REVIEW) {
            return false;
        }
        reservation.setReconciliationReason(reason.trim());
        reservation.setStatus(ReservationStatus.RELEASED);
        return true;
    }

    public synchronized List<Reservation> findByStatus(ReservationStatus status) {
        List<Reservation> matching = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == status) matching.add(reservation);
        }
        return List.copyOf(matching);
    }

    public synchronized Optional<Reservation> findById(String id) {
        return reservations.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public synchronized List<Reservation> findAll() { return List.copyOf(reservations); }

    private Reservation findRequired(String id) {
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(id)) return reservation;
        }
        throw new IllegalArgumentException("Reservation not found: " + id);
    }
}
