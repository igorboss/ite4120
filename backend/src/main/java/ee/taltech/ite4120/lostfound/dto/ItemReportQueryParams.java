package ee.taltech.ite4120.lostfound.dto;

import org.helex.commons.model.QueryParams;

/** Search filters supported by the Lost and Found list endpoint. */
public class ItemReportQueryParams extends QueryParams {
    private String textContains;
    private String reportType;
    private String categoryCode;
    private String locationCode;
    public String getTextContains() { return textContains; }
    public void setTextContains(String v) { textContains = v; }
    public String getReportType() { return reportType; }
    public void setReportType(String v) { reportType = v; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String v) { categoryCode = v; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String v) { locationCode = v; }
}
