package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.ui.adapters.base.SimpleBaseAdapter;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends SimpleBaseAdapter<ScheduledStopViewModel, ScheduledStopCellViewHolder> {

    private ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener;

    public ScheduledStopAdapter(ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener, @Nullable Bundle savedInstanceState) {
        super(savedInstanceState);
        this.onBusRouteClickedListener = onBusRouteClickedListener;
    }

    @Override
    protected ScheduledStopCellViewHolder createListItemViewHolder(ViewGroup parent) {
        return new ScheduledStopCellViewHolder(parent, onBusRouteClickedListener);
    }

    @Override
    protected void bindListItemViewHolder(ScheduledStopViewModel item, ScheduledStopCellViewHolder holder) {
        holder.bind(item);
    }
}
