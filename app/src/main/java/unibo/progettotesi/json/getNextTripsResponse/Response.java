
package unibo.progettotesi.json.getNextTripsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Response {

    @SerializedName("trips")
    @Expose
    public Trip trip;


}
