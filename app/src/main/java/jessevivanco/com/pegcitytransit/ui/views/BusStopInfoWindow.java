package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;

public class BusStopInfoWindow extends LinearLayout {

    @BindView(R.id.bus_stop_key)
    TextView busStopKeyTextView;

    @BindView(R.id.bus_stop_name)
    TextView busStopNameTextView;

    // TODO use a widget for this.
    @BindView(R.id.bus_routes)
    FlowLayout busRoutesFlowLayout;

    public BusStopInfoWindow(Context context) {
        super(context);
        setup();
    }

    public BusStopInfoWindow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public BusStopInfoWindow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    protected void setup() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.info_window_bus_stop, this, true);
        ButterKnife.bind(this);

        int padding = getResources().getDimensionPixelSize(R.dimen.material_spacing_x_small);
        setPadding(padding, padding, padding, padding);
    }

    public void showBusStopInfo(@Nullable BusStop busStop) {
        if (busStop != null) {
            busStopKeyTextView.setText(String.valueOf(busStop.getKey()));
            busStopNameTextView.setText(busStop.getName());

            busRoutesFlowLayout.removeAllViews();

            if (busStop.getBusRoutes() != null) {
                for (BusRoute busRoute : busStop.getBusRoutes()) {
                    busRoutesFlowLayout.addView(generateTextView(busRoute));
                }
            }

        } else {
            busStopKeyTextView.setText(null);
            busStopNameTextView.setText(null);
            busRoutesFlowLayout.removeAllViews();
        }
    }

    private BusRouteTextView generateTextView(BusRoute busRoute) {
        BusRouteTextView busRouteTextView = new BusRouteTextView(getContext());
        busRouteTextView.setBusRouteText(busRoute);
        return busRouteTextView;
    }
}
