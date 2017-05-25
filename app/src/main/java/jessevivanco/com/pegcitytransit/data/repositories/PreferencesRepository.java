package jessevivanco.com.pegcitytransit.data.repositories;

import com.pixplicity.easyprefs.library.Prefs;

public class PreferencesRepository {

    private static final String KEY_USE_24_HOUR_CLOCK = "24_hour_clock";

    public void setUsing24HourClock(boolean enabled) {
        Prefs.putBoolean(KEY_USE_24_HOUR_CLOCK, enabled);
    }

    public boolean isUsing24HourClock() {
        return Prefs.getBoolean(KEY_USE_24_HOUR_CLOCK, false);
    }
}
