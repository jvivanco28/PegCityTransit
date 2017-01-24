package jessevivanco.com.pegcitytransit.rest.models.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import jessevivanco.com.pegcitytransit.rest.models.BusRoute;

public class RoutesList {

    @SerializedName("routes")
    @Expose
    List<BusRoute> routes;

    @SerializedName("query-time")
    @Expose
    Date queryTime;

    public List<BusRoute> getRoutes() {
        return routes;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    @Override
    public String toString() {
        return "RoutesList{" +
                "routes=" + routes +
                ", queryTime=" + queryTime +
                '}';
    }
}
