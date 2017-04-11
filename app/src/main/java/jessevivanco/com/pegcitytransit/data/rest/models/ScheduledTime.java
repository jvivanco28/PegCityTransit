package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ScheduledTime {

    @SerializedName("scheduled")
    @Expose
    Date scheduled;

    @SerializedName("estimated")
    @Expose
    Date estimated;

    public Date getScheduled() {
        return scheduled;
    }

    public Date getEstimated() {
        return estimated;
    }

    @Override
    public String toString() {
        return "ScheduledTime{" +
                "scheduled='" + scheduled + '\'' +
                ", estimated='" + estimated + '\'' +
                '}';
    }
}
