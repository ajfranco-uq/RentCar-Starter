
package edu.rentcar.pricing.factory;

public final class StandardRentalFactory implements RentalModeFactory {

    @Override
    public String getModeId() {
        return "STANDARD";
    }

    @Override
    public String getDisplayName() {
        return "Standard";
    }

    @Override
    public Coverage createCoverage() {
        return new StandardCoverage();
    }

    @Override
    public Assistance createAssistance() {
        return new StandardAssistance();
    }

    @Override
    public RentalConditions createRentalConditions() {
        return new StandardRentalConditions();
    }
}
