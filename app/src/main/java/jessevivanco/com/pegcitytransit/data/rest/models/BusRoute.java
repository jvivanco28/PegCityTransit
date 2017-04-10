package jessevivanco.com.pegcitytransit.data.rest.models;


import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jessevivanco.com.pegcitytransit.R;

import static jessevivanco.com.pegcitytransit.data.rest.models.BusRoute.Coverage.EXPRESS;
import static jessevivanco.com.pegcitytransit.data.rest.models.BusRoute.Coverage.RAPID_TRANSIT;
import static jessevivanco.com.pegcitytransit.data.rest.models.BusRoute.Coverage.REGULAR;
import static jessevivanco.com.pegcitytransit.data.rest.models.BusRoute.Coverage.SPIRIT;

public class BusRoute {

    /**
     * Constants for bus route coverage types. This just
     */
    public enum Coverage {

        REGULAR("regular", R.drawable.regular_route, R.color.black),
        EXPRESS("express", R.drawable.express_route, R.color.black),
        RAPID_TRANSIT("rapid transit", R.drawable.rapid_transit_route, R.color.white),
        // NOTE: These come back as "regular" but we know that the spirit routes are 1, 2, and 3
        // Also, the route names contain the string "spirit" in them; that might be the better indicator.
        SPIRIT("spirit", R.drawable.spirit_route, R.color.white);

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

    public String getNumberFormatted() {
        return String.valueOf(number);
    }

    public String getName() {
        return name;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getCoverageRaw() {
        return coverage;
    }

    public Coverage getCoverage() {
        if (coverage == null) {
            return Coverage.REGULAR;
        } else if (coverage.equals(EXPRESS.apiValue)) {
            return EXPRESS;
        } else if (coverage.equals(RAPID_TRANSIT.apiValue)) {
            return RAPID_TRANSIT;
        } else if (name != null && name.contains(SPIRIT.apiValue)) {
            // Special case: There is no "spirit" coverage; the APi still returns those routes as
            // "regular" routes, but the route's name contains the string "spirit" so we'll just go
            // by that.
            return SPIRIT;
        } else {
            return REGULAR;
        }
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
