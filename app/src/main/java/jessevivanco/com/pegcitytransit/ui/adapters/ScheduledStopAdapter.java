package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.LoadingIndicatorCell;
import jessevivanco.com.pegcitytransit.ui.view_holders.NoResultsCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String STATE_KEY_SCHEDULE_LIST = "schedule";
    private static final String STATE_KEY_NO_RESULTS_MESSAGE = "schedule_no_results_message";

    private List<ScheduledStopViewModel> scheduledStops;
    private NoResultsCellViewHolder.OnRefreshButtonClickedListener onRefreshButtonClickedListener;

    private @Nullable
    String noResultsMessage;

    public ScheduledStopAdapter(@Nullable Bundle savedInstanceState, NoResultsCellViewHolder.OnRefreshButtonClickedListener onRefreshButtonClickedListener) {
        this.onRefreshButtonClickedListener = onRefreshButtonClickedListener;

        if (savedInstanceState != null) {
            scheduledStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_SCHEDULE_LIST));
            noResultsMessage = savedInstanceState.getString(STATE_KEY_NO_RESULTS_MESSAGE);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (scheduledStops != null && scheduledStops.size() > 0) {
            return ScheduledStopCellViewHolder.getLayoutResId();

        } else if ((scheduledStops == null || scheduledStops.size() == 0) && noResultsMessage != null) {
            return NoResultsCellViewHolder.getLayoutResId();

        } else {
            return LoadingIndicatorCell.getLayoutResId();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ScheduledStopCellViewHolder.getLayoutResId()) {
            return new ScheduledStopCellViewHolder(parent);

        } else if (viewType == NoResultsCellViewHolder.getLayoutResId()) {
            return new NoResultsCellViewHolder(parent, onRefreshButtonClickedListener);

        } else if (viewType == LoadingIndicatorCell.getLayoutResId()) {
            return new LoadingIndicatorCell(parent);
        }
        // Shouldn't get to this point.
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScheduledStopCellViewHolder) {
            ((ScheduledStopCellViewHolder) holder).bind(scheduledStops.get(position));
        } else if (holder instanceof NoResultsCellViewHolder) {

            ((NoResultsCellViewHolder) holder).setNoResultsText(noResultsMessage);
        }
    }

    @Override
    public int getItemCount() {
        return scheduledStops != null && scheduledStops.size() > 0 ?
                scheduledStops.size() :
                // We'll always show something. Either a loading indicator cell or no results cell.
                1;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (scheduledStops != null) {
            outState.putParcelable(STATE_KEY_SCHEDULE_LIST, Parcels.wrap(scheduledStops));
        }
        outState.putString(STATE_KEY_NO_RESULTS_MESSAGE, noResultsMessage);
    }

    public void setNoResultsMessage(@Nullable String noResultsMessage) {
        this.noResultsMessage = noResultsMessage;
        notifyDataSetChanged();
    }

    public void setScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        this.scheduledStops = scheduledStops;
        notifyDataSetChanged();
    }
}
