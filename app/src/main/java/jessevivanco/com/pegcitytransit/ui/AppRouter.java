package jessevivanco.com.pegcitytransit.ui;

import android.content.Context;
import android.content.Intent;

import org.parceler.Parcels;

import jessevivanco.com.pegcitytransit.ui.activities.BusRouteMapActivity;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class AppRouter {

    public void goToStopScheduleScreen(Context context, BusRouteViewModel route) {
        // TODO
    }

    public void goToBusRouteMapScreen(Context context, BusRouteViewModel route) {
        Intent intent = new Intent(context, BusRouteMapActivity.class);
        intent.putExtra(BusRouteMapActivity.ARG_KEY_BUS_ROUTE, Parcels.wrap(route));
        context.startActivity(intent);
    }
}
