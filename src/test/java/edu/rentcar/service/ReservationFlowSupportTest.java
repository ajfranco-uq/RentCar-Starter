package edu.rentcar.service;

import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationStatus;
import edu.rentcar.repository.ReservationRepository;
import edu.rentcar.pricing.decorator.IndividualService;
import edu.rentcar.pricing.composite.ServicePackage;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ReservationFlowSupportTest {
    @Test
    void holdPreventsOverlappingReservationUntilFailedOrConfirmed() {
        ReservationRepository repository = new ReservationRepository();
        Reservation first = repository.tryCreateHold("CUS-1", "VEH-1",
                LocalDate.of(2027, 1, 10), LocalDate.of(2027, 1, 15));
        assertNotNull(first);
        assertNull(repository.tryCreateHold("CUS-2", "VEH-1",
                LocalDate.of(2027, 1, 12), LocalDate.of(2027, 1, 17)));
        repository.updateStatus(first.getId(), ReservationStatus.FAILED);
        assertNotNull(repository.tryCreateHold("CUS-2", "VEH-1",
                LocalDate.of(2027, 1, 12), LocalDate.of(2027, 1, 17)));
    }

    @Test
    void adjacentDateIntervalsDoNotOverlap() {
        ReservationRepository repository = new ReservationRepository();
        assertNotNull(repository.tryCreateHold("CUS-1", "VEH-1",
                LocalDate.of(2027, 1, 10), LocalDate.of(2027, 1, 15)));
        assertNotNull(repository.tryCreateHold("CUS-2", "VEH-1",
                LocalDate.of(2027, 1, 15), LocalDate.of(2027, 1, 18)));
    }

    @Test
    void compositeRejectsDuplicateComponentsAndCycles() {
        IndividualService gps = new IndividualService("GPS", "GPS", new BigDecimal("5000"));
        ServicePackage pack = new ServicePackage("PACK", "Pack");
        pack.add(gps);
        assertThrows(IllegalArgumentException.class, () -> pack.add(gps));
        ServicePackage nested = new ServicePackage("NESTED", "Nested");
        nested.add(pack);
        assertThrows(IllegalArgumentException.class, () -> pack.add(nested));
    }
}
