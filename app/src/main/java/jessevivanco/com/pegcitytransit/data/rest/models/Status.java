package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;

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