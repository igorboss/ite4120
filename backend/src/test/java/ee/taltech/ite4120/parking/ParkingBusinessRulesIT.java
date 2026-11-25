package ee.taltech.ite4120.parking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ee.taltech.ite4120.parking.model.ParkingPermit;
import ee.taltech.ite4120.parking.repository.ParkingRepository;
import ee.taltech.ite4120.parking.service.ParkingService;
import java.time.LocalDate;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.ConflictException;
import org.helex.commons.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Executable business tests for the Accessible Parking Permit Register.
 * These tests originate from the plain-language business tests in the
 * parking specification.
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ParkingBusinessRulesIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    ParkingService service;

    @Autowired
    ParkingRepository repository;
    @Autowired
MockMvc mockMvc;
@Test
void anInvalidPersonalCodeIsRejected() throws Exception {
    // Given an applicant submits an invalid personal identity code
    String requestBody = """
            {
                "personal_code": "123",
                "application_date": "2026-11-21"
            }
            """;

    // When the application is submitted through the API
    // Then the API rejects it with HTTP 400 Bad Request
    mockMvc.perform(post("/api/v1/permit-applications")
            .header("Authorization", "Bearer superadmin")
            .contentType("application/json")
            .content(requestBody))
            .andExpect(status().isBadRequest());
}
        @Test
    void aParkingApplicationCanBeCreatedWithoutAReason() {
        // Given an applicant provides the required details only
        String personalCode = "39001010023";
        LocalDate applicationDate = LocalDate.now();

        // When the application is submitted without a reason
        Long applicationId = service.createApplication(
                personalCode,
                applicationDate);

        // Then an application is created
        assertThat(applicationId).isNotNull();
        assertThat(repository.getApplicationReason(applicationId)).isNull();
    }
    @Test
    void aParkingApplicationCanBeCreatedWithAReason() {
        // Given an applicant provides their details and a reason
        String personalCode = "39001010015";
        LocalDate applicationDate = LocalDate.now();
        String reason = "Applicant requires accessible parking";

        // When the application is submitted
        Long applicationId = service.createApplication(
                personalCode,
                applicationDate,
                reason);

        // Then an application is created
        assertThat(applicationId).isNotNull();
        assertThat(repository.getApplicationReason(applicationId)).isEqualTo(reason);
    }
    @Test
    void aNewParkingApplicationHasReceivedStatus() {
        // Given an applicant provides the required application details
        String personalCode = "39001010031";
        LocalDate applicationDate = LocalDate.now();

        // When a new parking permit application is created
        Long applicationId = service.createApplication(
                personalCode,
                applicationDate);

        // Then the application starts in RECEIVED status
        assertThat(applicationId).isNotNull();
        assertThat(repository.getApplicationStatus(applicationId))
                .isEqualTo("RECEIVED");
    }

    @Test
    void aParkingPermitCanBeIssuedForAnApprovedApplication() {
        // Given a person has a permit application with an APPROVED decision
        Long applicationId = service.createApprovedApplication(
                "39001010015",
                LocalDate.now());

        // When the municipal officer issues a parking permit for that person
        ParkingPermit permit = service.issuePermit(
                applicationId,
                "PARK-IT-001",
                LocalDate.now(),
                LocalDate.now().plusYears(1));

        // Then a parking permit is created with a unique permit number,
        // validity period, and ACTIVE status
        assertThat(permit.getId()).isNotNull();
        assertThat(permit.getApplicationId()).isEqualTo(applicationId);
        assertThat(permit.getPermitNumber()).isEqualTo("PARK-IT-001");
        assertThat(permit.getValidFrom()).isEqualTo(LocalDate.now());
        assertThat(permit.getValidUntil()).isEqualTo(LocalDate.now().plusYears(1));
        assertThat(permit.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void aParkingPermitCannotBeIssuedWithoutAnApprovedDecision() {
        // Given a person has a permit application without an APPROVED decision
        Long applicationId = service.createApplication(
                "39001010023",
                LocalDate.now());

        // When the municipal officer tries to issue a parking permit
        // Then the request is rejected and no parking permit is created
        assertThatThrownBy(() -> service.issuePermit(
                applicationId,
                "PARK-IT-002",
                LocalDate.now(),
                LocalDate.now().plusYears(1)))
                .isInstanceOf(ConflictException.class);

        assertThat(repository.countPermitsForApplication(applicationId))
                .isZero();
    }

    @Test
    void anApplicationListIsPaged() throws Exception {
        service.createApplication("39001010023", LocalDate.now(), "List test");

        mockMvc.perform(get("/api/v1/permit-applications")
                .header("Authorization", "Bearer superadmin")
                .param("limit", "5")
                .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.limit").value(5))
                .andExpect(jsonPath("$.meta.offset").value(0))
                .andExpect(jsonPath("$.meta.total").isNumber());
    }

    @Test
    void anApplicationListRejectsInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/v1/permit-applications")
                .header("Authorization", "Bearer superadmin")
                .param("limit", "101")
                .param("offset", "-1")
                .param("sort", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aParkingPermitCanBeIssuedWithAGeneratedNumber() {
        Long applicationId = service.createApprovedApplication(
                "39001010039",
                LocalDate.now());

        ParkingPermit permit = service.issuePermit(
                applicationId,
                LocalDate.now(),
                LocalDate.now().plusYears(1));

        assertThat(permit.getPermitNumber())
                .matches("PARK-[A-Z0-9]{12}");
    }

    @Test
    void aReversedPermitValidityPeriodIsRejected() {
        Long applicationId = service.createApprovedApplication(
                "39001010047",
                LocalDate.now());

        assertThatThrownBy(() -> service.issuePermit(
                applicationId,
                LocalDate.now().plusDays(1),
                LocalDate.now()))
                .isInstanceOf(ApiClientException.class);

        assertThat(repository.countPermitsForApplication(applicationId)).isZero();
    }

    @Test
    void anApplicationCannotReceiveASecondPermit() {
        Long applicationId = service.createApprovedApplication(
                "39001010055",
                LocalDate.now());

        service.issuePermit(
                applicationId,
                LocalDate.now(),
                LocalDate.now().plusYears(1));

        assertThatThrownBy(() -> service.issuePermit(
                applicationId,
                LocalDate.now(),
                LocalDate.now().plusYears(1)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void permitDetailsAreAvailableThroughTheService() {
        Long applicationId = service.createApprovedApplication(
                "39001010063",
                LocalDate.now());
        ParkingPermit issued = service.issuePermit(
                applicationId,
                LocalDate.now(),
                LocalDate.now().plusYears(1));

        assertThat(service.requirePermit(issued.getId()).getPermitNumber())
                .isEqualTo(issued.getPermitNumber());
    }

    @Test
    void aMissingPermitIsRejected() {
        assertThatThrownBy(() -> service.requirePermit(999999999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void permitEndpointRejectsAnApplicationWithoutApproval() throws Exception {
        Long applicationId = service.createApplication(
                "39001010071",
                LocalDate.now());

        mockMvc.perform(post("/api/v1/permit-applications/" + applicationId + "/permits")
                .header("Authorization", "Bearer superadmin")
                .contentType("application/json")
                .content("""
                        {
                            "valid_from": "2026-11-23",
                            "valid_until": "2027-11-23"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict with current state"));
    }

    @Test
    void permitEndpointRejectsReversedValidityPeriod() throws Exception {
        Long applicationId = service.createApprovedApplication(
                "39001010089",
                LocalDate.now());

        mockMvc.perform(post("/api/v1/permit-applications/" + applicationId + "/permits")
                .header("Authorization", "Bearer superadmin")
                .contentType("application/json")
                .content("""
                        {
                            "valid_from": "2026-11-24",
                            "valid_until": "2026-11-23"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"));
    }

    @Test
    void permitEndpointReturnsCreatedLocation() throws Exception {
        Long applicationId = service.createApprovedApplication(
                "39001010097",
                LocalDate.now());

        mockMvc.perform(post("/api/v1/permit-applications/" + applicationId + "/permits")
                .header("Authorization", "Bearer superadmin")
                .contentType("application/json")
                .content("""
                        {
                            "valid_from": "2026-11-23",
                            "valid_until": "2027-11-23"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void missingApplicationDetailsAreNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/permit-applications/999999999")
                .header("Authorization", "Bearer superadmin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not found"));
    }

    @Test
    void missingPermitDetailsAreNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/parking-permits/999999999")
                .header("Authorization", "Bearer superadmin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not found"));
    }

    @Test
    void anApprovedDecisionChangesApplicationStatusToDecided() throws Exception {
        // Given a newly received application
        Long applicationId = service.createApplication(
                "39001010015",
                LocalDate.now(),
                "Accessible parking required");

        assertThat(repository.getApplicationStatus(applicationId))
                .isEqualTo("RECEIVED");

        // When an officer approves the application through the API
        mockMvc.perform(post("/api/v1/permit-applications/" + applicationId + "/decision")
                .header("Authorization", "Bearer superadmin")
                .contentType("application/json")
                .content("""
                        {
                            "decision_date": "2026-11-25",
                            "decision_type": "APPROVED",
                            "decision_reason": "Eligibility requirements met"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/v1/permit-applications/" + applicationId));

        // Then the decision is stored and the application is DECIDED
        assertThat(repository.getApplicationStatus(applicationId))
                .isEqualTo("DECIDED");

        assertThat(repository.loadDecision(applicationId).getDecisionType())
                .isEqualTo("APPROVED");

        assertThat(repository.loadDecision(applicationId).getDecisionReason())
                .isEqualTo("Eligibility requirements met");
    }


    @Test
    void aRejectedDecisionChangesApplicationStatusToDecided() throws Exception {
        Long applicationId = service.createApplication(
                "39001010023",
                LocalDate.now(),
                "Accessible parking requested");

        assertThat(repository.getApplicationStatus(applicationId))
                .isEqualTo("RECEIVED");

        mockMvc.perform(post("/api/v1/permit-applications/" + applicationId + "/decision")
                .header("Authorization", "Bearer superadmin")
                .contentType("application/json")
                .content("""
                        {
                            "decision_date": "2026-11-25",
                            "decision_type": "REJECTED",
                            "decision_reason": "Eligibility requirements not met"
                        }
                        """))
                .andExpect(status().isCreated());

        assertThat(repository.getApplicationStatus(applicationId))
                .isEqualTo("DECIDED");

        assertThat(repository.loadDecision(applicationId).getDecisionType())
                .isEqualTo("REJECTED");

        assertThat(repository.loadDecision(applicationId).getDecisionReason())
                .isEqualTo("Eligibility requirements not met");
    }


    @Test
    void aSecondDecisionForTheSameApplicationIsRejected() throws Exception {
        Long applicationId = service.createApplication(
                "39001010031",
                LocalDate.now());

        String approvedDecision = """
                {
                    "decision_date": "2026-11-25",
                    "decision_type": "APPROVED",
                    "decision_reason": "Eligibility confirmed"
                }
                """;

        String rejectedDecision = """
                {
                    "decision_date": "2026-11-25",
                    "decision_type": "REJECTED",
                    "decision_reason": "Attempt to overwrite decision"
                }
                """;

        mockMvc.perform(post("/api/v1/permit-applications/" + applicationId + "/decision")
                .header("Authorization", "Bearer superadmin")
                .contentType("application/json")
                .content(approvedDecision))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/permit-applications/" + applicationId + "/decision")
                .header("Authorization", "Bearer superadmin")
                .contentType("application/json")
                .content(rejectedDecision))
                .andExpect(status().isConflict());

        assertThat(repository.loadDecision(applicationId).getDecisionType())
                .isEqualTo("APPROVED");

        assertThat(repository.getApplicationStatus(applicationId))
                .isEqualTo("DECIDED");
    }

}
