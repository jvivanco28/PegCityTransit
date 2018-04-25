package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.view_holders.BasicViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteFilterListCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.QueryTimeCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static int POSITION_QUERY_TIME_CELL = 0;
    public static int POSITION_BUS_ROUTE_FILTER_LIST_CELL = 1;
    public static int POSITION_SCHEDULE_TITLE_CELL = 2;

    private static final String STATE_KEY_LIST = ScheduledStopAdapter.class.getSimpleName() + "_list";
    private static final String STATE_KEY_QUERY_TIME = ScheduledStopAdapter.class.getSimpleName() + "_checked_time";

    private static final int VIEW_TYPE_BUS_ROUTE_FILTER = BusRouteFilterListCellViewHolder.getLayoutResId();
    private static final int VIEW_TYPE_QUERY_TIME = QueryTimeCellViewHolder.getLayoutResId();
    private static final int VIEW_TYPE_SCHEDULE_TITLE = 0; // We don't have a viewholder class for this so just use some arbitrary ID.
    private static final int VIEW_TYPE_SCHEDULE = ScheduledStopCellViewHolder.getLayoutResId();

    private String queryTime;
    private List<ScheduledStopViewModel> stopList;
    private List<BusRouteViewModel> busRoutes;
    private ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener;

    public ScheduledStopAdapter(ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener, @Nullable Bundle savedInstanceState) {
        this.onBusRouteClickedListener = onBusRouteClickedListener;

        if (savedInstanceState != null) {
            stopList = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_LIST));
            queryTime = savedInstanceState.getString(STATE_KEY_QUERY_TIME);
        }
    }

    @Override
    public int getItemCount() {
        return stopList != null ?
                // We're going to show two header cells:
                // 1. Bus Route filter
                // 2. Query time
                stopList.size() + 2 :
                0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == POSITION_QUERY_TIME_CELL)
            return VIEW_TYPE_QUERY_TIME;
        else if (position == POSITION_BUS_ROUTE_FILTER_LIST_CELL)
            return VIEW_TYPE_BUS_ROUTE_FILTER;
        else if (position == POSITION_SCHEDULE_TITLE_CELL)
            return VIEW_TYPE_SCHEDULE_TITLE;
        else
            return VIEW_TYPE_SCHEDULE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_QUERY_TIME) {
            return new QueryTimeCellViewHolder(parent);
        } else if (viewType == VIEW_TYPE_BUS_ROUTE_FILTER) {
            return new BusRouteFilterListCellViewHolder(parent);
        } else if (viewType == VIEW_TYPE_SCHEDULE_TITLE) {
            TextView scheduleTitle = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_header_cell, null);
            scheduleTitle.setText(parent.getContext().getString(R.string.schedule));
            return new BasicViewHolder<>(scheduleTitle);
        } else if (viewType == VIEW_TYPE_SCHEDULE) {
            return new ScheduledStopCellViewHolder(parent, onBusRouteClickedListener);
        }
        // Shouldn't happen.
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof QueryTimeCellViewHolder) {
            ((QueryTimeCellViewHolder) holder).bind(queryTime);
        } else if (holder instanceof BusRouteFilterListCellViewHolder) {
            ((BusRouteFilterListCellViewHolder) holder).bind(busRoutes);
        } else if (holder instanceof ScheduledStopCellViewHolder) {
            ((ScheduledStopCellViewHolder) holder).bind(stopList.get(position - 2));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (stopList != null) {
            outState.putParcelable(STATE_KEY_LIST, Parcels.wrap(stopList));
            outState.putString(STATE_KEY_QUERY_TIME, queryTime);
        }
    }

    public void setList(List<ScheduledStopViewModel> list, List<BusRouteViewModel> busRoutes, String queryTime) {
        this.stopList = list;
        this.busRoutes = busRoutes;
        this.queryTime = queryTime;
        notifyDataSetChanged();
    }
}

