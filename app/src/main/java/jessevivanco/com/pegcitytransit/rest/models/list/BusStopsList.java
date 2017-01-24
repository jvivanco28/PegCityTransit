package jessevivanco.com.pegcitytransit.rest.models.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import jessevivanco.com.pegcitytransit.rest.models.BusStop;

public class BusStopsList {

    @SerializedName("stops")
    @Expose
    List<BusStop> busStops;

    @SerializedName("query-time")
    @Expose
    Date queryTime;

    public List<BusStop> getBusStops() {
        return busStops;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    @Override
    public String toString() {
        return "BusStopsList{" +
                "busStops=" + busStops +
                ", queryTime=" + queryTime +
                '}';
    }
}
