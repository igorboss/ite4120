package ee.taltech.ite4120.lostfound.repository;

import ee.taltech.ite4120.lostfound.dto.ItemReportQueryParams;
import ee.taltech.ite4120.lostfound.model.ItemReport;
import ee.taltech.ite4120.lostfound.model.ReferenceData;
import java.util.List;
import java.util.Map;
import org.helex.commons.db.bean.PgBeanProcessor;
import org.helex.commons.db.repo.BaseRepository;
import org.helex.commons.db.sql.SqlBuilder;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Repository;

/** PostgreSQL access using the platform BaseRepository and SqlBuilder conventions. */
@Repository
public class ItemReportRepository extends BaseRepository {
    private static final String SELECT = "select r.*, c.name as category_name, l.name as location_name, l.address as location_address "
            + "from lostfound.item_report r join lostfound.category c on c.code = r.category_code "
            + "join lostfound.location l on l.code = r.location_code ";
    private static final Map<String, String> SORT = Map.of("itemName", "r.item_name", "reportDate", "r.report_date");
    private final PgBeanProcessor bp = new PgBeanProcessor(ItemReport.class);
    private final PgBeanProcessor refBp = new PgBeanProcessor(ReferenceData.class);

    public ItemReport load(Long id) { return getBean(SELECT + "where r.id = ? and r.sys_status = 'A'", bp, id); }
    public QueryResult<ItemReport> search(ItemReportQueryParams p) { return query(p, this::count, this::list); }
    private Integer count(ItemReportQueryParams p) {
        SqlBuilder sb = new SqlBuilder("select count(1) from lostfound.item_report r where r.sys_status = 'A'");
        sb.append(filter(p)); return queryForObject(sb.getSql(), Integer.class, sb.getParams());
    }
    private List<ItemReport> list(ItemReportQueryParams p) {
        SqlBuilder sb = new SqlBuilder(SELECT + "where r.sys_status = 'A'"); sb.append(filter(p));
        try { sb.append(order(p, SORT)); } catch (IllegalArgumentException e) {
            throw new ApiClientException("unknown sort key; sortable: itemName, reportDate");
        }
        sb.append(limit(p)); return getBeans(sb.getSql(), bp, sb.getParams());
    }
    private SqlBuilder filter(ItemReportQueryParams p) {
        SqlBuilder sb = new SqlBuilder();
        if (p.getTextContains() != null && !p.getTextContains().isBlank()) {
            String like = "%" + p.getTextContains().toLowerCase() + "%";
            sb.and("(lower(r.item_name) like ? or lower(r.description) like ?)", like, like);
        }
        if (p.getReportType() != null && !p.getReportType().isBlank()) sb.and("r.report_type = ?", p.getReportType());
        if (p.getCategoryCode() != null && !p.getCategoryCode().isBlank()) sb.and("r.category_code = ?", p.getCategoryCode());
        if (p.getLocationCode() != null && !p.getLocationCode().isBlank()) sb.and("r.location_code = ?", p.getLocationCode());
        return sb;
    }
    public Long insert(ItemReport r) {
        return queryForObject("insert into lostfound.item_report (report_type, item_name, description, report_date, category_code, location_code) values (?, ?, ?, ?, ?, ?) returning id",
                Long.class, r.getReportType(), r.getItemName(), r.getDescription(), r.getReportDate(), r.getCategoryCode(), r.getLocationCode());
    }
    public boolean categoryExists(String code) { return exists("select count(1) from lostfound.category where code = ?", code); }
    public boolean locationExists(String code) { return exists("select count(1) from lostfound.location where code = ?", code); }
    private boolean exists(String sql, String code) { Integer n = queryForObject(sql, Integer.class, code); return n != null && n > 0; }
    public List<ReferenceData> categories() { return getBeans("select code, name from lostfound.category order by name", refBp); }
    public List<ReferenceData> locations() { return getBeans("select code, name, address from lostfound.location order by name", refBp); }
}
