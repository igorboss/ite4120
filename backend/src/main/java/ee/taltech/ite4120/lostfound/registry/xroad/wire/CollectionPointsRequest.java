package ee.taltech.ite4120.lostfound.registry.xroad.wire;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/** Illustrative provider wire request. Replace the namespace and element names with the published WSDL contract. */
@JacksonXmlRootElement(localName = "collectionPoints", namespace = CollectionPointsRequest.NS)
public record CollectionPointsRequest(
        @JacksonXmlProperty(localName = "request", namespace = NS) Request request) {
    public static final String NS = "http://parma.example.eu/xroad";
    public record Request(@JacksonXmlProperty(localName = "municipalityCode", namespace = NS) String municipalityCode) { }
    public static CollectionPointsRequest forParma() { return new CollectionPointsRequest(new Request("PR")); }
}
