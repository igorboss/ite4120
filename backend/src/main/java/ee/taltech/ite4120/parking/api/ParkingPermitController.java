package ee.taltech.ite4120.parking.api;

import ee.taltech.ite4120.parking.model.ParkingPermit;
import ee.taltech.ite4120.parking.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parking-permits")
@Tag(name = "Parking permits", description = "Issued accessible parking permits")
public class ParkingPermitController {

    private final ParkingService service;

    public ParkingPermitController(ParkingService service) {
        this.service = service;
    }

    @Operation(summary = "Get an issued parking permit")
    @ApiResponse(responseCode = "200", description = "Permit details")
    @ApiResponse(responseCode = "404", description = "Permit not found")
    @GetMapping("/{id}")
    public ParkingPermit byId(@PathVariable Long id) {
        return service.requirePermit(id);
    }
}
