
package edu.rentcar.pricing.factory;

import java.math.BigDecimal;

public final class StandardRentalConditions implements RentalConditions {

    @Override
    public BigDecimal getPriceMultiplier() {
        return new BigDecimal("1.15");
    }
}
