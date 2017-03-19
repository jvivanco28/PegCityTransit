package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CrossStreet {
    @SerializedName("key")
    @Expose
    Long key;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("type")
    @Expose
    String type;

    public Long getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CrossStreet{" +
                "key=" + key +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
