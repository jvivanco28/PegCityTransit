package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.BusStopInfoCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String STATE_KEY_BUS_STOP = "bus_stop";
    private static final String STATE_KEY_SCHEDULE_LIST = "schedule";
    private static final int VIEW_TYPE_STOP_INFO_CELL = BusStopInfoCellViewHolder.getLayoutResId();
    private static final int VIEW_TYPE_SCHEDULE_CELL = ScheduledStopCellViewHolder.getLayoutResId();

    private BusStopViewModel busStop;
    private List<ScheduledStopViewModel> scheduledStops;

    public ScheduledStopAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            busStop = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_BUS_STOP));
            scheduledStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_SCHEDULE_LIST));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_STOP_INFO_CELL;
        } else {
            return VIEW_TYPE_SCHEDULE_CELL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == VIEW_TYPE_STOP_INFO_CELL ?
                new BusStopInfoCellViewHolder(parent) :
                new ScheduledStopCellViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BusStopInfoCellViewHolder) {
            ((BusStopInfoCellViewHolder) holder).bind(busStop);
        } else if (holder instanceof ScheduledStopCellViewHolder) {
            ((ScheduledStopCellViewHolder) holder).bind(scheduledStops.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        // The first cell will always be the bus stop info cell.
        return scheduledStops != null ?
                scheduledStops.size() + 1 :
                1;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (scheduledStops != null) {
            outState.putParcelable(STATE_KEY_BUS_STOP, Parcels.wrap(busStop));
            outState.putParcelable(STATE_KEY_SCHEDULE_LIST, Parcels.wrap(scheduledStops));
        }
    }

    public void setBusStop(BusStopViewModel busStop) {
        this.busStop = busStop;
        notifyItemChanged(0);
    }

    public void setScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        this.scheduledStops = scheduledStops;
        notifyDataSetChanged();
    }
}
