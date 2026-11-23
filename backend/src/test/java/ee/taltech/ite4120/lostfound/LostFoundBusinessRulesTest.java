package ee.taltech.ite4120.lostfound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ee.taltech.ite4120.lostfound.dto.ItemReportQueryParams;
import ee.taltech.ite4120.lostfound.model.ItemReport;
import ee.taltech.ite4120.lostfound.service.ItemReportService;
import java.time.LocalDate;
import org.helex.commons.exception.ApiClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** Executable business rules from LOSTFOUND.01, against a fresh PostgreSQL schema. */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class LostFoundBusinessRulesTest {
    @Container @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");
    @Autowired ItemReportService service;

    @Test void futureReportDatesAreRejected() {
        ItemReport r = report(); r.setReportDate(LocalDate.now().plusDays(1));
        assertThatThrownBy(() -> service.create(r)).isInstanceOf(ApiClientException.class).hasMessageContaining("must not be in the future");
    }
    @Test void unknownCategoryIsRejected() {
        ItemReport r = report(); r.setCategoryCode("UNKNOWN");
        assertThatThrownBy(() -> service.create(r)).isInstanceOf(ApiClientException.class).hasMessageContaining("unknown categoryCode");
    }
    @Test void unknownLocationIsRejected() {
        ItemReport r = report(); r.setLocationCode("UNKNOWN");
        assertThatThrownBy(() -> service.create(r)).isInstanceOf(ApiClientException.class).hasMessageContaining("unknown locationCode");
    }
    @Test void validReportIsPersistedAndCanBeRetrieved() {
        ItemReport saved = service.create(report());
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItemName()).isEqualTo("Black wallet");
        assertThat(saved.getCategoryName()).isEqualTo("Bags");
        assertThat(saved.getLocationName()).isEqualTo("Parco Ducale");
    }
    @Test void reportSearchCanFilterByTypeCategoryAndLocation() {
        service.create(report());
        ItemReportQueryParams p = new ItemReportQueryParams(); p.setReportType("FOUND");
        p.setCategoryCode("BAGS"); p.setLocationCode("PARCO_DUCALE");
        assertThat(service.search(p).getData()).allSatisfy(r -> {
            assertThat(r.getReportType()).isEqualTo("FOUND");
            assertThat(r.getCategoryCode()).isEqualTo("BAGS");
            assertThat(r.getLocationCode()).isEqualTo("PARCO_DUCALE");
        }).isNotEmpty();
    }
    private static ItemReport report() {
        ItemReport r = new ItemReport(); r.setReportType("FOUND"); r.setItemName("Black wallet");
        r.setDescription("Black leather wallet found near the park entrance."); r.setReportDate(LocalDate.now());
        r.setCategoryCode("BAGS"); r.setLocationCode("PARCO_DUCALE"); return r;
    }
}
