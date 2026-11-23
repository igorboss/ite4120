package ee.taltech.ite4120.lostfound.model;

/** Category, location or collection-point reference returned by the service. */
public class ReferenceData {
    private String code;
    private String name;
    private String address;
    public ReferenceData() { }
    public ReferenceData(String code, String name, String address) { this.code = code; this.name = name; this.address = address; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
