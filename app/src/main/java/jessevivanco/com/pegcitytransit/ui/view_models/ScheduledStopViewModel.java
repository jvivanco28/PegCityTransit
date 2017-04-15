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

    // TODO integer.xml
    private static final int MAX_RELATIVE_MINUTES = 20;

    private ScheduledStopViewModel(Builder builder) {
        routeNumber = builder.routeNumber;
        routeName = builder.routeName;
        routeCoverage = builder.routeCoverage;
        departureTimeFormatted = builder.departureTimeFormatted;
        status = builder.status;
    }

    public static ScheduledStopViewModel createFromRouteSchedule(Context context, Integer routeNumber, RouteCoverage routeCoverage, ScheduledStop scheduledStop) {
        if (routeNumber == null || scheduledStop == null) {
            return null;
        } else {

            String departureTimeFormatted = null;
            String status = null;

            // TODO we gotta test the crap outta this.
            // Set our departure time text.
            if (scheduledStop.getTimes() != null) {

                if (scheduledStop.getTimes().getDeparture() != null && scheduledStop.getTimes().getDeparture().getEstimated() != null && scheduledStop.getTimes().getDeparture().getScheduled() != null) {

                    long timeDiffInMillis = scheduledStop.getTimes().getDeparture().getEstimated().getTime() - System.currentTimeMillis();
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiffInMillis);

                    departureTimeFormatted =
                            minutes == 0 ?
                                    context.getString(R.string.due) :
                                    minutes < MAX_RELATIVE_MINUTES ?
                                            Phrase.from(context, R.string.departs_in)
                                                    .put("time", String.valueOf(minutes))
                                                    .put("time_unit", context.getResources().getQuantityString(R.plurals.minutes, (int) minutes))
                                                    .format()
                                                    .toString() :
                                            getTimeFormatted(scheduledStop.getTimes().getDeparture().getEstimated());

                    if (scheduledStop.getTimes().getDeparture().getEstimated().getTime() > scheduledStop.getTimes().getDeparture().getScheduled().getTime()) {
                        status = context.getString(R.string.late);
                    } else if (scheduledStop.getTimes().getDeparture().getEstimated().getTime() < scheduledStop.getTimes().getDeparture().getScheduled().getTime()) {
                        status = context.getString(R.string.early);
                    } else {
                        status = context.getString(R.string.on_time);
                    }
                }
            }
            return new Builder()
                    .routeNumber(routeNumber)
                    .routeName(scheduledStop.getVariant() != null ? scheduledStop.getVariant().getName() : null)
                    .routeCoverage(routeCoverage)
                    .departureTimeFormatted(departureTimeFormatted)
                    .status(status)
                    .build();
        }
    }

    private static String getTimeFormatted(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);

        return String.format(Locale.getDefault(),
                "%02d:%02d",
                hours,
                minutes);
    }

    Integer routeNumber;
    String routeName;
    RouteCoverage routeCoverage;
    String departureTimeFormatted;
    String status;

    public ScheduledStopViewModel() {
    }

    public static int getMaxRelativeMinutes() {
        return MAX_RELATIVE_MINUTES;
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

    public static final class Builder {
        private Integer routeNumber;
        private String routeName;
        private RouteCoverage routeCoverage;
        private String departureTimeFormatted;
        private String status;

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

        public ScheduledStopViewModel build() {
            return new ScheduledStopViewModel(this);
        }
    }
}
