package ee.taltech.ite4120.lostfound.registry.xroad.wire;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

/** Illustrative provider response; field names must be aligned with the real service WSDL. */
@JacksonXmlRootElement(localName = "collectionPointsResponse", namespace = CollectionPointsRequest.NS)
public record CollectionPointsResponse(
        @JacksonXmlElementWrapper(localName = "collectionPoints", useWrapping = true)
        @JacksonXmlProperty(localName = "collectionPoint", namespace = CollectionPointsRequest.NS)
        List<CollectionPointWire> collectionPoints) {
    public record CollectionPointWire(
            @JacksonXmlProperty(localName = "id", namespace = CollectionPointsRequest.NS) String id,
            @JacksonXmlProperty(localName = "name", namespace = CollectionPointsRequest.NS) String name,
            @JacksonXmlProperty(localName = "address", namespace = CollectionPointsRequest.NS) String address) { }
}
