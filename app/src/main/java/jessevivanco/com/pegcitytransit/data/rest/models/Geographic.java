package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geographic {

    @SerializedName("latitude")
    @Expose
    String latitude;
    @SerializedName("longitude")
    @Expose
    String longitude;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Geographic{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
