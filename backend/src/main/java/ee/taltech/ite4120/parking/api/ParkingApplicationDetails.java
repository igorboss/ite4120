package ee.taltech.ite4120.parking.api;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import ee.taltech.ite4120.parking.model.ParkingApplication;
import ee.taltech.ite4120.parking.model.ParkingDecision;
import ee.taltech.ite4120.parking.model.ParkingPermit;

public class ParkingApplicationDetails {

    @JsonUnwrapped
    private final ParkingApplication application;
    private final ParkingDecision decision;
    private final ParkingPermit permit;

    public ParkingApplicationDetails(
            ParkingApplication application,
            ParkingDecision decision,
            ParkingPermit permit) {
        this.application = application;
        this.decision = decision;
        this.permit = permit;
    }

    public ParkingApplication getApplication() { return application; }
    public ParkingDecision getDecision() { return decision; }
    public ParkingPermit getPermit() { return permit; }
}
