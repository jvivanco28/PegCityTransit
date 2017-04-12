package jessevivanco.com.pegcitytransit.ui;

import android.content.Context;
import android.content.Intent;

import org.parceler.Parcels;

import jessevivanco.com.pegcitytransit.ui.activities.BusRouteMapActivity;
import jessevivanco.com.pegcitytransit.ui.activities.BusStopScheduleActivity;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class AppRouter {

    public void goToStopScheduleScreen(Context context, BusStopViewModel stop) {
        Intent intent = new Intent(context, BusStopScheduleActivity.class);
        intent.putExtra(BusStopScheduleActivity.ARG_KEY_BUS_STOP, Parcels.wrap(stop));
        context.startActivity(intent);
    }

    public void goToBusRouteMapScreen(Context context, BusRouteViewModel route) {
        Intent intent = new Intent(context, BusRouteMapActivity.class);
        intent.putExtra(BusRouteMapActivity.ARG_KEY_BUS_ROUTE, Parcels.wrap(route));
        context.startActivity(intent);
    }
}
