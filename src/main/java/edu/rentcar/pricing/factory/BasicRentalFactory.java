
package edu.rentcar.pricing.factory;

public final class BasicRentalFactory implements RentalModeFactory {

    @Override
    public String getModeId() {
        return "BASIC";
    }

    @Override
    public String getDisplayName() {
        return "Basic";
    }

    @Override
    public Coverage createCoverage() {
        return new BasicCoverage();
    }

    @Override
    public Assistance createAssistance() {
        return new BasicAssistance();
    }

    @Override
    public RentalConditions createRentalConditions() {
        return new BasicRentalConditions();
    }
}
