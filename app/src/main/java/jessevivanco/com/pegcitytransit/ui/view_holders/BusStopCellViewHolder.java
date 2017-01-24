package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.databinding.CellBusStopBinding;
import jessevivanco.com.pegcitytransit.rest.models.BusStop;

public class BusStopCellViewHolder extends RecyclerView.ViewHolder {

    private CellBusStopBinding binding;

    public BusStopCellViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bus_stop, parent, false));

        this.binding = DataBindingUtil.bind(itemView);
    }

    public void bind(BusStop busStop) {
        binding.setBusStop(busStop);
    }
}
