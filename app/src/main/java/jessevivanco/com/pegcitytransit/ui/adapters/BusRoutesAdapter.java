package jessevivanco.com.pegcitytransit.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRoutesAdapter extends RecyclerView.Adapter<BusRouteCellViewHolder> {

    private BusRoutesPresenter busRoutesPresenter;
    private BusRouteCellViewHolder.OnBusRouteCellClickedListener onBusRouteCellClickedListener;

    private List<BusRouteViewModel> busRoutes;

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

    public void setBusRoutes(List<BusRouteViewModel> routes) {
        this.busRoutes = routes;
        notifyDataSetChanged();
    }

    public List<BusRouteViewModel> getBusRoutes() {
        return busRoutes;
    }
}
