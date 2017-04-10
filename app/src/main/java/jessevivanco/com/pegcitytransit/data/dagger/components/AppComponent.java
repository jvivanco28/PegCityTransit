package jessevivanco.com.pegcitytransit.data.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import jessevivanco.com.pegcitytransit.data.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.AppRouterModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RepositoriesModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RestModule;
import jessevivanco.com.pegcitytransit.ui.fragments.BusStopsMapFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;

/**
 * The bridge between our dependencies and our targets
 */
@Singleton
@Component(modules = {
        AppModule.class,
        AppRouterModule.class,
        RestModule.class,
        RepositoriesModule.class
})
public interface AppComponent {

    // Add injection targets here.

    void injectInto(BusStopsMapFragment target);

    void injectInto(BusStopsPresenter target);

    void injectInto(BusRoutesPresenter target);

    void injectInto(BusStopSchedulePresenter target);
}
