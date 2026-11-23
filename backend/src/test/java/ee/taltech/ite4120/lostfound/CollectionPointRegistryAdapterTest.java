package ee.taltech.ite4120.lostfound;

import static org.assertj.core.api.Assertions.assertThat;

import ee.taltech.ite4120.lostfound.registry.CollectionPointRegistryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CollectionPointRegistryAdapterTest {
    @Autowired CollectionPointRegistryAdapter registry;
    @Test void localMockRegistryReturnsCollectionPointsThroughTheAdapter() {
        assertThat(registry.findAll()).isNotEmpty().first().satisfies(point -> {
            assertThat(point.id()).isNotBlank(); assertThat(point.name()).isNotBlank(); assertThat(point.address()).isNotBlank();
        });
    }
}
