package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteTextView;

public class ScheduledStopCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.route_number)
    BusRouteTextView routeNumber;

    @BindView(R.id.route_name)
    TextView routeName;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.departure_time)
    IconTextView departureTime;

    public ScheduledStopCellViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);
    }

    public void bind(ScheduledStopViewModel scheduledStop) {
        if (scheduledStop != null) {
            routeNumber.setBusRoute(scheduledStop.getRouteNumber(), scheduledStop.getRouteCoverage());
            routeName.setText(scheduledStop.getRouteName());
            status.setText(scheduledStop.getStatus());
            status.setTextColor(scheduledStop.getStatusColor());
            departureTime.setText("{" + MaterialIcons.md_access_time.key() + "} " + scheduledStop.getDepartureTimeFormatted());
        } else {
            routeNumber.setText(null);
            routeName.setText(null);
            status.setText(null);
            departureTime.setText(null);
        }
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_scheduled_stop;
    }
}
