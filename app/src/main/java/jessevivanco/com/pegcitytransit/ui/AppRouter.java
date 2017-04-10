package jessevivanco.com.pegcitytransit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import jessevivanco.com.pegcitytransit.ui.activities.BusStopScheduleActivity;
import jessevivanco.com.pegcitytransit.ui.view_model.BusRouteViewModel;

public class AppRouter {

    public void goToStopScheduleScreen(Context context, BusRouteViewModel busRoute) {
        Bundle bundle = new Bundle();

        // todo add bus route to bundle

        Intent intent = new Intent(context, BusStopScheduleActivity.class);
        context.startActivity(intent);
    }
}
