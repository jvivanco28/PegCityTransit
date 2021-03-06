package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteSchedule {

    @SerializedName("route")
    @Expose
    BusRoute route;
    @SerializedName("scheduled-stops")
    @Expose
    List<ScheduledStop> scheduledStops;

    public BusRoute getRoute() {
        return route;
    }

    public List<ScheduledStop> getScheduledStops() {
        return scheduledStops;
    }

    @Override
    public String toString() {
        return "RouteSchedule{" +
                "route=" + route +
                ", scheduledStops=" + scheduledStops +
                '}';
    }
}
