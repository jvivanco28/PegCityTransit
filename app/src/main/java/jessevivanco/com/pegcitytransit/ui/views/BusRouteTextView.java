package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class BusRouteTextView extends AppCompatTextView {

    public BusRouteTextView(Context context) {
        super(context);
        init();
    }

    public BusRouteTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BusRouteTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Just applying style here.
     */
    private void init() {
        CalligraphyUtils.applyFontToTextView(getContext(), this, "fonts/bariol_bold.ttf");

        int padding = getResources().getDimensionPixelSize(R.dimen.material_spacing_x_small);
        setPadding(padding, padding, padding, padding);
    }

    public void setBusRouteText(BusRoute busRoute) {

        setBackgroundResource(busRoute != null ?
                busRoute.getCoverage().getBackgroundDrawableResId() :
                R.drawable.regular_route);

        int colorRes = busRoute != null ?
                busRoute.getCoverage().getTextColorResId() :
                R.color.black;

        setTextColor(getResources().getColor(colorRes));

        setText(busRoute != null && busRoute.getNumber() != null ?
                String.valueOf(busRoute.getNumber()) :
                null);
    }
}
