package ee.taltech.ite4120.parking.repository;

import ee.taltech.ite4120.parking.api.ParkingApplicationQueryParams;
import ee.taltech.ite4120.parking.model.ParkingApplication;
import ee.taltech.ite4120.parking.model.ParkingDecision;
import ee.taltech.ite4120.parking.model.ParkingPermit;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.helex.commons.db.bean.PgBeanProcessor;
import org.helex.commons.db.repo.BaseRepository;
import org.helex.commons.db.sql.SqlBuilder;
import org.helex.commons.exception.ApiClientException;
import org.springframework.stereotype.Repository;

@Repository
public class ParkingRepository extends BaseRepository {

    private static final Map<String, String> APPLICATION_SORT = Map.of(
            "applicationDate", "pa.application_date",
            "status", "pa.status");

    private final PgBeanProcessor permitBp =
            new PgBeanProcessor(ParkingPermit.class);
    private final PgBeanProcessor applicationBp =
            new PgBeanProcessor(ParkingApplication.class);
    private final PgBeanProcessor decisionBp =
            new PgBeanProcessor(ParkingDecision.class);


public Long insertApplication(
        String personalCode,
        LocalDate applicationDate,
        String reason) {

    String sql = """
            insert into parking.permit_application
                (personal_code, application_date, reason)
            values (?, ?, ?)
            returning id
            """;

    return queryForObject(
            sql,
            Long.class,
            personalCode,
            applicationDate,
            reason);
}

public Long insertApplication(
        String personalCode,
        LocalDate applicationDate) {

    return insertApplication(personalCode, applicationDate, null);
}
    public String getApplicationReason(Long applicationId) {
        return queryForObject(
                """
                select reason
                  from parking.permit_application
                 where id = ?
                """,
                String.class,
                applicationId);
    }


    public String getApplicationStatus(Long applicationId) {
        return queryForObject(
                """
                select status
                  from parking.permit_application
                 where id = ?
                """,
                String.class,
                applicationId);
    }

    public ParkingApplication loadApplication(Long id) {
        return getBean(
                """
                select *
                  from parking.permit_application
                 where id = ? and sys_status = 'A'
                """,
                applicationBp,
                id);
    }

    public int countApplications(ParkingApplicationQueryParams p) {
        validateListParams(p);
        SqlBuilder sb = new SqlBuilder(
                "select count(1) from parking.permit_application pa where pa.sys_status = 'A'");
        sb.append(applicationFilters(p));
        Integer count = queryForObject(sb.getSql(), Integer.class, sb.getParams());
        return count == null ? 0 : count;
    }

    public List<ParkingApplication> searchApplications(ParkingApplicationQueryParams p) {
        validateListParams(p);
        SqlBuilder sb = new SqlBuilder(
                "select pa.* from parking.permit_application pa where pa.sys_status = 'A'");
        sb.append(applicationFilters(p));
        sb.append(applicationOrder(p));
        sb.append(limit(p));
        return getBeans(sb.getSql(), applicationBp, sb.getParams());
    }

    private SqlBuilder applicationFilters(ParkingApplicationQueryParams p) {
        SqlBuilder sb = new SqlBuilder();
        sb.appendIfNotNull("and pa.status = ?", p.getStatus());
        sb.appendIfNotNull("and pa.application_date >= ?", p.getApplicationDateFrom());
        sb.appendIfNotNull("and pa.application_date <= ?", p.getApplicationDateTo());
        return sb;
    }

    private SqlBuilder applicationOrder(ParkingApplicationQueryParams p) {
        List<String> sorts = p.getSort();
        if (sorts == null || sorts.isEmpty()) {
            return new SqlBuilder(" order by pa.application_date desc, pa.id asc");
        }

        try {
            return order(p, APPLICATION_SORT);
        } catch (IllegalArgumentException e) {
            throw new ApiClientException(
                    "unknown sort key; sortable: applicationDate, status");
        }
    }

