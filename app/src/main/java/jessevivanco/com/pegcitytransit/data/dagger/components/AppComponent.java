package jessevivanco.com.pegcitytransit.data.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import jessevivanco.com.pegcitytransit.data.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.DiskLruModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RepositoriesModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RestModule;
import jessevivanco.com.pegcitytransit.ui.fragments.SettingsDialogFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.MainActivityPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.SearchStopsPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.TransmitMapPresenter;

/**
 * The bridge between our dependencies and our targets
 */
@Singleton
@Component(modules = {
        AppModule.class,
        RestModule.class,
        RepositoriesModule.class,
        DiskLruModule.class
})
public interface AppComponent {

    // Add injection targets here.

    void injectInto(TransitMapFragment target);

    void injectInto(TransmitMapPresenter target);

    void injectInto(BusRoutesPresenter target);

    void injectInto(BusStopSchedulePresenter target);

    void injectInto(SettingsDialogFragment target);

    void injectInto(SearchStopsPresenter target);

    void injectInto(MainActivityPresenter target);
}
