package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.adapters.BusRoutesFilterAdapter;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteFilterChangedListener;
import jessevivanco.com.pegcitytransit.ui.item_decorations.HorizontalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRouteFilterListCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.bus_route_filter_recycler_view)
    RecyclerView stopScheduleRecyclerView;
    LinearLayoutManager layoutManager;
    BusRoutesFilterAdapter busRoutesFilterAdapter;

    public BusRouteFilterListCellViewHolder(ViewGroup parent, OnBusRouteFilterChangedListener onBusRouteFilterSelectedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);
        init(onBusRouteFilterSelectedListener);
    }

    private void init(OnBusRouteFilterChangedListener onBusRouteFilterSelectedListener) {
        final Context context = itemView.getContext();

        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        stopScheduleRecyclerView.setLayoutManager(layoutManager);
        stopScheduleRecyclerView.addItemDecoration(new HorizontalListItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.material_spacing_small), 0));

        busRoutesFilterAdapter = new BusRoutesFilterAdapter(onBusRouteFilterSelectedListener);
        stopScheduleRecyclerView.setAdapter(busRoutesFilterAdapter);
    }

    public void bind(List<BusRouteViewModel> busRoutes) {
        busRoutesFilterAdapter.setList(busRoutes);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_bus_route_filter_list;
    }

}
