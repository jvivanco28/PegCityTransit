package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteSelectedListener;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRouteCell extends CardView implements View.OnClickListener {

    private static final String STATE_KEY_BUS_ROUTE = "bus_route";

    @BindView(R.id.bus_route_name)
    TextView busRouteName;

    @BindView(R.id.bus_route_number)
    BusRouteTextView busRouteTextView;

    @BindView(R.id.loading_view)
    LottieAnimationView loadingView;

    private BusRouteViewModel busRoute;
    private OnBusRouteSelectedListener onBusRouteSelected;

    public BusRouteCell(Context context) {
        super(context);
        setup(null);
    }

    public BusRouteCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public BusRouteCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(@Nullable AttributeSet attrs) {

        LayoutInflater.from(getContext()).inflate(getLayoutResId(), this, true);
        ButterKnife.bind(this);

        // If no layout attributes supplied, then default width should be MATCH_PARENT
        if (attrs == null) {
            this.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
        }

        this.setClickable(true);
        this.setForeground(getResources().getDrawable(R.drawable.card_view_button));
        this.setOnClickListener(this);
    }

    public void bind(BusRouteViewModel busRoute) {
        this.busRoute = busRoute;

        if (busRoute != null) {
            busRouteName.setText(busRoute.getName());
            busRouteTextView.setBusRoute(busRoute);
            busRouteTextView.setOnClickListener(this);
        } else {
            busRouteName.setText(null);
            busRouteTextView.setBusRoute(null);
            busRouteTextView.setOnClickListener(null);
        }
    }

    public void setOnCellClickedListener(OnBusRouteSelectedListener onCellClickedListener) {
        this.onBusRouteSelected = onCellClickedListener;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_KEY_BUS_ROUTE, Parcels.wrap(busRoute));
    }

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            bind(Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_BUS_ROUTE)));
        }
    }

    public void showLoadingIndicator(boolean visible) {
        if (visible) {
            loadingView.playAnimation();
        } else {
            loadingView.cancelAnimation();
        }
        loadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        onBusRouteSelected.onBusRouteSelected(busRoute);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_bus_route;
    }

}