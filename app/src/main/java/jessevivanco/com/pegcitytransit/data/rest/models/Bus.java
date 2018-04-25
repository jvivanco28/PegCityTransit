package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bus {

    @SerializedName("bike-rack")
    @Expose
    boolean bikeRack;
    @SerializedName("easy-access")
    @Expose
    boolean easyAccess;
    @SerializedName("wifi")
    @Expose
    boolean wifi;

    public boolean getBikeRack() {
        return bikeRack;
    }

    public boolean getEasyAccess() {
        return easyAccess;
    }

    public boolean hasWifi() {
        return wifi;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "bikeRack='" + bikeRack + '\'' +
                ", easyAccess='" + easyAccess + '\'' +
                ", wifi='" + wifi + '\'' +
                '}';
    }
}
