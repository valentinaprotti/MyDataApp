package unibo.progettotesi.model.MyData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.model.Stop;
import unibo.progettotesi.model.registry.Metadata;

/**
 * Created by Valentina on 25/08/2017.
 */

public class PersonalDataVault implements IPersonalDataVault {
    Location location;
    double latitude, longitude;
    Route route;
    Stop stop;

    public PersonalDataVault() {
        this.location = readLocation();
        this.latitude = readLatitude();
		this.longitude = readLongitude();
		this.route = readRoute();
		this.stop = readStop();
    }

    private Location readLocation() {
        String fileName = "PDV.txt";
        Location result = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;


            while ((line = br.readLine()) != null) {
                if (line == null || line.length() == 0)
                    throw new IllegalArgumentException("line must be initialized");
                result = Location.getLocationFromString(line.split(" ")[0]);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private double readLatitude() {
        String fileName = "PDV.txt";
        double result = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;


            while ((line = br.readLine()) != null) {
                if (line == null || line.length() == 0)
                    throw new IllegalArgumentException("line must be initialized");
                result = Double.parseDouble(line.split(" ")[1]);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

	private double readLongitude() {
		String fileName = "PDV.txt";
		double result = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;


			while ((line = br.readLine()) != null) {
				if (line == null || line.length() == 0)
					throw new IllegalArgumentException("line must be initialized");
				result = Double.parseDouble(line.split(" ")[2]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private Route readRoute() {
		String fileName = "PDV.txt";
		Route result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;


			while ((line = br.readLine()) != null) {
				if (line == null || line.length() == 0)
					throw new IllegalArgumentException("line must be initialized");
				result = Route.getRouteFromString(line.split(" ")[3]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private Stop readStop() {
		String fileName = "PDV.txt";
		Stop result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;


			while ((line = br.readLine()) != null) {
				if (line == null || line.length() == 0)
					throw new IllegalArgumentException("line must be initialized");
				result = Stop.getStopFromString(line.split(" ")[4]);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    @Override
    public IDataSet getData(Set<String> typesConst) {
        return null;
    }

    @Override
    public void saveData(IDataSet dataSet) {
        for (String typeConst : dataSet.getKeys()) {
            if (typeConst.equals(Metadata.LOCATION))
                this.setLocation((Location) dataSet.getObject(typeConst));
            if (typeConst.equals(Metadata.LATITUDE))
                this.setLatitude((double) dataSet.getObject(typeConst));
			if (typeConst.equals(Metadata.LONGITUDE))
				this.setLongitude((double) dataSet.getObject(typeConst));
			if (typeConst.equals(Metadata.ROUTE))
				this.setRoute((Route) dataSet.getObject(typeConst));
			if (typeConst.equals(Metadata.STOP))
				this.setStop((Stop) dataSet.getObject(typeConst));
        }
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}
}
