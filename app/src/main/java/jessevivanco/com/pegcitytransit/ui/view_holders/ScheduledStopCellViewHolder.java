package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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

    @BindView(R.id.has_wifi)
    IconTextView wifiIcon;

    private OnBusRouteNumberClickedListener onBusRouteClickedListener;

    public ScheduledStopCellViewHolder(ViewGroup parent, OnBusRouteNumberClickedListener onBusRouteClickedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);
        this.onBusRouteClickedListener = onBusRouteClickedListener;
    }

    public void bind(ScheduledStopViewModel scheduledStop) {
        if (scheduledStop != null) {
            routeNumber.setBusRoute(scheduledStop.getRouteNumber(), scheduledStop.getRouteCoverage());
            routeNumber.setOnClickListener(v -> onBusRouteClickedListener.onBusRouteNumberClicked(scheduledStop.getRouteNumber()));
            routeName.setText(scheduledStop.getRouteName());
            status.setText(scheduledStop.getStatus());
            status.setTextColor(scheduledStop.getStatusColor());
            departureTime.setText("{" + MaterialIcons.md_access_time.key() + "} " + scheduledStop.getDepartureTimeFormatted());
            wifiIcon.setVisibility(scheduledStop.isHasWifi() ? View.VISIBLE : View.GONE);
        } else {
            routeNumber.setText(null);
            routeNumber.setOnClickListener(null);
            routeName.setText(null);
            status.setText(null);
            departureTime.setText(null);
            wifiIcon.setVisibility(View.GONE);
        }
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_scheduled_stop;
    }

    public interface OnBusRouteNumberClickedListener {

        void onBusRouteNumberClicked(Integer busRouteNumber);
    }
}
