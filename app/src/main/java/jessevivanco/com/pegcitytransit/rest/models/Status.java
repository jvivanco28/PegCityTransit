package jessevivanco.com.pegcitytransit.rest.models;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

@Parcel
public class Status {

    @Expose
    String name;

    @Expose
    String value;

    @Expose
    String message;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}