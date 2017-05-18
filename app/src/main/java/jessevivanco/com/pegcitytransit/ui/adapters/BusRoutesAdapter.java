package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteCell;

public class BusRoutesAdapter extends RecyclerView.Adapter<BusRouteCellViewHolder> {

    private static final String STATE_KEY_LIST = "list";

    private BusRouteCell.OnBusRouteSelectedListener onBusRouteCellClickedListener;

    private List<BusRouteViewModel> busRoutes;

    public BusRoutesAdapter(@Nullable Bundle savedInstanceState,
                            BusRouteCell.OnBusRouteSelectedListener onBusRouteCellClickedListener) {

        this.onBusRouteCellClickedListener = onBusRouteCellClickedListener;

        if (savedInstanceState != null) {
            busRoutes = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_LIST));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_KEY_LIST, Parcels.wrap(busRoutes));
    }

    @Override
    public BusRouteCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BusRouteCellViewHolder(parent, onBusRouteCellClickedListener);
    }

    @Override
    public void onBindViewHolder(BusRouteCellViewHolder holder, int position) {
        holder.bind(busRoutes.get(position));
    }

    @Override
    public int getItemCount() {
        return busRoutes != null ? busRoutes.size() : 0;
    }

    public void setBusRoutes(List<BusRouteViewModel> routes) {
        this.busRoutes = routes;
        notifyDataSetChanged();
    }
}
