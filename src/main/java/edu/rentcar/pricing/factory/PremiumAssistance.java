
package edu.rentcar.pricing.factory;

import java.math.BigDecimal;

public final class PremiumAssistance implements Assistance {

    @Override
    public BigDecimal getDailyCost() {
        return new BigDecimal("15000");
    }
}
