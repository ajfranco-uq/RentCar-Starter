package edu.rentcar.pricing.decorator;

import edu.rentcar.pricing.decorator.RentService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Decorator used to add selected services to a rental price.
 */
public final class DecoratedRentService implements RentService {
    private final RentService base;
    private final RentService extra;

    public DecoratedRentService(RentService base, RentService extra) {
        this.base = base;
        this.extra = extra;
    }

    @Override
    public String getId() {
        return base.getId() + "+" + extra.getId();
    }

    @Override
    public String getName() {
        return base.getName() + ", " + extra.getName();
    }

    @Override
    public BigDecimal getDailyCost() {
        return base.getDailyCost().add(extra.getDailyCost());
    }

    @Override
    public Set<String> getIncludedServiceIds() {
        Set<String> ids = new HashSet<>(base.getIncludedServiceIds());
        ids.addAll(extra.getIncludedServiceIds());
        return ids;
    }
}
