package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.ui.adapters.base.SimpleBaseAdapter;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteSelectedListener;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRoutesAdapter extends SimpleBaseAdapter<BusRouteViewModel, BusRouteCellViewHolder> {

    private OnBusRouteSelectedListener onBusRouteSelectedListener;

    public BusRoutesAdapter(@Nullable Bundle savedInstanceState,
                            OnBusRouteSelectedListener onBusRouteSelectedListener) {
        super(savedInstanceState);
        this.onBusRouteSelectedListener = onBusRouteSelectedListener;
    }

    @Override
    protected BusRouteCellViewHolder createListItemViewHolder(ViewGroup parent) {
        return new BusRouteCellViewHolder(parent, onBusRouteSelectedListener);
    }

    @Override
    protected void bindListItemViewHolder(BusRouteViewModel item, BusRouteCellViewHolder holder) {
        holder.bind(item);
    }
}