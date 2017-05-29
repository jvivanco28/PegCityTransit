package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.ui.adapters.base.BaseAdapter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteCell;

public class BusRoutesAdapter extends BaseAdapter<BusRouteViewModel, BusRouteCellViewHolder> {

    private BusRouteCell.OnBusRouteSelectedListener onBusRouteSelectedListener;

    public BusRoutesAdapter(@Nullable Bundle savedInstanceState,
                            BusRouteCell.OnBusRouteSelectedListener onBusRouteSelectedListener) {
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