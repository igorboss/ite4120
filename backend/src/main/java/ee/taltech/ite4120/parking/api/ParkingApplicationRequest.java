
package ee.taltech.ite4120.parking.api;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ParkingApplicationRequest {

@JsonProperty("personal_code")
@NotBlank(message = "Personal identity code is required")
@Pattern(
    regexp = "\\d{11}",
    message = "Personal identity code must contain exactly 11 digits"
)
private String personalCode;

@JsonProperty("application_date")
@NotNull(message = "Application date is required")
private LocalDate applicationDate;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}