    private void validateListParams(ParkingApplicationQueryParams p) {
        if (p.getLimit() < 1 || p.getLimit() > 100) {
            throw new ApiClientException("limit must be an integer from 1 through 100");
        }
        if (p.getOffset() < 0) {
            throw new ApiClientException("offset must be a non-negative integer");
        }
        if (p.getStatus() != null
                && !"RECEIVED".equals(p.getStatus())
                && !"DECIDED".equals(p.getStatus())) {
            throw new ApiClientException("status must be RECEIVED or DECIDED");
        }
        if (p.getApplicationDateFrom() != null
                && p.getApplicationDateTo() != null
                && p.getApplicationDateFrom().isAfter(p.getApplicationDateTo())) {
            throw new ApiClientException(
                    "applicationDateFrom must not be after applicationDateTo");
        }
    }

    public void updateApplicationStatusToDecided(Long applicationId) {
        jdbcTemplate.update(
                """
                update parking.permit_application
                   set status = 'DECIDED'
                 where id = ? and sys_status = 'A'
                """,
                applicationId);
    }

    public ParkingDecision loadDecision(Long applicationId) {
        return getBean(
                """
                select *
                  from parking.permit_decision
                 where application_id = ? and sys_status = 'A'
                """,
                decisionBp,
                applicationId);
    }

    public Long insertDecision(
            Long applicationId,
            LocalDate decisionDate,
            String decisionType,
            String decisionReason) {

        String sql = """
                insert into parking.permit_decision
                    (application_id, decision_date, decision_type, decision_reason)
                values (?, ?, ?, ?)
                returning id
                """;

        return queryForObject(
                sql,
                Long.class,
                applicationId,
                decisionDate,
                decisionType,
                decisionReason);
    }

    public Long insertApprovedDecision(Long applicationId, LocalDate decisionDate) {
        String sql = """
                insert into parking.permit_decision
                    (application_id, decision_date, decision_type)
                values (?, ?, 'APPROVED')
                returning id
                """;

        return queryForObject(
                sql,
                Long.class,
                applicationId,
                decisionDate);
    }

    public boolean hasApprovedDecision(Long applicationId) {
        Integer count = queryForObject(
                """
                select count(1)
                  from parking.permit_decision
                 where application_id = ?
                   and decision_type = 'APPROVED'
                """,
                Integer.class,
                applicationId);

        return count != null && count > 0;
    }

    public Long insertPermit(
            Long applicationId,
            String permitNumber,
            LocalDate validFrom,
            LocalDate validUntil) {

        String sql = """
                insert into parking.parking_permit
                    (application_id, permit_number, valid_from, valid_until, status)
                values (?, ?, ?, ?, 'ACTIVE')
                returning id
                """;

        return queryForObject(
                sql,
                Long.class,
                applicationId,
                permitNumber,
                validFrom,
                validUntil);
    }

    public ParkingPermit loadPermit(Long id) {
        return getBean(
                """
                select *
                  from parking.parking_permit
                 where id = ? and sys_status = 'A'
                """,
                permitBp,
                id);
    }

    public ParkingPermit loadPermitForApplication(Long applicationId) {
        return getBean(
                """
                select *
                  from parking.parking_permit
                 where application_id = ? and sys_status = 'A'
                """,
                permitBp,
                applicationId);
    }

    public boolean permitNumberExists(String permitNumber) {
        Integer count = queryForObject(
                """
                select count(1)
                  from parking.parking_permit
                 where permit_number = ? and sys_status = 'A'
                """,
                Integer.class,
                permitNumber);
        return count != null && count > 0;
    }

    public int countPermitsForApplication(Long applicationId) {
        Integer count = queryForObject(
                """
                select count(1)
                  from parking.parking_permit
                 where application_id = ?
                """,
                Integer.class,
                applicationId);

        return count == null ? 0 : count;
    }
}
