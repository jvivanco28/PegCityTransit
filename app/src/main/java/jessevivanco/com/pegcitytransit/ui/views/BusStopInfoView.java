package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopInfoView extends LinearLayout {

    @BindView(R.id.bus_stop_key)
    TextView busStopKeyTextView;

    @BindView(R.id.fav_icon)
    View favIcon;

    @BindView(R.id.bus_stop_name)
    TextView busStopNameTextView;

    @BindView(R.id.bus_routes)
    FlowLayout busRoutesFlowLayout;

    public BusStopInfoView(Context context) {
        super(context);
        setup();
    }

    public BusStopInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public BusStopInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.info_window_bus_stop, this, true);
        ButterKnife.bind(this);

        busStopKeyTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_small));
        busStopNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_small));

        int padding = getResources().getDimensionPixelSize(R.dimen.material_spacing_x_small);
        setPadding(padding, padding, padding, padding);
    }

    public void showBusStopInfo(@Nullable BusStopViewModel busStop) {
        if (busStop != null) {
            busStopKeyTextView.setText(String.valueOf(busStop.getKey()));
            favIcon.setVisibility(busStop.isSavedStop() ? VISIBLE : GONE);
            busStopNameTextView.setText(busStop.getName());

            busRoutesFlowLayout.removeAllViews();

            if (busStop.getRoutes() != null) {
                for (BusRouteViewModel route : busStop.getRoutes()) {
                    busRoutesFlowLayout.addView(generateTextView(route));
                }
            }
        } else {
            busStopKeyTextView.setText(null);
            favIcon.setVisibility(View.GONE);
            busStopNameTextView.setText(null);
            busRoutesFlowLayout.removeAllViews();
        }
    }

    private BusRouteTextView generateTextView(BusRouteViewModel route) {
        BusRouteTextView busRouteTextView = new BusRouteTextView(getContext(), BusRouteTextView.Size.MINI);
        busRouteTextView.setBusRoute(route);
        return busRouteTextView;
    }
}