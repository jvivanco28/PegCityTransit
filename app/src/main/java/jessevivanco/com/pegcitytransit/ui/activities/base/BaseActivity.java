package jessevivanco.com.pegcitytransit.ui.activities.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

abstract public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
    }

    /**
     * Retrieve the layout for the main content view of this Activity.
     *
     * @return
     */
    abstract protected
    @LayoutRes
    int getContentView();

}
