package jessevivanco.com.pegcitytransit.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusStopCellViewHolder;

public class BusStopsAdapter extends RecyclerView.Adapter<BusStopCellViewHolder> {

    private BusStopsPresenter busStopsPresenter;
    private List<BusStop> busStops;

    public BusStopsAdapter(BusStopsPresenter busStopsPresenter) {
        this.busStopsPresenter = busStopsPresenter;
    }

    @Override
    public BusStopCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BusStopCellViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BusStopCellViewHolder holder, int position) {
        holder.bind(busStops.get(position));
    }

    @Override
    public int getItemCount() {
        return busStops != null ? busStops.size() : 0;
    }

    public BusStopsPresenter getBusStopsPresenter() {
        return busStopsPresenter;
    }

    public void setBusStops(List<BusStop> busStops) {
        this.busStops = busStops;
        notifyDataSetChanged();
    }
}
