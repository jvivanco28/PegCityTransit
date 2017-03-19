package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variant {

    @SerializedName("key")
    @Expose
    String key;
    @SerializedName("name")
    @Expose
    String name;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Variant{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
