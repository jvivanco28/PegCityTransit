package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;

// TODO Dialog or normal fragment?
public class SettingsDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = SettingsDialogFragment.class.getSimpleName();

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

    }
}
