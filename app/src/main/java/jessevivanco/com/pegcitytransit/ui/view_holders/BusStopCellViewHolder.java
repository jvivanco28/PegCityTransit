package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.databinding.CellBusStopBinding;

public class BusStopCellViewHolder extends RecyclerView.ViewHolder {

    private CellBusStopBinding binding;
    private OnBusStopCellClickedListener onCellClickedListener;

    public BusStopCellViewHolder(ViewGroup parent, OnBusStopCellClickedListener onCellClickedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bus_stop, parent, false));
        ButterKnife.bind(this, itemView);

        this.binding = DataBindingUtil.bind(itemView);
        this.onCellClickedListener = onCellClickedListener;
    }

    public void bind(BusStop busStop) {
        binding.setBusStop(busStop);
    }

    @OnClick
    public void onCellClicked() {
        onCellClickedListener.onBusStopCellClicked(getAdapterPosition());
    }

    public interface OnBusStopCellClickedListener {

        void onBusStopCellClicked(int adapterPosition);
    }
}
