package unibo.progettotesi.model.users;

/**
 * Created by Valentina on 24/08/2017.
 */

import java.util.List;
import java.util.Set;

import unibo.progettotesi.model.consents.DataConsent;
import unibo.progettotesi.model.consents.ServiceConsent;
import unibo.progettotesi.model.services.IService;

public interface IAccount {

    public IService getService();

    /**
     * There can be only one ServiceConsent Active or Disabled at a time for a
     * service. If there is no such consent, this method returns null
     *
     * @return The only Active or Disabled ServiceConsent for
     *         this service, null otherwise.
     */
    public ServiceConsent getActiveDisabledSC();

    public Set<ServiceConsent> getAllServiceConsents();

    public void addDataConsent(DataConsent dataConsent);

    public void addServiceConsent(ServiceConsent serviceConsent);

    public List<DataConsent> getAllDataConsents(ServiceConsent sc);
}
