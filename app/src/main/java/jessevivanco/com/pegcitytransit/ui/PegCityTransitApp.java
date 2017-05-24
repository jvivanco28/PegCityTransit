package jessevivanco.com.pegcitytransit.ui;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.tspoon.traceur.Traceur;

import io.fabric.sdk.android.Fabric;
import jessevivanco.com.pegcitytransit.BuildConfig;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.dagger.components.DaggerAppComponent;
import jessevivanco.com.pegcitytransit.data.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RestModule;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class PegCityTransitApp extends Application {

    /**
     * The bridge between our dependencies and our targets.
     */
    private AppComponent injector;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        // Helpful tool for debugging Rx-related exceptions.
        if (BuildConfig.DEBUG) {
            Traceur.enableLogging();
        }
        initDaggerModules();
        initIconify();
        initFonts();
    }

    private void initDaggerModules() {
        injector = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .restModule(new RestModule(getApplicationContext(),
                        getString(R.string.api_base_url),
                        getString(R.string.api_key), BuildConfig.DEBUG))
                .build();
    }

    /**
     * So we can use vector icons for free.
     */
    protected void initIconify() {
        // Icon font.
        Iconify.with(new MaterialModule());
    }

    /**
     * We'll use something other than Roboto as our default font.
     */
    private void initFonts() {
        // Default text font.
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.default_font_path))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    /**
     * Our field injector.
     *
     * @return
     */
    public AppComponent getInjector() {
        return injector;
    }

}
