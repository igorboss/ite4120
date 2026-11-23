package ee.taltech.ite4120.lostfound.service;

import ee.taltech.ite4120.lostfound.dto.ItemReportQueryParams;
import ee.taltech.ite4120.lostfound.model.ItemReport;
import ee.taltech.ite4120.lostfound.model.ReferenceData;
import ee.taltech.ite4120.lostfound.repository.ItemReportRepository;
import java.time.LocalDate;
import java.util.List;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.NotFoundException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business rules for citizen-submitted reports. */
@Service
public class ItemReportService {
    private final ItemReportRepository repository;
    public ItemReportService(ItemReportRepository repository) { this.repository = repository; }
    public QueryResult<ItemReport> search(ItemReportQueryParams p) { return repository.search(p); }
    public ItemReport require(Long id) {
        ItemReport r = repository.load(id); if (r == null) throw new NotFoundException("report " + id + " not found"); return r;
    }
    @Transactional
    public ItemReport create(ItemReport r) {
        if (r.getReportDate().isAfter(LocalDate.now())) throw new ApiClientException("reportDate must not be in the future");
        if (!repository.categoryExists(r.getCategoryCode())) throw new ApiClientException("unknown categoryCode: " + r.getCategoryCode());
        if (!repository.locationExists(r.getLocationCode())) throw new ApiClientException("unknown locationCode: " + r.getLocationCode());
        return require(repository.insert(r));
    }
    public List<ReferenceData> categories() { return repository.categories(); }
    public List<ReferenceData> locations() { return repository.locations(); }
}
