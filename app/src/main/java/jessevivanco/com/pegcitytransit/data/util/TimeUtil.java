package jessevivanco.com.pegcitytransit.data.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

final public class TimeUtil {

    private TimeUtil() {
    }

    public static String getTimeFormatted(Date date, boolean use24HourTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return getTimeFormatted(cal, use24HourTime);
    }

    public static String getTimeFormatted(Calendar cal, boolean use24HourTime) {

        int hours = cal.get(use24HourTime ? Calendar.HOUR_OF_DAY : Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);

        if (use24HourTime) {
            return String.format(Locale.getDefault(),
                    "%02d:%02d",
                    hours,
                    minutes);
        } else {
            return String.format(Locale.getDefault(),
                    "%2d:%02d %s",
                    hours,
                    minutes,
                    cal.get(Calendar.AM_PM) == Calendar.AM ? "am" : "pm");
        }
    }


}
