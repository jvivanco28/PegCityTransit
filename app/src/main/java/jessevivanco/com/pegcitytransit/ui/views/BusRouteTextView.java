package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.view_model.BusRouteViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class BusRouteTextView extends AppCompatTextView {

    public enum Size {
        MINI,
        NORMAL
    }

    public BusRouteTextView(Context context, Size size) {
        super(context);
        init(size);
    }

    public BusRouteTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null);
    }

    public BusRouteTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(null);
    }

    /**
     * Just applying style here. FYI this will override styles applied by an outside source (like in an xml layout).
     */
    private void init(Size size) {
        CalligraphyUtils.applyFontToTextView(getContext(), this, "fonts/bariol_bold.ttf");

        setGravity(Gravity.CENTER);

        int padding = size != null && size == Size.MINI ?
                getResources().getDimensionPixelSize(R.dimen.material_spacing_x_small) :
                getResources().getDimensionPixelSize(R.dimen.material_spacing_small);

        setPadding(padding, padding, padding, padding);

        float textSize = size != null && size == Size.MINI ?
                getResources().getDimension(R.dimen.text_bus_route_mini) :
                getResources().getDimension(R.dimen.text_bus_route_large);

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public void setBusRoute(BusRouteViewModel route) {

        setBackgroundResource(route != null ?
                route.getCoverage().getBackgroundDrawableResId() :
                R.drawable.regular_route);

        int colorRes = route != null ?
                route.getCoverage().getTextColorResId() :
                R.color.black;

        setTextColor(getResources().getColor(colorRes));

        setText(route != null && route.getNumber() != null ?
                String.valueOf(route.getNumber()) :
                null);
    }
}
