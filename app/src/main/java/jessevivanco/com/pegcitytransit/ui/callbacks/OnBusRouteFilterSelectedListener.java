package jessevivanco.com.pegcitytransit.ui.callbacks;

import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public interface OnBusRouteFilterSelectedListener {

    void onBusRouteFilterSelected(BusRouteViewModel busRoute, boolean enableFilter);
}
