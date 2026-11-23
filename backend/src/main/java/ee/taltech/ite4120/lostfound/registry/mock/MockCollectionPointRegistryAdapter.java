package ee.taltech.ite4120.lostfound.registry.mock;

import ee.taltech.ite4120.lostfound.registry.CollectionPoint;
import ee.taltech.ite4120.lostfound.registry.CollectionPointRegistryAdapter;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Local deterministic registry used for the course demonstration. */
@Component
@Profile("local | test")
public class MockCollectionPointRegistryAdapter implements CollectionPointRegistryAdapter {
    @Override public List<CollectionPoint> findAll() {
        return List.of(
            new CollectionPoint("CP-001", "Municipal Customer Service", "Largo Torello de Strada 11, Parma"),
            new CollectionPoint("CP-002", "Parma Railway Station Desk", "Piazzale Carlo Alberto Dalla Chiesa, Parma"),
            new CollectionPoint("CP-003", "Municipal Police Headquarters", "Strada del Taglio 8, Parma")
        );
    }
}
