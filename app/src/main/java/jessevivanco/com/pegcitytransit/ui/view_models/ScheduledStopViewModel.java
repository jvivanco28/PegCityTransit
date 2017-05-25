package jessevivanco.com.pegcitytransit.ui.view_models;

import android.content.Context;

import com.squareup.phrase.Phrase;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.ScheduledStop;
import jessevivanco.com.pegcitytransit.ui.util.RouteCoverage;

@Parcel
public class ScheduledStopViewModel {

    private ScheduledStopViewModel(Builder builder) {
        routeNumber = builder.routeNumber;
        routeName = builder.routeName;
        routeCoverage = builder.routeCoverage;
        departureTimeFormatted = builder.departureTimeFormatted;
        departureTime = builder.departureTime;
        status = builder.status;
    }

    public static ScheduledStopViewModel createFromRouteSchedule(Context context,
                                                                 Integer routeNumber,
                                                                 RouteCoverage routeCoverage,
                                                                 ScheduledStop scheduledStop,
                                                                 final int MAX_RELATIVE_MINUTES,
                                                                 boolean use24HourTime) {
        if (routeNumber == null || scheduledStop == null) {
            return null;
        } else {

            String departureTimeFormatted;
            String status;
            Date departureTime;

            // Set our departure time text.
            long timeDiffInMillis = scheduledStop.getTimes().getDeparture().getEstimated().getTime() - System.currentTimeMillis();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiffInMillis);

            departureTime = scheduledStop.getTimes().getDeparture().getEstimated();

            departureTimeFormatted =
                    minutes == 0 ?
                            context.getString(R.string.due) :
                            minutes < MAX_RELATIVE_MINUTES ?
                                    Phrase.from(context, R.string.departs_in)
                                            .put("time", String.valueOf(minutes))
                                            .put("time_unit", context.getResources().getQuantityString(R.plurals.minutes, (int) minutes))
                                            .format()
                                            .toString() :
                                    getTimeFormatted(scheduledStop.getTimes().getDeparture().getEstimated(), use24HourTime);

            if (scheduledStop.getTimes().getDeparture().getEstimated().getTime() > scheduledStop.getTimes().getDeparture().getScheduled().getTime()) {
                status = context.getString(R.string.late);
            } else if (scheduledStop.getTimes().getDeparture().getEstimated().getTime() < scheduledStop.getTimes().getDeparture().getScheduled().getTime()) {
                status = context.getString(R.string.early);
            } else {
                status = context.getString(R.string.on_time);
            }
            return new Builder()
                    .routeNumber(routeNumber)
                    .routeName(scheduledStop.getVariant() != null ? scheduledStop.getVariant().getName() : null)
                    .routeCoverage(routeCoverage)
                    .departureTimeFormatted(departureTimeFormatted)
                    .departureTime(departureTime)
                    .status(status)
                    .build();
        }
    }

    private static String getTimeFormatted(Date date, boolean use24HourTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

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

    Integer routeNumber;
    String routeName;
    RouteCoverage routeCoverage;
    String departureTimeFormatted;
    Date departureTime;
    String status;

    public ScheduledStopViewModel() {
    }

    public Integer getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public RouteCoverage getRouteCoverage() {
        return routeCoverage;
    }

    public String getDepartureTimeFormatted() {
        return departureTimeFormatted;
    }

    public String getStatus() {
        return status;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public static final class Builder {
        private Integer routeNumber;
        private String routeName;
        private RouteCoverage routeCoverage;
        private String departureTimeFormatted;
        private String status;
        private Date departureTime;

        public Builder() {
        }

        public Builder routeNumber(Integer val) {
            routeNumber = val;
            return this;
        }

        public Builder routeName(String val) {
            routeName = val;
            return this;
        }

        public Builder routeCoverage(RouteCoverage val) {
            routeCoverage = val;
            return this;
        }

        public Builder departureTimeFormatted(String val) {
            departureTimeFormatted = val;
            return this;
        }

        public Builder status(String val) {
            status = val;
            return this;
        }

        public Builder departureTime(Date val) {
            departureTime = val;
            return this;
        }

        public ScheduledStopViewModel build() {
            return new ScheduledStopViewModel(this);
        }
    }
}
