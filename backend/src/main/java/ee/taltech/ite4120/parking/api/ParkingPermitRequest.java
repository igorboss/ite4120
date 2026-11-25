package ee.taltech.ite4120.parking.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ParkingPermitRequest {

    @JsonProperty("valid_from")
    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;

    @JsonProperty("valid_until")
    @NotNull(message = "Valid until date is required")
    private LocalDate validUntil;

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
}
