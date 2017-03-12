package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class ScheduledTime {

    @SerializedName("scheduled")
    @Expose
    String scheduled;

    @SerializedName("estimated")
    @Expose
    String estimated;

    public String getScheduled() {
        return scheduled;
    }

    public String getEstimated() {
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
