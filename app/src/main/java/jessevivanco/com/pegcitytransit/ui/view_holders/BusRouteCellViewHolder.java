package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.databinding.CellBusRouteBinding;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteTextView;

public class BusRouteCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.bus_route_number)
    BusRouteTextView busRouteTextView;

    private CellBusRouteBinding binding;
    private OnBusRouteCellClickedListener onCellClickedListener;

    public BusRouteCellViewHolder(ViewGroup parent, OnBusRouteCellClickedListener onCellClickedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bus_route, parent, false));
        ButterKnife.bind(this, itemView);

        this.binding = DataBindingUtil.bind(itemView);
        this.onCellClickedListener = onCellClickedListener;
    }

    public void bind(BusRouteViewModel busRoute) {
        binding.setBusRoute(busRoute);
        busRouteTextView.setBusRoute(busRoute);
    }

    @OnClick
    public void onCellClicked() {
        onCellClickedListener.onBusRouteCellClicked(binding.getBusRoute());
    }

    public interface OnBusRouteCellClickedListener {

        void onBusRouteCellClicked(BusRouteViewModel busRoute);
    }
}
