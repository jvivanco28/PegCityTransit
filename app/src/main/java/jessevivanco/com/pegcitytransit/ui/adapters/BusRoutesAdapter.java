package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.ui.adapters.base.BaseAdapter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.NoResultsCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteCell;

public class BusRoutesAdapter extends BaseAdapter<BusRouteViewModel, BusRouteCellViewHolder> {

    private BusRoutesAdapterCallbacks callbacks;

    public BusRoutesAdapter(@Nullable Bundle savedInstanceState,
                            BusRoutesAdapterCallbacks callbacks) {
        super(savedInstanceState, callbacks);

        this.callbacks = callbacks;
    }

    @Override
    protected BusRouteCellViewHolder createListItemViewHolder(ViewGroup parent) {
        return new BusRouteCellViewHolder(parent, callbacks);
    }

    @Override
    protected void bindListItemViewHolder(BusRouteViewModel item, BusRouteCellViewHolder holder) {
        holder.bind(item);
    }

    public interface BusRoutesAdapterCallbacks extends NoResultsCellViewHolder.OnRefreshButtonClickedListener, BusRouteCell.OnBusRouteSelectedListener {
        // Just combining the two interfaces.
    }
}