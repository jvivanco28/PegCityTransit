package jessevivanco.com.pegcitytransit.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteFilterChangedListener;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteFilterCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRoutesFilterAdapter extends RecyclerView.Adapter<BusRouteFilterCellViewHolder> {

    private final OnBusRouteFilterChangedListener onBusRouteFilterSelectedListener;

    private List<BusRouteViewModel> list;

    public BusRoutesFilterAdapter(OnBusRouteFilterChangedListener onBusRouteFilterSelectedListener) {
        this.onBusRouteFilterSelectedListener = onBusRouteFilterSelectedListener;
    }

    @Override
    public BusRouteFilterCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BusRouteFilterCellViewHolder(parent, onBusRouteFilterSelectedListener);
    }

    @Override
    public void onBindViewHolder(BusRouteFilterCellViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void setList(List<BusRouteViewModel> list) {
        this.list = list;

        notifyDataSetChanged();
    }
}