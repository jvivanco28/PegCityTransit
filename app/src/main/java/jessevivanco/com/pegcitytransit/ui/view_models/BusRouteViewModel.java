package jessevivanco.com.pegcitytransit.ui.view_models;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import org.parceler.Parcel;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;

import static jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel.Coverage.EXPRESS;
import static jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel.Coverage.RAPID_TRANSIT;
import static jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel.Coverage.REGULAR;
import static jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel.Coverage.SPIRIT;
import static jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel.Coverage.SUPER_EXPRESS;

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
                    .coverage(route.getCoverage())
                    .build();
        }
    }

    Long key;
    Integer number;
    String name;
    String customerType;
    Coverage coverage;

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

    public Coverage getCoverage() {
        return coverage;
    }

    public static final class Builder {
        private Long key;
        private Integer number;
        private String name;
        private String customerType;
        private Coverage coverage;

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

        public Builder coverage(String val) {

            if (val == null) {
                coverage = REGULAR;
            } else if (val.equals(EXPRESS.apiValue)) {
                coverage = EXPRESS;
            } else if (val.equals(SUPER_EXPRESS.apiValue)) {
                coverage = SUPER_EXPRESS;
            } else if (val.equals(RAPID_TRANSIT.apiValue)) {
                coverage = RAPID_TRANSIT;
            } else if (name != null && name.contains(SPIRIT.apiValue)) {
                // Special case: There is no "spirit" coverage; the APi still returns those routes as
                // "regular" routes, but the route's name contains the string "spirit" so we'll just go
                // by that.
                coverage = SPIRIT;
            } else {
                coverage = REGULAR;
            }
            return this;
        }

        public BusRouteViewModel build() {
            return new BusRouteViewModel(this);
        }
    }

    /**
     * Constants for bus route coverage types. This just
     */
    public enum Coverage {

        REGULAR("regular", R.drawable.regular_route, R.color.black),
        EXPRESS("express", R.drawable.express_route, R.color.black),
        SUPER_EXPRESS("super express", R.drawable.express_route, R.color.black),
        RAPID_TRANSIT("rapid transit", R.drawable.rapid_transit_route, R.color.white),
        // NOTE: These come back as "regular" but we know that the spirit routes are 1, 2, and 3
        // Also, the route names contain the string "spirit" in them; that might be the better indicator.
        // NOTE: Yes, the capital 'S' is intentional.
        SPIRIT("Spirit", R.drawable.spirit_route, R.color.white);

        private String apiValue;

        private
        @DrawableRes
        int backgroundDrawableResId;

        private
        @ColorRes
        int textColorResId;

        Coverage(String apiValue, @DrawableRes int backgroundDrawable, @ColorRes int textColorResId) {
            this.apiValue = apiValue;
            this.backgroundDrawableResId = backgroundDrawable;
            this.textColorResId = textColorResId;
        }

        public String getApiValue() {
            return apiValue;
        }

        public
        @DrawableRes
        int getBackgroundDrawableResId() {
            return backgroundDrawableResId;
        }

        public
        @ColorRes
        int getTextColorResId() {
            return textColorResId;
        }
    }
}
