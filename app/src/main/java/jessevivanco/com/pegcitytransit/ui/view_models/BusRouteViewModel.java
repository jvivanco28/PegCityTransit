package jessevivanco.com.pegcitytransit.ui.view_models;

import org.parceler.Parcel;

import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.ui.util.RouteCoverage;

@Parcel
public class BusRouteViewModel {

    public static BusRouteViewModel createFromBusRoute(BusRoute route) {
        if (route == null) {
            return null;
        } else {
            return new Builder()
                    .key(route.getKey())
                    .number(route.getNumber())
                    .name(route.getName())
                    .customerType(route.getCustomerType())
                    .coverage(route.getCoverage(), route.getName())
                    .build();
        }
    }

    Long key;
    Integer number;
    String name;
    String customerType;
    RouteCoverage coverage;

    public BusRouteViewModel() {
    }

    private BusRouteViewModel(Builder builder) {
        key = builder.key;
        number = builder.number;
        name = builder.name;
        customerType = builder.customerType;
        coverage = builder.coverage;
    }

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

    public RouteCoverage getCoverage() {
        return coverage;
    }

    public static final class Builder {
        private Long key;
        private Integer number;
        private String name;
        private String customerType;
        private RouteCoverage coverage;

        public Builder() {
        }

        public Builder key(Long val) {
            key = val;
            return this;
        }

        public Builder number(Integer val) {
            number = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder customerType(String val) {
            customerType = val;
            return this;
        }

        public Builder coverage(String coverageStr, String routeName) {
            coverage = RouteCoverage.getCoverage(coverageStr, routeName);
            return this;
        }

        public BusRouteViewModel build() {
            return new BusRouteViewModel(this);
        }
    }
}
