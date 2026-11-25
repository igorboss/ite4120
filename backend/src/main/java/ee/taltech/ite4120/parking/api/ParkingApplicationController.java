
package ee.taltech.ite4120.parking.api;

import ee.taltech.ite4120.parking.service.ParkingService;
import ee.taltech.ite4120.parking.model.ParkingPermit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permit-applications")
@Tag(
    name = "Parking",
    description = "Accessible parking permit applications"
)
public class ParkingApplicationController {

    private final ParkingService service;

    public ParkingApplicationController(ParkingService service) {
        this.service = service;
    }

    @Operation(summary = "Create a parking permit application")
    @ApiResponse(responseCode = "201", description = "Application created")
    @ApiResponse(responseCode = "400", description = "Invalid application data")
    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody ParkingApplicationRequest body) {
        Long applicationId = service.createApplication(
                body.getPersonalCode(),
                body.getApplicationDate(),
                body.getReason());
        return ResponseEntity
                .created(URI.create("/api/v1/permit-applications/" + applicationId))
                .build();
    }

    @Operation(summary = "List parking permit applications")
    @ApiResponse(responseCode = "200", description = "Paged application list")
    @ApiResponse(responseCode = "400", description = "Invalid filter, pagination, or sort value")
    @GetMapping
    public ParkingApplicationListResponse search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate applicationDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate applicationDateTo,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(required = false) List<String> sort) {
        ParkingApplicationQueryParams params =
                new ParkingApplicationQueryParams();
        params.setStatus(status);
        params.setApplicationDateFrom(applicationDateFrom);
        params.setApplicationDateTo(applicationDateTo);
        params.limit(limit);
        params.offset(offset);
        params.sort(sort);
        return new ParkingApplicationListResponse(
                service.searchApplications(params),
                params.getLimit(),
                params.getOffset(),
                service.countApplications(params));
    }

    @Operation(summary = "Get parking permit application details")
    @ApiResponse(responseCode = "200", description = "Application details")
    @ApiResponse(responseCode = "404", description = "Application not found")
    @GetMapping("/{id}")
    public ParkingApplicationDetails byId(@PathVariable Long id) {
        return new ParkingApplicationDetails(
                service.requireApplication(id),
                service.loadDecision(id),
                service.loadPermitForApplication(id));
    }

    @Operation(summary = "Record a parking permit application decision")
    @ApiResponse(responseCode = "201", description = "Decision recorded")
    @ApiResponse(responseCode = "400", description = "Invalid decision data")
    @ApiResponse(responseCode = "404", description = "Application not found")
    @ApiResponse(responseCode = "409", description = "Application already has a decision")
    @PostMapping("/{id}/decision")
    public ResponseEntity<Void> recordDecision(
            @PathVariable Long id,
            @Valid @RequestBody ParkingDecisionRequest body) {

        service.recordDecision(
                id,
                body.getDecisionDate(),
                body.getDecisionType(),
                body.getDecisionReason());

        return ResponseEntity
                .created(URI.create("/api/v1/permit-applications/" + id))
                .build();
    }

    @Operation(summary = "Issue a parking permit for an approved application")
    @ApiResponse(responseCode = "201", description = "Permit issued")
    @ApiResponse(responseCode = "400", description = "Invalid permit data")
    @ApiResponse(responseCode = "404", description = "Application not found")
    @ApiResponse(responseCode = "409", description = "Application is not approved or already has a permit")
    @PostMapping("/{id}/permits")
    public ResponseEntity<Void> issuePermit(
            @PathVariable Long id,
            @Valid @RequestBody ParkingPermitRequest body) {
        ParkingPermit permit = service.issuePermit(
                id,
                body.getValidFrom(),
                body.getValidUntil());
        return ResponseEntity
                .created(URI.create("/api/v1/parking-permits/" + permit.getId()))
                .build();
    }
}
