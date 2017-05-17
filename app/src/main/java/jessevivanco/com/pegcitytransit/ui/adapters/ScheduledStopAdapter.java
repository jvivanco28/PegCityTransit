package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<ScheduledStopCellViewHolder> {

    private static final String STATE_KEY_SCHEDULE_LIST = "schedule";

    private List<ScheduledStopViewModel> scheduledStops;

    public ScheduledStopAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            scheduledStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_SCHEDULE_LIST));
        }
    }

    @Override
    public ScheduledStopCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduledStopCellViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ScheduledStopCellViewHolder holder, int position) {
        holder.bind(scheduledStops.get(position));
    }

    @Override
    public int getItemCount() {
        return scheduledStops != null ?
                scheduledStops.size() :
                0;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (scheduledStops != null) {
            outState.putParcelable(STATE_KEY_SCHEDULE_LIST, Parcels.wrap(scheduledStops));
        }
    }

    public void setScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        this.scheduledStops = scheduledStops;
        notifyDataSetChanged();
    }
}
