package jessevivanco.com.pegcitytransit.data.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import jessevivanco.com.pegcitytransit.data.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.AppRouterModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.DiskLruModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RepositoriesModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RestModule;
import jessevivanco.com.pegcitytransit.ui.activities.BusRouteMapActivity;
import jessevivanco.com.pegcitytransit.ui.fragments.BusRoutesFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesListPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.TransmitMapPresenter;

/**
 * The bridge between our dependencies and our targets
 */
@Singleton
@Component(modules = {
        AppModule.class,
        AppRouterModule.class,
        RestModule.class,
        RepositoriesModule.class,
        DiskLruModule.class
})
public interface AppComponent {

    // Add injection targets here.

    void injectInto(BusRouteMapActivity target);

    void injectInto(TransitMapFragment target);

    void injectInto(BusRoutesFragment target);

    void injectInto(TransmitMapPresenter target);

    void injectInto(BusRoutesListPresenter target);

    void injectInto(BusStopSchedulePresenter target);

}
