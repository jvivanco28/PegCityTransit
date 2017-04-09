package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.databinding.CellBusRouteBinding;

public class BusRouteCellViewHolder extends RecyclerView.ViewHolder {

    private CellBusRouteBinding binding;
    private OnBusRouteCellClickedListener onCellClickedListener;

    public BusRouteCellViewHolder(ViewGroup parent, OnBusRouteCellClickedListener onCellClickedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bus_route, parent, false));
        ButterKnife.bind(this, itemView);

        this.binding = DataBindingUtil.bind(itemView);
        this.onCellClickedListener = onCellClickedListener;
    }

    public void bind(BusRoute busRoute) {
        binding.setBusRoute(busRoute);
    }

    @OnClick
    public void onCellClicked() {
        onCellClickedListener.onBusRouteCellClicked(binding.getBusRoute());
    }

    public interface OnBusRouteCellClickedListener {

        void onBusRouteCellClicked(BusRoute busRoute);
    }
}
