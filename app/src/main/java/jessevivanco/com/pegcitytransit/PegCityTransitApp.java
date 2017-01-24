package jessevivanco.com.pegcitytransit;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;

import io.realm.Realm;
import jessevivanco.com.pegcitytransit.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.dagger.components.DaggerAppComponent;
import jessevivanco.com.pegcitytransit.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.dagger.modules.RestModule;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class PegCityTransitApp extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO enable crash reporting (Fabric or Firebase?)
//        if (BuildConfig.REPORT_CRASHES) {
//            Fabric.with(this, new Crashlytics());
//        }

        Realm.init(this);

        initDependencies();

        initIconify();
        initFonts();
    }

    private void initDependencies() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .restModule(new RestModule(getApplicationContext(),
                        getString(R.string.api_base_url),
                        getString(R.string.api_key), BuildConfig
                        .DEBUG))
                .build();
    }

    /**
     * So we can display decent-looking icons for free.
     */
    protected void initIconify() {
        // Icon font.
        Iconify.with(new MaterialCommunityModule());
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
    public static AppComponent getAppComponent() {
        return appComponent;
    }

}
