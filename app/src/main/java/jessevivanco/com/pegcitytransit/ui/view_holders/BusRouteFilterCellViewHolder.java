package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteFilterChangedListener;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteTextView;

public class BusRouteFilterCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.bus_route_text_view)
    BusRouteTextView busRouteTextView;

    @BindView(R.id.filter_checkmark)
    View filterCheckmark;

    private BusRouteViewModel busRoute;

    public BusRouteFilterCellViewHolder(ViewGroup parent, OnBusRouteFilterChangedListener onBusRouteFilterSelectedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);

        busRouteTextView.setOnClickListener(v -> {
            // Enable/disable the filter on the view model.
            busRoute.setFilterApplied(!busRoute.isFilterApplied());
            // Tell the listener to refresh the list with the filter enabled/disabled.
            onBusRouteFilterSelectedListener.onBusRouteFilterChanged();
        });
    }

    public void bind(BusRouteViewModel busRoute) {
        this.busRoute = busRoute;
        busRouteTextView.setBusRoute(busRoute);
        filterCheckmark.setVisibility(busRoute.isFilterApplied() ? View.VISIBLE : View.INVISIBLE);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_bus_route_text_view;
    }
}
