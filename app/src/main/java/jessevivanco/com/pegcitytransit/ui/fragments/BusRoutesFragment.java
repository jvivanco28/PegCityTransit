package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.AppRouter;
import jessevivanco.com.pegcitytransit.ui.adapters.BusRoutesAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesListPresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

@Deprecated
public class BusRoutesFragment extends BaseFragment implements BusRoutesListPresenter.ViewContract, BusRouteCellViewHolder.OnBusRouteCellClickedListener {

    private static final String STATE_KEY_VIEW_STATE = "view_state";

    @Inject
    AppRouter appRouter;

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_routes_recycler_view)
    RecyclerView routesRecyclerView;

    @BindView(R.id.loading_view_container)
    ViewGroup loadingViewContainer;

    @BindView(R.id.loading_text)
    TextView loadingText;

    private BusRoutesAdapter routesAdapter;
    private BusRoutesListPresenter routesPresenter;

    public static BusRoutesFragment newInstance() {
        return new BusRoutesFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_routes;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInjector().injectInto(this);
        ButterKnife.bind(this, view);

        setupAdapter(savedInstanceState);
        setupRecyclerView();
        setupLoadingView();

        // TODO
//        if (savedInstanceState != null) {
//            showLoadingState(savedInstanceState.getBoolean(STATE_KEY_VIEW_STATE, false));
//        }

        if (routesAdapter.getBusRoutes() == null || routesAdapter.getBusRoutes().size() == 0) {

            routesRecyclerView.setVisibility(View.GONE);
            loadingViewContainer.setVisibility(View.VISIBLE);

            routesPresenter.loadAllBusRoutes();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO
//        outState.putBoolean(STATE_KEY_SHOW_LOADING_VIEW, loadingViewContainer.getVisibility() == View.VISIBLE);
        routesAdapter.onSaveInstanceState(outState);
    }

    protected void setupAdapter(Bundle savedInstanceState) {
        routesPresenter = new BusRoutesListPresenter(getInjector(), this);
        routesAdapter = new BusRoutesAdapter(routesPresenter, savedInstanceState, this);
    }

    private void setupRecyclerView() {
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        routesRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        routesRecyclerView.setAdapter(routesAdapter);
    }

    private void setupLoadingView() {
        loadingText.setText(getString(R.string.loading_message_finding_routes));
    }

    /**
     * We clicked on a bus route. Go to the bus route map screen.
     *
     * @param busRoute
     */
    @Override
    public void onBusRouteCellClicked(BusRouteViewModel busRoute) {
        appRouter.goToBusRouteMapScreen(getActivity(), busRoute);
    }

//    /**
//     * Bus Routes were loaded. Display them.
//     *
//     * @param routes
//     */
//    @Override
//    public void showBusRoutes(List<BusRouteViewModel> routes) {
//        routesRecyclerView.setVisibility(View.VISIBLE);
//        loadingViewContainer.setVisibility(View.GONE);
//
//        routesAdapter.setBusRoutes(routes);
//    }
//
//    @Override
//    public void onLoadBusRoutesError(String message) {
//        routesRecyclerView.setVisibility(View.VISIBLE);
//        loadingViewContainer.setVisibility(View.GONE);
//
//        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
//    }

    @Override
    public void showErrorMessage(String msg) {
        routesRecyclerView.setVisibility(View.VISIBLE);
        loadingViewContainer.setVisibility(View.GONE);

        Snackbar.make(rootContainer, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showAllBusRoutes(List<BusRouteViewModel> busRoutes) {

    }
}