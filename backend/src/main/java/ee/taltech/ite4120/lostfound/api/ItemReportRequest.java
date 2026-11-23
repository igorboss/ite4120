package ee.taltech.ite4120.lostfound.api;

import ee.taltech.ite4120.lostfound.model.ItemReport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Validated request body for creating a lost or found report. */
public class ItemReportRequest {
    @NotBlank @Pattern(regexp = "LOST|FOUND", message = "reportType must be LOST or FOUND")
    private String reportType;
    @NotBlank @Size(max = 160) private String itemName;
    @NotBlank @Size(max = 2000) private String description;
    @NotNull private LocalDate reportDate;
    @NotBlank private String categoryCode;
    @NotBlank private String locationCode;

    public ItemReport toModel() {
        ItemReport r = new ItemReport(); r.setReportType(reportType); r.setItemName(itemName.trim());
        r.setDescription(description.trim()); r.setReportDate(reportDate); r.setCategoryCode(categoryCode);
        r.setLocationCode(locationCode); return r;
    }
    public String getReportType() { return reportType; }
    public void setReportType(String v) { reportType = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { itemName = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate v) { reportDate = v; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String v) { categoryCode = v; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String v) { locationCode = v; }
}
