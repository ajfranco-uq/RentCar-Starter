package edu.rentcar.pricing.factory;

public interface RentalModeFactory {

    String getModeId();

    String getDisplayName();

    Coverage createCoverage();

    Assistance createAssistance();

    RentalConditions createRentalConditions();
}
