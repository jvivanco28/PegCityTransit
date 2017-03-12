package jessevivanco.com.pegcitytransit.ui.activities.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

abstract public class BaseActivity extends AppCompatActivity {

    private AppComponent injector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        injector = ((PegCityTransitApp) getApplication()).getInjector();
    }

    /**
     * Retrieve the layout for the main content view of this Activity.
     *
     * @return
     */
    abstract protected
    @LayoutRes
    int getContentView();

    /**
     * Wrapping activity context so we can utilize our own default font.
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Our field injector. If you have any members annotated with <code>@Inject</code>, then make
     * sure you call <code>AppComponent#injectInto(T)</code> where <code>T</code> is an instance
     * of yourself.
     *
     * @return
     */
    public AppComponent getInjector() {
        return injector;
    }
}
