package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.ui.adapters.base.SimpleBaseAdapter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteFilterCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRoutesFilterAdapter extends SimpleBaseAdapter<BusRouteViewModel, BusRouteFilterCellViewHolder> {

    public BusRoutesFilterAdapter(@Nullable Bundle savedInstanceState) {
//                                 OnBusRouteSelectedListener onBusRouteSelectedListener) {
        super(savedInstanceState);
//        this.onBusRouteSelectedListener = onBusRouteSelectedListener;
    }

    @Override
    protected BusRouteFilterCellViewHolder createListItemViewHolder(ViewGroup parent) {
        return new BusRouteFilterCellViewHolder(parent);
    }

    @Override
    protected void bindListItemViewHolder(BusRouteViewModel item, BusRouteFilterCellViewHolder holder) {
        holder.bind(item);
    }
}