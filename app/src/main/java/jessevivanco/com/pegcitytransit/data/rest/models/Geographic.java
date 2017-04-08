package jessevivanco.com.pegcitytransit.data.rest.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geographic {

    private static final String TAG = Geographic.class.getSimpleName();

    @SerializedName("latitude")
    @Expose
    String latitude;
    @SerializedName("longitude")
    @Expose
    String longitude;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public
    @Nullable
    LatLng getLatLng() {
        LatLng latLng = null;

        if (latitude != null && longitude != null) {
            try {
                latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing lat and long.", e);
            }
        }
        return latLng;
    }

    @Override
    public String toString() {
        return "Geographic{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
