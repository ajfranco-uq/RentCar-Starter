
package edu.rentcar.pricing.factory;

public final class PremiumRentalFactory implements RentalModeFactory {

    @Override
    public String getModeId() {
        return "PREMIUM";
    }

    @Override
    public String getDisplayName() {
        return "Premium";
    }

    @Override
    public Coverage createCoverage() {
        return new PremiumCoverage();
    }

    @Override
    public Assistance createAssistance() {
        return new PremiumAssistance();
    }

    @Override
    public RentalConditions createRentalConditions() {
        return new PremiumRentalConditions();
    }
}
