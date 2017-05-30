package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.repositories.PreferencesRepository;
import jessevivanco.com.pegcitytransit.data.util.DeviceUtil;
import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;
import jessevivanco.com.pegcitytransit.ui.util.IntentUtil;

public class SettingsDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = SettingsDialogFragment.class.getSimpleName();

    @Inject
    PreferencesRepository preferencesRepository;

    @BindView(R.id.use_24_hour_time_switch)
    Switch use24HourTimeSwitch;

    @BindView(R.id.search_radius_spinner)
    Spinner searchRadiusSpinner;
    ArrayAdapter<CharSequence> searchRadiusSpinnerAdapter;
    String[] searchRadiusOptions;

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
        setupSearchRadiusSpinner();
    }

    private void setup24TimeSwitch() {

        use24HourTimeSwitch.setChecked(preferencesRepository.isUsing24HourClock());

        use24HourTimeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesRepository.setUsing24HourClock(isChecked);
        });
    }

    private void setupSearchRadiusSpinner() {
        searchRadiusOptions = getResources().getStringArray(R.array.map_search_radius);

        searchRadiusSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, R.id.spinner_item, getResources().getStringArray(R.array.map_search_radius));
        searchRadiusSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        searchRadiusSpinner.setAdapter(searchRadiusSpinnerAdapter);

        setCurrentSearchRadiusAdapterSelection();

        searchRadiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferencesRepository.setMapSearchRadius(Integer.parseInt(searchRadiusOptions[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }

    private void setCurrentSearchRadiusAdapterSelection() {
        String searchRadiusPreference = String.valueOf(preferencesRepository.getMapSearchRadius());

        for (int i = 0; i < searchRadiusOptions.length; i++) {
            if (searchRadiusPreference.equals(searchRadiusOptions[i])) {
                searchRadiusSpinner.setSelection(i);
                break;
            }
        }
    }

    @OnClick(R.id.toolbar_close_button)
    public void closeModal() {
        dismissAllowingStateLoss();
    }

    @OnClick(R.id.report_issue_button)
    public void reportIssue() {
        getActivity().startActivity(IntentUtil.getSendEmailIntent(getString(R.string.feedback_email), getString(R.string.feedback_email_subject), DeviceUtil.getDebugInfo(getActivity())));
    }

    @OnClick(R.id.rate_app_button)
    public void rateApp() {
        getActivity().startActivity(IntentUtil.getAppIntent(getActivity()));
    }
}
