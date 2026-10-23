package edu.rentcar.service;

import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.Reservation;
import edu.rentcar.repository.ReservationRepository;

public final class AvailabilityService {
    private final ReservationRepository repository;
    public AvailabilityService(ReservationRepository repository) { this.repository = repository; }

    public Reservation createHoldIfAvailable(RentalRequest request) {
        return repository.tryCreateHold(request.getCustomerId(), request.getVehicleId(),
                request.getStartDate(), request.getEndDate());
    }

    public void releaseHold(String reservationId, boolean confirmed) {
        if (!confirmed) repository.updateStatus(reservationId,
                edu.rentcar.model.ReservationStatus.FAILED);
    }
}
