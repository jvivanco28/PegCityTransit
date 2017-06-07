package jessevivanco.com.pegcitytransit.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;
import jessevivanco.com.pegcitytransit.ui.adapters.BusRoutesAdapter;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteSelectedListener;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.ViewState;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.views.ErrorStateCell;
import jessevivanco.com.pegcitytransit.ui.views.layout_manager.OneShotAnimatedLinearLayoutManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BusRoutesDialogFragment extends BottomSheetDialogFragment implements BusRoutesPresenter.ViewContract {

    public static final String TAG = BusRoutesDialogFragment.class.getSimpleName();
    private static final String STATE_KEY_LOADING_INDICATOR_VISIBILITY = "bus_route_loading_indicator_visibility";
    private static final String STATE_KEY_VIEW_STATE = BusRoutesDialogFragment.class.getSimpleName() + "_view_state";

    @BindView(R.id.bus_routes_root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_routes_recycler_view)
    RecyclerView routesRecyclerView;

    @BindView(R.id.bus_routes_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.loading_view)
    FrameLayout loadingCell;

    @BindView(R.id.error_state_cell)
    ErrorStateCell errorStateCell;

    private OneShotAnimatedLinearLayoutManager layoutManager;
    private BusRoutesAdapter routesAdapter;
    private BusRoutesPresenter routesPresenter;
    private OnBusRouteSelectedListener onBusRouteSelectedListener;
    private ViewState viewState;

    public static BusRoutesDialogFragment newInstance() {
        return new BusRoutesDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View contentView = View.inflate(getContext(), R.layout.dialog_bus_routes, null);

        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);

        setupAdapter(savedInstanceState);
        setupRecyclerView();
        setupNoResultsView();
        setupViewState(savedInstanceState);

        if (savedInstanceState == null) {
            routesPresenter.loadAllBusRoutes();
        }
        return dialog;
    }

    /**
     * Using <a href="http://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment">this</a>
     * technique to callback to the hosting activity with the selected result.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnBusRouteSelectedListener) {
            onBusRouteSelectedListener = (OnBusRouteSelectedListener) context;
        } else {
            throw new IllegalStateException("Hosting activity must implement " + OnBusRouteSelectedListener.class.getSimpleName());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_KEY_LOADING_INDICATOR_VISIBILITY, progressBar.getVisibility());
        outState.putInt(STATE_KEY_VIEW_STATE, viewState.ordinal());

        routesAdapter.onSaveInstanceState(outState);
        errorStateCell.onSaveInstanceState(outState);
    }

    private void setupAdapter(Bundle savedInstanceState) {
        routesPresenter = new BusRoutesPresenter(((PegCityTransitApp) getActivity().getApplication()).getInjector(), this);
        routesAdapter = new BusRoutesAdapter(savedInstanceState, onBusRouteSelectedListener);
    }

    private void setupRecyclerView() {
        layoutManager = new OneShotAnimatedLinearLayoutManager(getActivity(), routesRecyclerView);
        routesRecyclerView.setLayoutManager(layoutManager);
        routesRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        routesRecyclerView.setAdapter(routesAdapter);
    }

    private void setupNoResultsView() {
        errorStateCell.setOnRefreshButtonClickedListener(() -> routesPresenter.loadAllBusRoutes());
    }

    private void setupViewState(@Nullable Bundle savedInstanceState) {
        viewState = savedInstanceState != null ?
                ViewState.values()[savedInstanceState.getInt(STATE_KEY_VIEW_STATE, ViewState.LIST.ordinal())] :
                ViewState.LIST;

        progressBar.setVisibility(savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_LOADING_INDICATOR_VISIBILITY, GONE) :
                GONE);

        errorStateCell.onRestoreInstanceState(savedInstanceState);

        viewState = savedInstanceState != null ?
                ViewState.values()[savedInstanceState.getInt(STATE_KEY_VIEW_STATE, ViewState.LIST.ordinal())] :
                ViewState.LIST;
        showViewState(viewState);
    }

    @Override
    public void showErrorMessage(String msg) {
        errorStateCell.setNoResultsText(msg);
    }

    @Override
    public void showViewState(ViewState viewState) {
        this.viewState = viewState;

        routesRecyclerView.setVisibility(viewState == ViewState.LIST ? VISIBLE : GONE);
        errorStateCell.setVisibility(viewState == ViewState.ERROR ? VISIBLE : GONE);
        progressBar.setVisibility(viewState == ViewState.LOADING ? VISIBLE : GONE);
        loadingCell.setVisibility(viewState == ViewState.LOADING ? VISIBLE : GONE);
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
