package jessevivanco.com.pegcitytransit.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
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
import jessevivanco.com.pegcitytransit.ui.views.layout_manager.OneShotAnimatedLinearLayoutManager;

public class BusRoutesDialogFragment extends BottomSheetDialogFragment implements BusRoutesPresenter.ViewContract, BusRoutesAdapter.BusRoutesAdapterCallbacks {

    public static final String TAG = BusRoutesDialogFragment.class.getSimpleName();
    private static final String STATE_KEY_LOADING_INDICATOR_VISIBILITY = "bus_route_loading_indicator_visibility";

    @BindView(R.id.bus_routes_root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_routes_progress_bar)
    ProgressBar loadingIndicator;

    @BindView(R.id.bus_routes_recycler_view)
    RecyclerView routesRecyclerView;

    private OneShotAnimatedLinearLayoutManager layoutManager;
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
        layoutManager = new OneShotAnimatedLinearLayoutManager(getActivity(), routesRecyclerView);
        routesRecyclerView.setLayoutManager(layoutManager);
        routesRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        routesRecyclerView.setAdapter(routesAdapter);
    }

    @Override
    public void onBusRouteSelected(BusRouteViewModel busRoute) {
        listener.onBusRouteSelected(busRoute);

        dismissAllowingStateLoss();
    }

    @Override
    public void onRefreshButtonClicked() {
        routesPresenter.loadAllBusRoutes();
    }

    @Override
    public void showLoadingIndicator(boolean visible) {
        loadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorMessage(String msg) {
        routesAdapter.setNoResultsMessage(msg);
    }

    @Override
    public void showAllBusRoutes(List<BusRouteViewModel> busRoutes) {
        layoutManager.setAnimateNextLayout(busRoutes != null);
        routesAdapter.setList(busRoutes);
    }

    @OnClick(R.id.toolbar_close_button)
    public void closeModal() {
        dismissAllowingStateLoss();
    }
}
