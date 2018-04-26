package jessevivanco.com.pegcitytransit.ui.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteFilterSelectedListener;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteFilterCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRoutesFilterAdapter extends RecyclerView.Adapter<BusRouteFilterCellViewHolder> {

    private final OnBusRouteFilterSelectedListener onBusRouteFilterSelectedListener;

    private List<BusRouteViewModel> list;
    // Note we can only ever have one bus route applied as a filter.
    @Nullable
    private List<BusRouteViewModel> activeFilterList;

    public BusRoutesFilterAdapter(OnBusRouteFilterSelectedListener onBusRouteFilterSelectedListener) {
        this.onBusRouteFilterSelectedListener = onBusRouteFilterSelectedListener;
    }

    @Override
    public BusRouteFilterCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BusRouteFilterCellViewHolder(parent, onBusRouteFilterSelectedListener);
    }

    @Override
    public void onBindViewHolder(BusRouteFilterCellViewHolder holder, int position) {
        if (activeFilterList != null) {
            holder.bind(activeFilterList.get(position));
        } else {
            holder.bind(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (activeFilterList != null) {
            return activeFilterList.size();
        } else {
            return list != null ? list.size() : 0;
        }
    }

    public void setList(List<BusRouteViewModel> list, @Nullable BusRouteViewModel activeBusRouteFilter) {
        this.list = list;
        if (activeBusRouteFilter != null)
            this.activeFilterList = Collections.singletonList(activeBusRouteFilter);
        else
            this.activeFilterList = null;

        notifyDataSetChanged();
    }
}