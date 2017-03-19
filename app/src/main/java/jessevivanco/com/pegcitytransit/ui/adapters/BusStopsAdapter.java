package jessevivanco.com.pegcitytransit.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.adapters.base.RefreshableAdapter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsAdapterPresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusStopCellViewHolder;

public class BusStopsAdapter extends RefreshableAdapter<BusStop> {

    private final int BUS_STOP_CELL_VIEW_TYPE_ID = R.layout.cell_bus_stop;

    private BusStopsAdapterPresenter busStopsPresenter;

    public BusStopsAdapter(Context context,
                           BusStopsAdapterPresenter busStopsProvider,
                           @Nullable Bundle savedInstanceState) {
        super(context, savedInstanceState, busStopsProvider);

        this.busStopsPresenter = busStopsProvider;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BUS_STOP_CELL_VIEW_TYPE_ID) {
            return new BusStopCellViewHolder(parent);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getList() != null) {
            if (holder instanceof BusStopCellViewHolder) {
                ((BusStopCellViewHolder) holder).bind(getList().get(position));
            }
        }
    }

    @Override
    public int getDataViewType(int position) {
        return BUS_STOP_CELL_VIEW_TYPE_ID;
    }

    @Override
    public long getDataId(int position) {
        if (getList() != null && getList().get(position) != null) {
            return getList().get(position).getKey();
        }

        return RecyclerView.NO_ID;
    }

    @Override
    protected boolean dataHasStableIds() {
        return true;
    }

    @Override
    public Observable<? extends List<BusStop>> fetchData() {
        return busStopsPresenter.loadData();
    }

    /**
     * Hooking into superclass impl. We need to load the routes for each bus stop.
     *
     * @param busStops
     */
    @Override
    protected void handleDataRetrieved(@Nullable List<BusStop> busStops) {
        super.handleDataRetrieved(busStops);

//        subscriptions.add(
//                busStopsPresenter.getRoutesForBusStops(busStops)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(busStop -> {
//
////                            Log.v("DEBUG", "busStopRoutes = " + busStop.getBusRoutes());
////
////                            // TODO refactor this
////                            // Probably not the best way of handling this, but good enough for now.
////                            int updateIndex = busStops.indexOf(busStop);
////
////                            // The item *should* exist, but just in case.
////                            if (updateIndex >= 0 && updateIndex < busStops.size()) {
////                                getList().set(updateIndex, busStop);
////                                notifyItemChanged(updateIndex);
////                            }
//                        }, throwable -> {
//                            // TODO report error?
//                            Log.e(LOG_TAG, "Error loading bus routes.", throwable);
//                        }, () -> {
//                            Log.v("DEBUG", "Observable COMPLETE!");
//                        })
//        );
    }
}
