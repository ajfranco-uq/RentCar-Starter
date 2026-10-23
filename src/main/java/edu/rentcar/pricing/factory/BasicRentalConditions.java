
package edu.rentcar.pricing.factory;

import java.math.BigDecimal;

public final class BasicRentalConditions implements RentalConditions {

    @Override
    public BigDecimal getPriceMultiplier() {
        return BigDecimal.ONE;
    }
}
