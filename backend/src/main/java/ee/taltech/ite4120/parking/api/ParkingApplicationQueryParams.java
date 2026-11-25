package ee.taltech.ite4120.parking.api;

import java.time.LocalDate;
import org.helex.commons.model.QueryParams;

public class ParkingApplicationQueryParams extends QueryParams {

    private String status;
    private LocalDate applicationDateFrom;
    private LocalDate applicationDateTo;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getApplicationDateFrom() { return applicationDateFrom; }
    public void setApplicationDateFrom(LocalDate applicationDateFrom) {
        this.applicationDateFrom = applicationDateFrom;
    }
    public LocalDate getApplicationDateTo() { return applicationDateTo; }
    public void setApplicationDateTo(LocalDate applicationDateTo) {
        this.applicationDateTo = applicationDateTo;
    }
}
