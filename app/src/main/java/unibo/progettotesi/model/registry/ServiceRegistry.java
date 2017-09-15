package unibo.progettotesi.model.registry;

/**
 * Created by Valentina on 24/08/2017.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import unibo.progettotesi.model.services.IService;

/**
 * This class realizes a simplified version of the GoBoBusService Registry described in
 * the MyData specification. This ServiceRegistry keeps track of each service
 * registered to the MyData infrastructure in a Map, where services (keys) are
 * associated to a Set of declared data types (value). Each service must
 * register to be accessible from users; data types declared during registration
 * phase will be the only data types that this service will be allowed to work
 * with.
 *
 * @author Giada
 *
 */

public class ServiceRegistry {

    private static Map<IService, Set<String>> mappings = new HashMap<IService, Set<String>>();

    private ServiceRegistry() {
    }

    /**
     * This method registers a particular service to the ServiceRegistry. In
     * particular, only the types declared as an input parameter will be allowed
     * into or out from the Personal Data Vault for this specific service.
     *
     * @param service
     * @param types
     */

    public static void registerService(IService service, Set<String> types) {
        // forse dovrei verificare che il servizio sia affidabile
        if (service == null || types == null)
            throw new IllegalArgumentException("GoBoBusService and corresponding metadata must not be null.");
        mappings.put(service, types);
    }

    /**
     * This method retrieves the Set of specified data types registered for the
     * given service.
     *
     * @param service
     * @return
     */
    public static Set<String> getMetadataForService(IService service) {
        if (service == null)
            throw new IllegalArgumentException("Cannot retrieve metadata for null GoBoBusService.");
        if (!mappings.containsKey(service))
            throw new IllegalArgumentException(
                    "GoBoBusService " + service.toString() + " entry is not registered in GoBoBusService Registry.");
        return mappings.get(service);
    }
}
