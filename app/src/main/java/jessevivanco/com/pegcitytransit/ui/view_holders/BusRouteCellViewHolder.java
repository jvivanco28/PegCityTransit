package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteCell;

public class BusRouteCellViewHolder extends RecyclerView.ViewHolder {

    private BusRouteCell busRouteCell;

    public BusRouteCellViewHolder(ViewGroup parent, BusRouteCell.OnBusRouteSelectedListener onCellClickedListener) {
        super(new BusRouteCell(parent.getContext()));

        this.busRouteCell = (BusRouteCell) itemView;
        busRouteCell.setOnCellClickedListener(onCellClickedListener);
    }

    public void bind(BusRouteViewModel busRoute) {
        busRouteCell.bind(busRoute);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return BusRouteCell.getLayoutResId();
    }
}
