package edu.rentcar.pricing;

import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.Vehicle;
import edu.rentcar.pricing.factory.RentalModeFactory;
import edu.rentcar.pricing.decorator.DecoratedRentService;
import edu.rentcar.pricing.decorator.IndividualService;
import edu.rentcar.pricing.decorator.RentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public final class RentalPriceCalculator {
    private final RentalFactoryProvider factoryProvider = new RentalFactoryProvider();

    public BigDecimal calculate(RentalRequest request, Vehicle vehicle) {
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days < 1) throw new IllegalArgumentException("Reservation must be at least one day.");

        RentalModeFactory factory = factoryProvider.getFactory(request.getMode().getId());
        BigDecimal daily = BigDecimal.valueOf(vehicle.getDailyRate())
            .multiply(factory.createRentalConditions().getPriceMultiplier())
            .add(factory.createCoverage().getDailyCost())
            .add(factory.createAssistance().getDailyCost());

        RentService decorated = new IndividualService("rental-base", "Rental", daily);
        Set<String> applied = new HashSet<>();
        for (RentService service : request.getServices()) {
            Set<String> ids = service.getIncludedServiceIds();
            for (String id : ids) {
                if (applied.contains(id)) {
                    throw new IllegalArgumentException("Duplicate service detected: " + service.getName());
                }
            }
            applied.addAll(ids);
            decorated = new DecoratedRentService(decorated, service);
        }
        return decorated.getDailyCost().multiply(BigDecimal.valueOf(days))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
