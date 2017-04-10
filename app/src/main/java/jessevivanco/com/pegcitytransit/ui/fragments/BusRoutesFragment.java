package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.adapters.BusRoutesAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_model.BusRouteViewModel;

public class BusRoutesFragment extends BaseFragment implements BusRoutesPresenter.ViewContract, BusRouteCellViewHolder.OnBusRouteCellClickedListener {

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_routes_recycler_view)
    RecyclerView routesRecyclerView;

    private BusRoutesAdapter routesAdapter;// TODO marked for deletion
    private BusRoutesPresenter routesPresenter;

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
        ButterKnife.bind(this, view);

        setupAdapter();
        setupRecyclerView();

        if (routesAdapter.getBusRoutes() == null || routesAdapter.getBusRoutes().size() == 0) {
            routesPresenter.loadBusRoutes();
        }
    }

    protected void setupAdapter() {
        routesPresenter = new BusRoutesPresenter(getInjector(), this);
        routesAdapter = new BusRoutesAdapter(routesPresenter, this);
    }

    private void setupRecyclerView() {
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        routesRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        routesRecyclerView.setAdapter(routesAdapter);
    }

    @Override
    public void onBusRouteCellClicked(BusRouteViewModel busRoute) {
        // todo
    }

    /**
     * Bus Routes were loaded. Display them.
     *
     * @param routes
     */
    @Override
    public void showBusRoutes(List<BusRouteViewModel> routes) {
        routesAdapter.setBusRoutes(routes);
    }

    @Override
    public void onLoadBusRoutesError(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
    }
}
