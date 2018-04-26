package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteFilterChangedListener;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteFilterListCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.QueryTimeCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String STATE_KEY_FULL_STOPS_LIST = ScheduledStopAdapter.class.getSimpleName() + "_list";
    private static final String STATE_KEY_FILTERED_LIST = ScheduledStopAdapter.class.getSimpleName() + "_filtered_list";
    private static final String STATE_KEY_ROUTES_LIST = ScheduledStopAdapter.class.getSimpleName() + "_routes";
    private static final String STATE_KEY_QUERY_TIME = ScheduledStopAdapter.class.getSimpleName() + "_checked_time";

    private static final int VIEW_TYPE_BUS_ROUTE_FILTER = BusRouteFilterListCellViewHolder.getLayoutResId();
    private static final int VIEW_TYPE_QUERY_TIME = QueryTimeCellViewHolder.getLayoutResId();
    private static final int VIEW_TYPE_SCHEDULE = ScheduledStopCellViewHolder.getLayoutResId();

    private String queryTime;
    private List<ScheduledStopViewModel> fullStopList;
    private List<BusRouteViewModel> busRoutes;

    /**
     * NOTE: We always show the filtered list. If no filter is applied to the list, then this just
     * references the full {@code fullStopList}.
     */
    private List<ScheduledStopViewModel> filteredList;

    /**
     * Bus route filter listener in the header cell. This will alter the stop list.
     */
    private OnBusRouteFilterChangedListener onBusRouteFilterSelectedListener;

    /**
     * Bus route listener for each stop cell. This will load the entire route on the map.
     */
    private ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener;

    public ScheduledStopAdapter(OnBusRouteFilterChangedListener onBusRouteFilterSelectedListener, ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener, @Nullable Bundle savedInstanceState) {
        this.onBusRouteFilterSelectedListener = onBusRouteFilterSelectedListener;
        this.onBusRouteClickedListener = onBusRouteClickedListener;

        if (savedInstanceState != null) {
            fullStopList = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_FULL_STOPS_LIST));
            filteredList = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_FILTERED_LIST));
            busRoutes = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_ROUTES_LIST));
            queryTime = savedInstanceState.getString(STATE_KEY_QUERY_TIME);

            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return filteredList != null ?
                // We're going to show two header cells:
                // 1. Bus Route filter
                // 2. Query time
                filteredList.size() + 2 :
                0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_BUS_ROUTE_FILTER;
        else if (position == 1)
            return VIEW_TYPE_QUERY_TIME;
        else
            return VIEW_TYPE_SCHEDULE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BUS_ROUTE_FILTER) {
            return new BusRouteFilterListCellViewHolder(parent, onBusRouteFilterSelectedListener);
        } else if (viewType == VIEW_TYPE_QUERY_TIME) {
            return new QueryTimeCellViewHolder(parent);
        } else if (viewType == VIEW_TYPE_SCHEDULE) {
            return new ScheduledStopCellViewHolder(parent, onBusRouteClickedListener);
        }
        // Shouldn't happen.
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BusRouteFilterListCellViewHolder) {
            ((BusRouteFilterListCellViewHolder) holder).bind(busRoutes);
        } else if (holder instanceof QueryTimeCellViewHolder) {
            ((QueryTimeCellViewHolder) holder).bind(queryTime);
        } else if (holder instanceof ScheduledStopCellViewHolder) {
            ((ScheduledStopCellViewHolder) holder).bind(filteredList.get(position - 2));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (fullStopList != null) {
            outState.putParcelable(STATE_KEY_FULL_STOPS_LIST, Parcels.wrap(fullStopList));
            outState.putParcelable(STATE_KEY_FILTERED_LIST, Parcels.wrap(filteredList));
            outState.putParcelable(STATE_KEY_ROUTES_LIST, Parcels.wrap(busRoutes));
            outState.putString(STATE_KEY_QUERY_TIME, queryTime);
        }
    }

    public void setFullStopList(List<ScheduledStopViewModel> stops,
                                List<BusRouteViewModel> busRoutes,
                                String queryTime) {

        this.fullStopList = stops;
        this.busRoutes = busRoutes;
        this.queryTime = queryTime;

        // Clear out filters.
        this.filteredList = stops;

        notifyDataSetChanged();
    }

    public void setFilteredList(List<ScheduledStopViewModel> filteredList) {
        this.filteredList = filteredList;

        notifyDataSetChanged();
    }

    public void clearFilters() {
        this.filteredList = fullStopList;

        notifyDataSetChanged();
    }

    public List<ScheduledStopViewModel> getFullStopList() {
        return fullStopList;
    }

    public List<BusRouteViewModel> getBusRoutes() {
        return busRoutes;
    }
}

