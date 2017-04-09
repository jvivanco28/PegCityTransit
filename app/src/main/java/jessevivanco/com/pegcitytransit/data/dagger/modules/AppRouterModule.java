package jessevivanco.com.pegcitytransit.data.dagger.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jessevivanco.com.pegcitytransit.ui.AppRouter;

@Module
public class AppRouterModule {

    @Provides
    @Singleton
    AppRouter provideAppRouter() {
        return new AppRouter();
    }
}
