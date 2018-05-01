package jessevivanco.com.pegcitytransit.ui.presenters;

import javax.inject.Inject;

import jessevivanco.com.pegcitytransit.BuildConfig;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.repositories.PreferencesRepository;

public class MainActivityPresenter {

    @Inject
    PreferencesRepository preferencesRepository;
    @Inject
    BusRoutesRepository busRoutesRepository;

    public MainActivityPresenter(AppComponent injector) {
        injector.injectInto(this);
    }

    public void checkIfAppUpdated() {

        int currVersionCode = BuildConfig.VERSION_CODE;
        int prevVersionCode = preferencesRepository.getLastUsedAppVersionCode();

        if (currVersionCode != prevVersionCode) {
            switch (currVersionCode) {

                // Fixed an issue where bus routes weren't loading in the correct order. Just wipe
                // those out once updated. Refreshing the list will apply the new fix.
                case 3:
                    busRoutesRepository.clearAllRoutesCache();
                    break;
            }
            preferencesRepository.setLastUsedAppVersionCode(currVersionCode);
        }
    }
}
