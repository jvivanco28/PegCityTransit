package jessevivanco.com.pegcitytransit.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.fragments.BusRoutesFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.BusStopTimetablesFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.BusStopsMapFragment;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setupBottomNav(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    private void setupBottomNav(@Nullable Bundle savedInstanceState) {
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {

            Fragment fragment = null;
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content);

            switch (item.getItemId()) {
                case R.id.stops:
                    if (!(currentFragment instanceof BusStopsMapFragment)) {
                        fragment = BusStopsMapFragment.newInstance();
                    }
                    break;
                case R.id.timetables:
                    if (!(currentFragment instanceof BusStopTimetablesFragment)) {
                        fragment = BusStopTimetablesFragment.newInstance();
                    }
                    break;
                case R.id.routes:
                    if (!(currentFragment instanceof BusRoutesFragment)) {
                        fragment = BusRoutesFragment.newInstance();
                    }
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            return true;
        });

        // Select the first tab on initial launch.
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.stops);
        }
    }
}
