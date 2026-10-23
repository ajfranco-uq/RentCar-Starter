package edu.rentcar.pricing.decorator;

import edu.rentcar.pricing.decorator.RentService;

import java.math.BigDecimal;
import java.util.Set;

public final class IndividualService implements RentService {
    private final String id;
    private final String name;
    private final BigDecimal dailyCost;

    public IndividualService(String id, String name, BigDecimal dailyCost) {
        this.id = id;
        this.name = name;
        this.dailyCost = dailyCost;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getDailyCost() {
        return dailyCost;
    }

    @Override
    public Set<String> getIncludedServiceIds() {
        return Set.of(id);
    }

    @Override
    public String toString() {
        return name + " (+" + dailyCost.toPlainString() + "/day)";
    }
}
