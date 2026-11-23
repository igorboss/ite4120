package ee.taltech.ite4120.lostfound.registry.xroad;

import ee.taltech.ite4120.lostfound.registry.CollectionPoint;
import ee.taltech.ite4120.lostfound.registry.CollectionPointRegistryAdapter;
import ee.taltech.ite4120.lostfound.registry.CollectionPointRegistryException;
import ee.taltech.ite4120.lostfound.registry.xroad.wire.CollectionPointsRequest;
import ee.taltech.ite4120.lostfound.registry.xroad.wire.CollectionPointsResponse;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.helex.forge.xroad.XRoadClient;
import org.helex.forge.xroad.XRoadClientConfig;
import org.helex.forge.xroad.XRoadClientId;
import org.helex.forge.xroad.XRoadException;
import org.helex.forge.xroad.XRoadResponse;
import org.helex.forge.xroad.XRoadServiceId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** X-Road transport adapter. The wire schema and identifiers are course placeholders until the provider contract is supplied. */
@Component
@Profile("xroad")
public class XRoadCollectionPointRegistryAdapter implements CollectionPointRegistryAdapter {
    private final XRoadClient client;
    private final XRoadServiceId service;
    public XRoadCollectionPointRegistryAdapter(
            @Value("${lostfound.xroad.security-server-url:http://localhost:8080}") String securityServerUrl,
            @Value("${lostfound.xroad.instance:ee-test}") String instance,
            @Value("${lostfound.xroad.client-member-class:EDU}") String clientMemberClass,
            @Value("${lostfound.xroad.client-member-code:taltech}") String clientMemberCode,
            @Value("${lostfound.xroad.client-subsystem:ite4120}") String clientSubsystem,
            @Value("${lostfound.xroad.service-member-class:GOV}") String serviceMemberClass,
            @Value("${lostfound.xroad.service-member-code:parma}") String serviceMemberCode,
            @Value("${lostfound.xroad.service-subsystem:lostfound}") String serviceSubsystem) {
        XRoadClientId clientId = new XRoadClientId(instance, clientMemberClass, clientMemberCode, clientSubsystem);
        this.client = new XRoadClient(new XRoadClientConfig(URI.create(securityServerUrl), clientId, "ITE4120-LostFound", Duration.ofSeconds(15)));
        this.service = new XRoadServiceId(instance, serviceMemberClass, serviceMemberCode, serviceSubsystem, "collectionPoints", "v1");
    }
    @Override public List<CollectionPoint> findAll() {
        try {
            XRoadResponse<CollectionPointsResponse> response = client.send(service, CollectionPointsRequest.forParma(), CollectionPointsResponse.class);
            if (response.body() == null || response.body().collectionPoints() == null) return List.of();
            return response.body().collectionPoints().stream()
                    .map(w -> new CollectionPoint(w.id(), w.name(), w.address())).toList();
        } catch (XRoadException e) {
            throw new CollectionPointRegistryException("X-Road collection-point call failed: " + e.getMessage(), e);
        }
    }
}
