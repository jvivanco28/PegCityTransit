package jessevivanco.com.pegcitytransit.ui.view_models;

import org.parceler.Parcel;

import java.util.Date;

import jessevivanco.com.pegcitytransit.data.rest.models.ScheduledStop;

@Parcel
public class ScheduledStopViewModel {

    public static ScheduledStopViewModel createFromRouteSchedule(Integer routeNumber, ScheduledStop scheduledStop) {
        if (routeNumber == null || scheduledStop == null) {
            return null;
        } else {
            return new Builder()
                    .routeNumber(routeNumber)
                    .routeName(scheduledStop.getVariant() != null ? scheduledStop.getVariant().getName() : null)
                    .scheduledArrival(scheduledStop.getTimes() != null ? scheduledStop.getTimes().getArrival().getScheduled() : null)
                    .estimatedArrival(scheduledStop.getTimes() != null ? scheduledStop.getTimes().getArrival().getEstimated() : null)
                    .scheduledDeparture(scheduledStop.getTimes() != null ? scheduledStop.getTimes().getDeparture().getScheduled() : null)
                    .scheduledDeparture(scheduledStop.getTimes() != null ? scheduledStop.getTimes().getDeparture().getEstimated() : null)
                    .build();
        }
    }

    Integer routeNumber;
    String routeName;
    Date scheduledArrival;
    Date estimatedArrival;
    Date scheduledDeparture;
    Date estimatedDeparture;

    public ScheduledStopViewModel() {
    }

    private ScheduledStopViewModel(Builder builder) {
        routeNumber = builder.routeNumber;
        routeName = builder.routeName;
        scheduledArrival = builder.scheduledArrival;
        estimatedArrival = builder.estimatedArrival;
        scheduledDeparture = builder.scheduledDeparture;
        estimatedDeparture = builder.estimatedDeparture;
    }

    public Integer getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public Date getScheduledArrival() {
        return scheduledArrival;
    }

    public Date getEstimatedArrival() {
        return estimatedArrival;
    }

    public Date getScheduledDeparture() {
        return scheduledDeparture;
    }

    public Date getEstimatedDeparture() {
        return estimatedDeparture;
    }

    public static final class Builder {
        private Integer routeNumber;
        private String routeName;
        private Date scheduledArrival;
        private Date estimatedArrival;
        private Date scheduledDeparture;
        private Date estimatedDeparture;

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

        public Builder scheduledArrival(Date val) {
            scheduledArrival = val;
            return this;
        }

        public Builder estimatedArrival(Date val) {
            estimatedArrival = val;
            return this;
        }

        public Builder scheduledDeparture(Date val) {
            scheduledDeparture = val;
            return this;
        }

        public Builder estimatedDeparture(Date val) {
            estimatedDeparture = val;
            return this;
        }

        public ScheduledStopViewModel build() {
            return new ScheduledStopViewModel(this);
        }
    }
}
