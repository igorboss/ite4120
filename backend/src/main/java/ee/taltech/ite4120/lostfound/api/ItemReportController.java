package ee.taltech.ite4120.lostfound.api;

import ee.taltech.ite4120.lostfound.dto.ItemReportQueryParams;
import ee.taltech.ite4120.lostfound.model.ItemReport;
import ee.taltech.ite4120.lostfound.model.ReferenceData;
import ee.taltech.ite4120.lostfound.registry.CollectionPoint;
import ee.taltech.ite4120.lostfound.registry.CollectionPointRegistryAdapter;
import ee.taltech.ite4120.lostfound.registry.CollectionPointRegistryException;
import ee.taltech.ite4120.lostfound.service.ItemReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.helex.commons.model.QueryResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/lost-found")
@Tag(name = "Lost and Found", description = "Parma municipal lost and found registry")
public class ItemReportController {
    private final ItemReportService service;
    private final CollectionPointRegistryAdapter registry;
    public ItemReportController(ItemReportService service, CollectionPointRegistryAdapter registry) { this.service = service; this.registry = registry; }
    @Operation(summary = "Search lost and found reports")
    @GetMapping("/reports") public QueryResult<ItemReport> search(@ModelAttribute ItemReportQueryParams params) { return service.search(params); }
    @GetMapping("/reports/{id}") public ItemReport byId(@PathVariable Long id) { return service.require(id); }
    @Operation(summary = "Create a lost or found report")
    @PostMapping("/reports") public ResponseEntity<ItemReport> create(@Valid @RequestBody ItemReportRequest body) {
        ItemReport saved = service.create(body.toModel()); return ResponseEntity.created(URI.create("/api/lost-found/reports/" + saved.getId())).body(saved);
    }
    @GetMapping("/categories") public List<ReferenceData> categories() { return service.categories(); }
    @GetMapping("/locations") public List<ReferenceData> locations() { return service.locations(); }
    @Operation(summary = "Retrieve collection points from the external registry (mock/X-Road adapter)")
    @GetMapping("/collection-points") public List<CollectionPoint> collectionPoints() {
        try { return registry.findAll(); }
        catch (CollectionPointRegistryException e) { throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "External collection-point registry is unavailable", e); }
    }
}
