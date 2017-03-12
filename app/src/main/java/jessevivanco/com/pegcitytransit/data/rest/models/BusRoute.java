package jessevivanco.com.pegcitytransit.data.rest.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class BusRoute {

    @SerializedName("key")
    @Expose
    Long key;
    @SerializedName("number")
    @Expose
    Integer number;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("customer-type")
    @Expose
    String customerType;
    @SerializedName("coverage")
    @Expose
    String coverage;

    public Long getKey() {
        return key;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getCoverage() {
        return coverage;
    }

    @Override
    public String toString() {
        return "BusRoute{" +
                "key=" + key +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", customerType='" + customerType + '\'' +
                ", coverage='" + coverage + '\'' +
                '}';
    }
}
