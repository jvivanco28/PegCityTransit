package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.NoResultsCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String STATE_KEY_SCHEDULE_LIST = "schedule";

    private List<ScheduledStopViewModel> scheduledStops;
    private NoResultsCellViewHolder.OnRefreshButtonClickedListener onRefreshButtonClickedListener;

    public ScheduledStopAdapter(@Nullable Bundle savedInstanceState, NoResultsCellViewHolder.OnRefreshButtonClickedListener onRefreshButtonClickedListener) {
        this.onRefreshButtonClickedListener = onRefreshButtonClickedListener;

        if (savedInstanceState != null) {
            scheduledStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_SCHEDULE_LIST));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (scheduledStops != null && scheduledStops.size() > 0) {
            return ScheduledStopCellViewHolder.getLayoutResId();
        } else {
            return NoResultsCellViewHolder.getLayoutResId();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ScheduledStopCellViewHolder.getLayoutResId()) {
            return new ScheduledStopCellViewHolder(parent);
        } else if (viewType == NoResultsCellViewHolder.getLayoutResId()) {
            return new NoResultsCellViewHolder(parent, onRefreshButtonClickedListener);
        }
        // Shouldn't get to this point.
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScheduledStopCellViewHolder) {
            ((ScheduledStopCellViewHolder) holder).bind(scheduledStops.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return scheduledStops != null && scheduledStops.size() > 0 ?
                scheduledStops.size() :
                1;
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
