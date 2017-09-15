package unibo.progettotesi.model.services;

/**
 * Created by Valentina on 25/08/2017.
 */

import java.io.FileNotFoundException;
import java.io.IOException;

import unibo.progettotesi.model.MyData.IDataSet;
import unibo.progettotesi.model.MyData.IMyData;
import unibo.progettotesi.model.MyData.MyData;
import unibo.progettotesi.model.consents.ConsentManager;
import unibo.progettotesi.model.consents.InputDataConsent;
import unibo.progettotesi.model.consents.OutputDataConsent;
import unibo.progettotesi.model.consents.ServiceConsent;
import unibo.progettotesi.model.security.ISecurityManager;
import unibo.progettotesi.model.users.IUser;

/**
 * Any new service should extend this class to be invokable within the
 * infrastructure. This does not comprehend service registration, which should
 * be done by the extending class.
 *
 * @author Giada
 *
 */

public abstract class AbstractService implements IService {

    protected IMyData myDataInstance;
    protected ISecurityManager securityManager;

    protected AbstractService() {
        this.myDataInstance = MyData.getInstance();
        this.securityManager = new unibo.progettotesi.model.security.SecurityManager();
    }

    /**
     * This method implements that part of the business logic which is common to
     * each service: it asks the ConsentManager for a OutputDataConsent and it
     * receives the DataSet associated with that consent. After that, an
     * abstract method is called, which will contain the specific implementation
     * of the service considered.
     */
    @Override
    public Object provideService(IUser user) throws FileNotFoundException, IOException {
        OutputDataConsent outDataConsent = ConsentManager.askOutputDataConsent(user, this);
        IDataSet dataSet = myDataInstance.getDataSetForOutputDataConsent(outDataConsent);
        return this.concreteService(dataSet);
    }

    @Override
    public void gatherData(IUser user, IDataSet dataSet) {
        if (!user.hasAccountAtService(this))
            throw new IllegalArgumentException(
                    "User " + user.toString() + " doesn't have an account to this service " + this.toString() + ".");
        ServiceConsent sConsent = user.getActiveSCForService(this);
        if (sConsent == null)
            throw new IllegalStateException(
                    "The GoBoBusService Consent associated with " + this.toString() + " is not Active.");
        InputDataConsent inDataConsent = ConsentManager.askInputDataConsent(user, this, dataSet);
        this.myDataInstance.saveDataSet(dataSet, inDataConsent);
    }

    @Override
    public ISecurityManager getSecurityManager() {
        return this.securityManager;
    }

    protected abstract Object concreteService(IDataSet dataSet) throws FileNotFoundException, IOException;

    /**
     * This method is called at the instantiation of the implementing object,
     * which will be the concrete service. Its function is to register the
     * service to the GoBoBusService Registry, as specified in the MyData Architecture
     * Specification. In particular, the concrete service has to declare which
     * data types it needs to work.
     */
    protected abstract void registerService();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();

}
