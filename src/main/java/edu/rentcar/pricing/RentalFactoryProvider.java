package edu.rentcar.pricing;

import edu.rentcar.model.RentalMode;
import edu.rentcar.pricing.factory.RentalModeFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RentalFactoryProvider {

    private final Map<String, RentalModeFactory> factories;

    public RentalFactoryProvider() {
        this.factories = ServiceLoader
                .load(RentalModeFactory.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toUnmodifiableMap(
                        factory -> normalize(factory.getModeId()),
                        Function.identity()
                ));
        if (factories.isEmpty()) {
            throw new IllegalStateException("No rental mode factories were discovered.");
        }
    }

    public RentalModeFactory getFactory(String modeId) {
        RentalModeFactory factory = factories.get(normalize(modeId));
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported rental mode: " + modeId);
        }
        return factory;
    }

    public List<RentalMode> getAvailableModes() {
        return factories.values().stream()
                .map(factory -> new RentalMode(factory.getModeId(), factory.getDisplayName()))
                .sorted(Comparator.comparing(RentalMode::getLabel))
                .toList();
    }

    private static String normalize(String modeId) {
        if (modeId == null || modeId.isBlank()) {
            throw new IllegalArgumentException("Rental mode ID is required.");
        }
        return modeId.trim().toUpperCase(java.util.Locale.ROOT);
    }
}
