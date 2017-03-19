package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Street {

    @SerializedName("key")
    @Expose
    Long key;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("leg")
    @Expose
    String leg;

    public Long getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLeg() {
        return leg;
    }

    @Override
    public String toString() {
        return "Street{" +
                "key=" + key +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", leg='" + leg + '\'' +
                '}';
    }
}
