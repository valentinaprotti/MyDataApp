package unibo.progettotesi.model.MyData;

/**
 * Created by Valentina on 25/08/2017.
 */


import java.util.Date;

import unibo.progettotesi.model.consents.InputDataConsent;
import unibo.progettotesi.model.consents.OutputDataConsent;
import unibo.progettotesi.model.services.IService;
import unibo.progettotesi.model.users.IUser;

public interface IMyData {

    public IUser loginUser(String email, char[] password);

    public IUser createMyDataAccount(String firstName, String lastName, Date dateOfBirth, String emailAddress,
									 char[] password);

    public IDataSet getDataSetForOutputDataConsent(OutputDataConsent outputDataConsent);

    public void saveDataSet(IDataSet dataSet, InputDataConsent inDataConsent);

    public void issueNewServiceConsent(IService selectedService, IUser authenticatedUser);

    public void createServiceAccount(IUser user, IService service);

}

