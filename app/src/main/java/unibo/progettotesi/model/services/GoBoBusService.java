package unibo.progettotesi.model.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import unibo.progettotesi.model.MyData.IDataSet;
import unibo.progettotesi.model.registry.Metadata;
import unibo.progettotesi.model.registry.ServiceRegistry;

/**
 * Created by Valentina on 29/08/2017.
 */

public class GoBoBusService extends AbstractService {
	private final String name = "Servizio GoBoBus";
	private final Set<String> identifiers = new HashSet<String>();

	public GoBoBusService() {
		super();
		this.registerService();
	}

	// ??????????????
	@Override
	protected Object concreteService(IDataSet dataSet) throws FileNotFoundException, IOException {
		return null;
	}

	/**
	 * In this function, each concrete service specifies which of the allowed
	 * types it is going to use. The GoBoBusService Registry must be informed at the
	 * end of this process.
	 */
	@Override
	protected void registerService() {
		identifiers.add(Metadata.LOCATION);
		identifiers.add(Metadata.LATITUDE);
		identifiers.add(Metadata.LONGITUDE);
		identifiers.add(Metadata.ROUTE);
		identifiers.add(Metadata.STOP);
		ServiceRegistry.registerService(this, identifiers);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoBoBusService other = (GoBoBusService) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
