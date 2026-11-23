package ee.taltech.ite4120.lostfound.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** A citizen-submitted lost or found item report. */
public class ItemReport {
    private Long id;
    private String reportType;
    private String itemName;
    private String description;
    private LocalDate reportDate;
    private String categoryCode;
    private String categoryName;
    private String locationCode;
    private String locationName;
    private String locationAddress;
    private String sysStatus;
    private Integer sysVersion;
    private OffsetDateTime sysCreatedAt;
    private String sysCreatedBy;
    private OffsetDateTime sysModifiedAt;
    private String sysModifiedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress; }
    public String getSysStatus() { return sysStatus; }
    public void setSysStatus(String sysStatus) { this.sysStatus = sysStatus; }
    public Integer getSysVersion() { return sysVersion; }
    public void setSysVersion(Integer sysVersion) { this.sysVersion = sysVersion; }
    public OffsetDateTime getSysCreatedAt() { return sysCreatedAt; }
    public void setSysCreatedAt(OffsetDateTime sysCreatedAt) { this.sysCreatedAt = sysCreatedAt; }
    public String getSysCreatedBy() { return sysCreatedBy; }
    public void setSysCreatedBy(String sysCreatedBy) { this.sysCreatedBy = sysCreatedBy; }
    public OffsetDateTime getSysModifiedAt() { return sysModifiedAt; }
    public void setSysModifiedAt(OffsetDateTime sysModifiedAt) { this.sysModifiedAt = sysModifiedAt; }
    public String getSysModifiedBy() { return sysModifiedBy; }
    public void setSysModifiedBy(String sysModifiedBy) { this.sysModifiedBy = sysModifiedBy; }
}
