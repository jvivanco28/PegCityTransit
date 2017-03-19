package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bus {

    @SerializedName("bike-rack")
    @Expose
    String bikeRack;
    @SerializedName("easy-access")
    @Expose
    String easyAccess;

    public String getBikeRack() {
        return bikeRack;
    }

    public String getEasyAccess() {
        return easyAccess;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "bikeRack='" + bikeRack + '\'' +
                ", easyAccess='" + easyAccess + '\'' +
                '}';
    }
}
