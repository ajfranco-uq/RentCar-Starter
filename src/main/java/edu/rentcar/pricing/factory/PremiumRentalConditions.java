
package edu.rentcar.pricing.factory;

import java.math.BigDecimal;

public final class PremiumRentalConditions implements RentalConditions {

    @Override
    public BigDecimal getPriceMultiplier() {
        return new BigDecimal("1.35");
    }
}
