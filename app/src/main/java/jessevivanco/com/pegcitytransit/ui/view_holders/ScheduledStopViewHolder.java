package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

            Log.v("DEBUG", "poop " + scheduledStop.getScheduledDeparture() + " -- " + scheduledStop.getEstimatedDeparture());

            // TODO make this better
            // Sometimes the API doesn't give us these values. No sure why.
            if (scheduledStop.getScheduledArrival() != null && scheduledStop.getEstimatedArrival() != null) {

                // Check if the bus is late.
                if (scheduledStop.getEstimatedArrival().after(scheduledStop.getScheduledArrival())) {
                    arrivalTime.setText("1 LATE " + scheduledStop.getEstimatedArrival());
                } else {
                    arrivalTime.setText("1 OK " + scheduledStop.getEstimatedArrival());
                }
            } else if (scheduledStop.getEstimatedDeparture() != null && scheduledStop.getScheduledDeparture() != null) {
                // These shouldn't be null, but gonna stay on the safe side.

                // Check if the bus is late.
                if (scheduledStop.getEstimatedDeparture().after(scheduledStop.getScheduledDeparture())) {
                    arrivalTime.setText("2 LATE " + scheduledStop.getEstimatedDeparture());
                } else {
                    arrivalTime.setText("2 OK " + scheduledStop.getEstimatedDeparture());
                }
            } else {
                Log.v("DEBUG", "derp");
                arrivalTime.setText(null);
            }
        } else {
            routeNumber.setText(null);
            routeName.setText(null);
            arrivalTime.setText(null);
        }
    }
}
