package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.ui.adapters.base.BaseAdapter;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends BaseAdapter<ScheduledStopViewModel, ScheduledStopCellViewHolder> {

    public ScheduledStopAdapter(@Nullable Bundle savedInstanceState) {
        super(savedInstanceState);
    }

    @Override
    protected ScheduledStopCellViewHolder createListItemViewHolder(ViewGroup parent) {
        return new ScheduledStopCellViewHolder(parent);
    }

    @Override
    protected void bindListItemViewHolder(ScheduledStopViewModel item, ScheduledStopCellViewHolder holder) {
        holder.bind(item);
    }
}
