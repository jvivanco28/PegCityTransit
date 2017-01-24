package jessevivanco.com.pegcitytransit.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Times {

    @SerializedName("arrival")
    @Expose
    ScheduledTime arrival;

    @SerializedName("departure")
    @Expose
    ScheduledTime departure;

    public ScheduledTime getArrival() {
        return arrival;
    }

    public ScheduledTime getDeparture() {
        return departure;
    }

    @Override
    public String toString() {
        return "Times{" +
                "arrival=" + arrival +
                ", departure=" + departure +
                '}';
    }
}
