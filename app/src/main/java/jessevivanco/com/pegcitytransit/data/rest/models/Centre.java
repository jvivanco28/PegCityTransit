package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Centre {
    @SerializedName("utm")
    @Expose
    Utm utm;
    @SerializedName("geographic")
    @Expose
    Geographic geographic;

    public Utm getUtm() {
        return utm;
    }

    public Geographic getGeographic() {
        return geographic;
    }

    @Override
    public String toString() {
        return "Centre{" +
                "utm=" + utm +
                ", geographic=" + geographic +
                '}';
    }
}
