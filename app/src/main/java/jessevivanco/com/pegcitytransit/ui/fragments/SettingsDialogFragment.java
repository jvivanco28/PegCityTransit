package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.repositories.PreferencesRepository;
import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;

// TODO Dialog or normal fragment?
public class SettingsDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = SettingsDialogFragment.class.getSimpleName();

    @Inject
    PreferencesRepository preferencesRepository;

    @BindView(R.id.use_24_hour_time_switch)
    Switch use24HourTimeSwitch;

    public static SettingsDialogFragment newInstance() {
        return new SettingsDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((PegCityTransitApp) getActivity().getApplication()).getInjector().injectInto(this);

        setup24TimeSwitch();
    }

    private void setup24TimeSwitch() {

        use24HourTimeSwitch.setChecked(preferencesRepository.isUsing24HourClock());

        use24HourTimeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesRepository.setUsing24HourClock(isChecked);
        });
    }

    @OnClick(R.id.toolbar_close_button)
    public void closeModal() {
        dismissAllowingStateLoss();
    }
}
