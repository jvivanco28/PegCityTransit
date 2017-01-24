package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.adapters.base.BaseAdapter;
import jessevivanco.com.pegcitytransit.ui.provider.BusStopsProvider;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusStopCellViewHolder;

public class BusStopsAdapter extends BaseAdapter<BusStop> {

    private final int BUS_STOP_CELL_VIEW_TYPE_ID = R.layout.cell_bus_stop;

    private BusStopsProvider busStopsProvider;

    public BusStopsAdapter(BusStopsProvider busStopsProvider,
                           @Nullable Bundle savedInstanceState,
                           @Nullable OnListLoadedCallback listLoadedCallback) {
        super(savedInstanceState, listLoadedCallback, busStopsProvider);

        this.busStopsProvider = busStopsProvider;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BUS_STOP_CELL_VIEW_TYPE_ID) {
            return new BusStopCellViewHolder(parent);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getList() != null) {
            if (holder instanceof BusStopCellViewHolder) {
                ((BusStopCellViewHolder) holder).bind(getList().get(position));
            }
        }
    }

    @Override
    public int getDataViewType(int position) {
        return BUS_STOP_CELL_VIEW_TYPE_ID;
    }

    @Override
    public long getDataId(int position) {
        if (getList() != null && getList().get(position) != null) {
            return getList().get(position).getKey();
        }

        return RecyclerView.NO_ID;
    }

    @Override
    protected boolean dataHasStableIds() {
        return true;
    }

    @Override
    public void fetchData() {
        busStopsProvider.loadData(this);
    }

}
