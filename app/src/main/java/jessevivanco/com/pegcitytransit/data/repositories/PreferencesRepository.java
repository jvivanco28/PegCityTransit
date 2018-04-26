package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;

import jessevivanco.com.pegcitytransit.R;

public class PreferencesRepository {

    private static final String KEY_USE_24_HOUR_CLOCK = "24_hour_clock";
    private static final String KEY_MAP_SEARCH_RADIUS = "map_search_radius";
    private static final String KEY_LAST_USED_APP_VERSION_CODE = "last_app_version";

    private Context context;

    public PreferencesRepository(Context context) {
        this.context = context;
    }

    public void setUsing24HourClock(boolean enabled) {
        Prefs.putBoolean(KEY_USE_24_HOUR_CLOCK, enabled);
    }

    public boolean isUsing24HourClock() {
        return Prefs.getBoolean(KEY_USE_24_HOUR_CLOCK, false);
    }

    public void setMapSearchRadius(int mapSearchRadius) {
        Prefs.putInt(KEY_MAP_SEARCH_RADIUS, mapSearchRadius);
    }

    public int getMapSearchRadius() {
        return Prefs.getInt(KEY_MAP_SEARCH_RADIUS, context.getResources().getInteger(R.integer.default_map_search_radius));
    }

    public void setLastUsedAppVersionCode(int versionCode) {
        Prefs.putInt(KEY_LAST_USED_APP_VERSION_CODE, versionCode);
    }

    public int getLastUsedAppVersionCode() {
        return Prefs.getInt(KEY_LAST_USED_APP_VERSION_CODE, -1);
    }
}
