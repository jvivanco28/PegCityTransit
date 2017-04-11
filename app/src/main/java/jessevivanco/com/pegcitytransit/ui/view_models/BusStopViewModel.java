package jessevivanco.com.pegcitytransit.ui.view_models;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.util.List;

import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;

@Parcel
public class BusStopViewModel {

    public static BusStopViewModel createFromBusStop(BusStop busStop) {
        if (busStop == null) {
            return null;
        } else {
            return new Builder()
                    .key(busStop.getKey())
                    .distance(busStop.getDistance() != null ? busStop.getDistance().getDirect() : null)
                    .latLng(busStop.getCentre() != null && busStop.getCentre().getGeographic() != null ? busStop.getCentre().getGeographic().getLatLng() : null)
                    .name(busStop.getName())
                    .number(busStop.getNumber())
                    .build();
        }
    }

    Long key;
    String name;
    Integer number;
    LatLng latLng;
    String distance;

    @Nullable
    List<BusRouteViewModel> routes;

    public BusStopViewModel() {
    }

    private BusStopViewModel(Builder builder) {
        key = builder.key;
        name = builder.name;
        number = builder.number;
        latLng = builder.latLng;
        distance = builder.distance;
        routes = builder.routes;
    }

    public Long getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getDistance() {
        return distance;
    }

    @Nullable
    public List<BusRouteViewModel> getRoutes() {
        return routes;
    }

    public void setRoutes(@Nullable List<BusRouteViewModel> routes) {
        this.routes = routes;
    }

    public static final class Builder {
        private Long key;
        private String name;
        private Integer number;
        private LatLng latLng;
        private String distance;
        private List<BusRouteViewModel> routes;

        public Builder() {
        }

        public Builder key(Long val) {
            key = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder number(Integer val) {
            number = val;
            return this;
        }

        public Builder latLng(LatLng val) {
            latLng = val;
            return this;
        }

        public Builder distance(String val) {
            distance = val;
            return this;
        }

        public Builder routes(List<BusRouteViewModel> val) {
            routes = val;
            return this;
        }

        public BusStopViewModel build() {
            return new BusStopViewModel(this);
        }
    }
}
