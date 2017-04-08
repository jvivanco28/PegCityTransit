package jessevivanco.com.pegcitytransit.ui.fragments.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;

abstract public class BaseFragment extends Fragment {

    private AppComponent injector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        injector = ((PegCityTransitApp) getActivity().getApplication()).getInjector();

        return inflater.inflate(getLayoutResourceId(), container, false);
    }

    abstract protected
    @LayoutRes
    int getLayoutResourceId();

    public AppComponent getInjector() {
        return injector;
    }
}