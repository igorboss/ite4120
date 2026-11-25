package ee.taltech.ite4120.parking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class ParkingPermit {

    private Long id;

    @JsonProperty("application_id")
    private Long applicationId;

    @JsonProperty("permit_number")
    private String permitNumber;

    @JsonProperty("valid_from")
    private LocalDate validFrom;

    @JsonProperty("valid_until")
    private LocalDate validUntil;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
