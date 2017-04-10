package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusStop {

    @SerializedName("key")
    @Expose
    Long key;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("number")
    @Expose
    Integer number;

    @SerializedName("direction")
    @Expose
    String direction;

    @SerializedName("side")
    @Expose
    String side;

    @SerializedName("street")
    @Expose
    Street street;

    @SerializedName("cross-street")
    @Expose
    CrossStreet crossStreet;

    @SerializedName("centre")
    @Expose
    Centre centre;

    @SerializedName("distances")
    @Expose
    Distance distances;

    /**
     * A list of bus routes for this specific bus stop. NOTE: This info does not come from the API.
     */
    List<BusRoute> busRoutes;

    public Long getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public String getNumberFormatted() {
        return String.valueOf(number);
    }

    public String getDirection() {
        return direction;
    }

    public String getSide() {
        return side;
    }

    public Street getStreet() {
        return street;
    }

    public CrossStreet getCrossStreet() {
        return crossStreet;
    }

    public Centre getCentre() {
        return centre;
    }

    public Distance getDistance() {
        return distances;
    }

    public List<BusRoute> getBusRoutes() {
        return busRoutes;
    }

    public void setBusRoutes(List<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof BusStop) {
            BusStop busStop = (BusStop) obj;

            return busStop.getKey() != null &&
                    this.key != null &&
                    busStop.getKey().equals(this.key);
        }
        return false;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "key=" + key +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", direction='" + direction + '\'' +
                ", side='" + side + '\'' +
                ", street=" + street +
                ", crossStreet=" + crossStreet +
                ", centre=" + centre +
                ", distances=" + distances +
                ", busRoutes=" + busRoutes +
                '}';
    }
}
