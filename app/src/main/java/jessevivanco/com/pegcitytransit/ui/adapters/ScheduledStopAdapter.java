package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.QueryTimeCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class ScheduledStopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String STATE_KEY_LIST = ScheduledStopAdapter.class.getSimpleName() + "_list";
    private static final String STATE_KEY_QUERY_TIME = ScheduledStopAdapter.class.getSimpleName() + "_checked_time";

    private static final int VIEW_TYPE_QUERY_TIME = QueryTimeCellViewHolder.getLayoutResId();
    private static final int VIEW_TYPE_SCHEDULE = ScheduledStopCellViewHolder.getLayoutResId();

    private String queryTime;
    private List<ScheduledStopViewModel> list;
    private ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener;

    public ScheduledStopAdapter(ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener onBusRouteClickedListener, @Nullable Bundle savedInstanceState) {
        this.onBusRouteClickedListener = onBusRouteClickedListener;

        if (savedInstanceState != null) {
            list = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_LIST));
            queryTime = savedInstanceState.getString(STATE_KEY_QUERY_TIME);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ?
                // We're going to show a header cell displaying the query time
                list.size() + 1 :
                0;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ?
                VIEW_TYPE_QUERY_TIME :
                VIEW_TYPE_SCHEDULE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_QUERY_TIME) {
            return new QueryTimeCellViewHolder(parent);
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
        } else if (holder instanceof ScheduledStopCellViewHolder) {
            ((ScheduledStopCellViewHolder) holder).bind(list.get(position - 1));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (list != null) {
            outState.putParcelable(STATE_KEY_LIST, Parcels.wrap(list));
            outState.putString(STATE_KEY_QUERY_TIME, queryTime);
        }
    }

    public void setList(List<ScheduledStopViewModel> list, String queryTime) {
        this.list = list;
        this.queryTime = queryTime;
        notifyDataSetChanged();
    }
}

