package edu.rentcar.pricing.decorator;

import java.math.BigDecimal;
import java.util.Set;

public interface RentService {
    String getId();
    String getName();
    BigDecimal getDailyCost();
    Set<String> getIncludedServiceIds();
}
