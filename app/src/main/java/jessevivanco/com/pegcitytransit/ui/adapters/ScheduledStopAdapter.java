package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<ScheduledStopViewHolder> {

    private static final String STATE_KEY_LIST = "list";

    private List<ScheduledStopViewModel> scheduledStops;

    public ScheduledStopAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            scheduledStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_LIST));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (scheduledStops != null) {
            outState.putParcelable(STATE_KEY_LIST, Parcels.wrap(scheduledStops));
        }
    }

    @Override
    public ScheduledStopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduledStopViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ScheduledStopViewHolder holder, int position) {
        holder.bind(scheduledStops.get(position));
    }

    @Override
    public int getItemCount() {
        return scheduledStops != null ? scheduledStops.size() : 0;
    }

    public void setScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        this.scheduledStops = scheduledStops;
        notifyDataSetChanged();
    }
}
