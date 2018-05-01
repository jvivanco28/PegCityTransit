package jessevivanco.com.pegcitytransit.ui.view_models;

import android.content.Context;

import com.squareup.phrase.Phrase;

import java.util.Date;
import java.util.List;

import io.reactivex.annotations.NonNull;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.util.TimeUtil;

public class BusStopScheduleViewModel {

    @NonNull
    private List<ScheduledStopViewModel> scheduledStops;
    @NonNull
    private List<BusRouteViewModel> busRoutes;
    @NonNull
    private String queryTime;

    public BusStopScheduleViewModel(List<ScheduledStopViewModel> scheduledStops,
                                    List<BusRouteViewModel> busRoutes,
                                    Date queryTime,
                                    boolean use24HourTime,
                                    Context context) {

        this.scheduledStops = scheduledStops;
        this.busRoutes = busRoutes;

        this.queryTime = Phrase.from(context.getString(R.string.checked_at))
                .put("time", TimeUtil.getTimeFormatted(queryTime, use24HourTime))
                .format()
                .toString();
    }

    public List<ScheduledStopViewModel> getScheduledStops() {
        return scheduledStops;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public List<BusRouteViewModel> getBusRoutes() {
        return busRoutes;
    }
}
