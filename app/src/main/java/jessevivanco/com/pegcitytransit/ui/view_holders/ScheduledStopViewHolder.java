package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteTextView;

public class ScheduledStopViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.route_number)
    BusRouteTextView routeNumber;

    @BindView(R.id.route_name)
    TextView routeName;

    @BindView(R.id.arrival_time)
    TextView arrivalTime;

    public ScheduledStopViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_scheduled_stop, parent, false));
        ButterKnife.bind(this, itemView);
    }

    public void bind(ScheduledStopViewModel scheduledStop) {
        if (scheduledStop != null) {
            // TODO use setBusRoute()
            routeNumber.setText(String.valueOf(scheduledStop.getRouteNumber()));
            routeName.setText(scheduledStop.getRouteName());

            // TODO make this better
            arrivalTime.setText(scheduledStop.getEstimatedArrival() != null ? scheduledStop.getEstimatedArrival().toString() : scheduledStop.getScheduledArrival().toString());
        } else {
            routeNumber.setText(null);
            routeName.setText(null);
            arrivalTime.setText(null);
        }
    }
}
