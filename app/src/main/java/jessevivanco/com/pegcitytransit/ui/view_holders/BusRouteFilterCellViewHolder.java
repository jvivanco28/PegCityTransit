package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteFilterSelectedListener;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteSelectedListener;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteTextView;

public class BusRouteFilterCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.bus_route_text_view)
    BusRouteTextView busRouteTextView;

    private BusRouteViewModel busRoute;

    public BusRouteFilterCellViewHolder(ViewGroup parent, OnBusRouteFilterSelectedListener onBusRouteFilterSelectedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);

        // TODO should be able to disable filter
        busRouteTextView.setOnClickListener(v -> onBusRouteFilterSelectedListener.onBusRouteFilterSelected(busRoute, true));
    }

    public void bind(BusRouteViewModel busRoute) {
        this.busRoute = busRoute;
        busRouteTextView.setBusRoute(busRoute);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_bus_route_text_view;
    }
}
