package jessevivanco.com.pegcitytransit.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;
import jessevivanco.com.pegcitytransit.ui.adapters.BusRoutesAdapter;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteCell;

public class BusRoutesDialogFragment extends BottomSheetDialogFragment implements BusRoutesPresenter.ViewContract, BusRouteCell.OnBusRouteSelectedListener {

    public static final String TAG = BusRoutesDialogFragment.class.getSimpleName();
    private static final String STATE_KEY_LOADING_INDICATOR_VISIBILITY = "bus_route_loading_indicator_visibility";

    @BindView(R.id.bus_routes_root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_routes_progress_bar)
    ProgressBar loadingIndicator;

    @BindView(R.id.bus_routes_recycler_view)
    RecyclerView routesRecyclerView;

    private BusRoutesAdapter routesAdapter;
    private BusRoutesPresenter routesPresenter;

    private BusRouteCell.OnBusRouteSelectedListener listener;

    public static BusRoutesDialogFragment newInstance() {
        return new BusRoutesDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bus_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        restoreState(savedInstanceState);
        setupAdapter(savedInstanceState);
        setupRecyclerView();

        if (savedInstanceState == null) {
            routesPresenter.loadAllBusRoutes();
        }
    }

    /**
     * Using <a href="http://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment">this</a>
     * technique to callback to the hosting activity with the selected result.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BusRouteCell.OnBusRouteSelectedListener) {
            listener = (BusRouteCell.OnBusRouteSelectedListener) context;
        } else {
            throw new IllegalStateException("Hosting activity must implement " + BusRouteCell.OnBusRouteSelectedListener.class.getSimpleName());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_KEY_LOADING_INDICATOR_VISIBILITY, loadingIndicator.getVisibility());
        routesAdapter.onSaveInstanceState(outState);
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            loadingIndicator.setVisibility(savedInstanceState.getInt(STATE_KEY_LOADING_INDICATOR_VISIBILITY, View.GONE));
        }
    }

    private void setupAdapter(Bundle savedInstanceState) {
        routesPresenter = new BusRoutesPresenter(((PegCityTransitApp) getActivity().getApplication()).getInjector(), this);
        routesAdapter = new BusRoutesAdapter(savedInstanceState, this);
    }

    private void setupRecyclerView() {
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        routesRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        routesRecyclerView.setAdapter(routesAdapter);
    }

    @Override
    public void onBusRouteSelected(BusRouteViewModel busRoute) {
        listener.onBusRouteSelected(busRoute);
    }

    @Override
    public void showLoadingIndicator(boolean visible) {
        loadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorMessage(String msg) {
        // TODO error state in adapter
        Snackbar.make(rootContainer, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showAllBusRoutes(List<BusRouteViewModel> busRoutes) {
        routesAdapter.setBusRoutes(busRoutes);
    }

    @OnClick(R.id.toolbar_close_button)
    public void closeModal() {
        dismissAllowingStateLoss();
    }
}
