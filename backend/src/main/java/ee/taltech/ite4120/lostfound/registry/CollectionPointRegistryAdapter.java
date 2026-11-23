package ee.taltech.ite4120.lostfound.registry;

import java.util.List;

/** Port to the external municipal collection-point registry. */
public interface CollectionPointRegistryAdapter { List<CollectionPoint> findAll(); }
