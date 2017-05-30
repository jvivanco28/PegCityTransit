package jessevivanco.com.pegcitytransit.ui.util;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import jessevivanco.com.pegcitytransit.R;

/**
 * Constants for bus route coverage types.
 */
public enum RouteCoverage {

    REGULAR("regular", R.drawable.regular_route_button, R.color.black),
    EXPRESS("express", R.drawable.express_route_button, R.color.black),
    SUPER_EXPRESS("super express", R.drawable.express_route_button, R.color.black),
    RAPID_TRANSIT("rapid transit", R.drawable.rapid_transit_route_button, R.color.white),
    // NOTE: These come back as "regular" but we know that the spirit routes are 1, 2, and 3
    // Also, the route names contain the string "spirit" in them; that might be the better indicator.
    // NOTE: Capital 'S' is intentional.
    SPIRIT("Spirit", R.drawable.spirit_route_button, R.color.white);

    private String apiValue;

    private
    @DrawableRes
    int backgroundDrawableResId;

    private
    @ColorRes
    int textColorResId;

    RouteCoverage(String apiValue, @DrawableRes int backgroundDrawable, @ColorRes int textColorResId) {
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

    public static RouteCoverage getCoverage(String coverageStrVal, String routeName) {

        if (coverageStrVal == null) {
            return REGULAR;
        } else if (coverageStrVal.equals(EXPRESS.apiValue)) {
            return EXPRESS;
        } else if (coverageStrVal.equals(SUPER_EXPRESS.apiValue)) {
            return SUPER_EXPRESS;
        } else if (coverageStrVal.equals(RAPID_TRANSIT.apiValue)) {
            return RAPID_TRANSIT;
        } else if (routeName != null && routeName.contains(SPIRIT.apiValue)) {
            // Special case: There is no "spirit" coverage; the API still returns those routes as
            // "regular" routes, but the route's name contains the string "spirit" so we'll just go
            // by that.
            return SPIRIT;
        } else {
            return REGULAR;
        }
    }
}