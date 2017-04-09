package jessevivanco.com.pegcitytransit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.ui.activities.BusStopScheduleActivity;

public class AppRouter {

    public void goToStopScheduleScreen(Context context, BusRoute busRoute) {
        Bundle bundle = new Bundle();

        // todo add bus route to bundle

        Intent intent = new Intent(context, BusStopScheduleActivity.class);
        context.startActivity(intent);
    }
}
