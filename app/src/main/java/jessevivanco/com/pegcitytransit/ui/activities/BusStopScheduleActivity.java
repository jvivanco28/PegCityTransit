package jessevivanco.com.pegcitytransit.ui.activities;

import android.os.Bundle;

import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;

public class BusStopScheduleActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_bus_stop_schedule;
    }
}
