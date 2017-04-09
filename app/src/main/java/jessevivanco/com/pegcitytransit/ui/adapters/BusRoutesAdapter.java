package jessevivanco.com.pegcitytransit.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;

public class BusRoutesAdapter extends RecyclerView.Adapter<BusRouteCellViewHolder> {

    private BusRoutesPresenter busRoutesPresenter;
    private BusRouteCellViewHolder.OnBusRouteCellClickedListener onBusRouteCellClickedListener;

    private List<BusRoute> busRoutes;

    public BusRoutesAdapter(BusRoutesPresenter busRoutesPresenter, BusRouteCellViewHolder.OnBusRouteCellClickedListener onBusRouteCellClickedListener) {
        this.busRoutesPresenter = busRoutesPresenter;
        this.onBusRouteCellClickedListener = onBusRouteCellClickedListener;
    }

    @Override
    public BusRouteCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BusRouteCellViewHolder(parent, onBusRouteCellClickedListener);
    }

    @Override
    public void onBindViewHolder(BusRouteCellViewHolder holder, int position) {
        holder.bind(busRoutes.get(position));
    }

    @Override
    public int getItemCount() {
        return busRoutes != null ? busRoutes.size() : 0;
    }

    public BusRoutesPresenter getBusRoutesPresenter() {
        return busRoutesPresenter;
    }

    public void setBusRoutes(List<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
        notifyDataSetChanged();
    }

    public List<BusRoute> getBusRoutes() {
        return busRoutes;
    }
}
