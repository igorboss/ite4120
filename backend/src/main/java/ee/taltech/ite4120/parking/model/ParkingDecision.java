package ee.taltech.ite4120.parking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class ParkingDecision {

    private Long id;

    @JsonProperty("application_id")
    private Long applicationId;

    @JsonProperty("decision_date")
    private LocalDate decisionDate;

    @JsonProperty("decision_type")
    private String decisionType;

    @JsonProperty("decision_reason")
    private String decisionReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public LocalDate getDecisionDate() { return decisionDate; }
    public void setDecisionDate(LocalDate decisionDate) { this.decisionDate = decisionDate; }
    public String getDecisionType() { return decisionType; }
    public void setDecisionType(String decisionType) { this.decisionType = decisionType; }
    public String getDecisionReason() { return decisionReason; }
    public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }
}
