package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StopSchedule {

    @SerializedName("stop")
    @Expose
    BusStop stop;
    @SerializedName("route-schedules")
    @Expose
    List<RouteSchedule> routeSchedules;

    public BusStop getStop() {
        return stop;
    }

    public List<RouteSchedule> getRouteSchedules() {
        return routeSchedules;
    }

    @Override
    public String toString() {
        return "StopSchedule{" +
                "stop=" + stop +
                ", routeSchedules=" + routeSchedules +
                '}';
    }
}
