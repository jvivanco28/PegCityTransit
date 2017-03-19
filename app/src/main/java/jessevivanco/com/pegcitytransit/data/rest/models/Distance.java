package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distance {

    @SerializedName("direct")
    @Expose
    String direct;

    public String getDirect() {
        return direct;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "direct='" + direct + '\'' +
                '}';
    }
}
