package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScheduledStop {

    @SerializedName("key")
    @Expose
    String key;
    @SerializedName("times")
    @Expose
    Times times;
    @SerializedName("variant")
    @Expose
    Variant variant;
    @SerializedName("bus")
    @Expose
    Bus bus;

    public String getKey() {
        return key;
    }

    public Times getTimes() {
        return times;
    }

    public Variant getVariant() {
        return variant;
    }

    public Bus getBus() {
        return bus;
    }

    @Override
    public String toString() {
        return "ScheduledStop{" +
                "key='" + key + '\'' +
                ", times=" + times +
                ", variant=" + variant +
                ", bus=" + bus +
                '}';
    }
}
