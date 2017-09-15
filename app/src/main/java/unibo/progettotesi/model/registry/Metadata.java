package unibo.progettotesi.model.registry;

import unibo.progettotesi.model.Location;

import unibo.progettotesi.model.Route;
import unibo.progettotesi.model.Stop;

/**
 * Created by Valentina on 25/08/2017.
 */

public final class Metadata implements IMetadata {

    public static final String LOCATION = Location.class.getCanonicalName();
    public static final String LATITUDE = double.class.getCanonicalName();
    public static final String LONGITUDE = double.class.getCanonicalName();
    public static final String ROUTE = Route.class.getCanonicalName();
    public static final String STOP = Stop.class.getCanonicalName();

    private Metadata() {
    }
}

