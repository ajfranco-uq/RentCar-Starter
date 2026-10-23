
package edu.rentcar.pricing.factory;

import java.math.BigDecimal;

public final class StandardAssistance implements Assistance {

    @Override
    public BigDecimal getDailyCost() {
        return new BigDecimal("8000");
    }
}
