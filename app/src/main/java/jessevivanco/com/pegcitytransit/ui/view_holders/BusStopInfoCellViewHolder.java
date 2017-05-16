package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusStopInfoView;

public class BusStopInfoCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.bus_stop_info_view)
    BusStopInfoView busStopInfoView;

    public BusStopInfoCellViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);
    }

    public void bind(@Nullable BusStopViewModel busStop) {
        busStopInfoView.showBusStopInfo(busStop);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_bus_stop_info;
    }
}
