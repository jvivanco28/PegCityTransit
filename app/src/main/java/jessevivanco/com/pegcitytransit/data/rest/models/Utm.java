package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Utm {

    @SerializedName("zone")
    @Expose
    String zone;
    @SerializedName("x")
    @Expose
    Integer x;
    @SerializedName("y")
    @Expose
    Integer y;

    public String getZone() {
        return zone;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Utm{" +
                "zone='" + zone + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
