package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class BusStop extends RealmObject {

    @PrimaryKey
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

    @Ignore
    @SerializedName("street")
    @Expose
    Street street;

    @Ignore
    @SerializedName("cross-street")
    @Expose
    CrossStreet crossStreet;

    @Ignore
    @SerializedName("centre")
    @Expose
    Centre centre;

    @Ignore
    @SerializedName("distances")
    @Expose
    Distance distances;

    /**
     * A list of bus routes for this specific bus stop. NOTE: This info does not come from the API.
     */
    RealmList<BusRoute> busRoutes;

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

    public RealmList<BusRoute> getBusRoutes() {
        return busRoutes;
    }

    public void setBusRoutes(RealmList<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
    }

    // TODO delete this. Only for testing purposes.
    public String getRoutesFormatted() {
        String routes = "";
        for (int i = 0; busRoutes != null && i < busRoutes.size(); i++) {
            if (i == busRoutes.size() - 1)
                routes += busRoutes.get(i).getNumber();
            else
                routes += busRoutes.get(i).getNumber() + ", ";
        }
        return routes;
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
