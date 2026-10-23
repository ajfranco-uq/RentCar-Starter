
package edu.rentcar.pricing.factory;

import java.math.BigDecimal;

public final class PremiumCoverage implements Coverage {

    @Override
    public BigDecimal getDailyCost() {
        return new BigDecimal("35000");
    }
}
