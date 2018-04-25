package jessevivanco.com.pegcitytransit.ui.item_decorations;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jessevivanco.com.pegcitytransit.R;

import static jessevivanco.com.pegcitytransit.ui.adapters.ScheduledStopAdapter.POSITION_BUS_ROUTE_FILTER_LIST_CELL;

public class StopScheduleListItemDecoration extends RecyclerView.ItemDecoration {

    private final int MARGIN_TB;
    private final int MARGIN_LR;

    public StopScheduleListItemDecoration(Context context) {
        MARGIN_TB = context.getResources().getDimensionPixelSize(R.dimen.material_spacing_small);
        MARGIN_LR = context.getResources().getDimensionPixelSize(R.dimen.material_spacing_small);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        // Only add top margin to fist cell.
        if (position == 0) {
            outRect.top = MARGIN_TB;
        }
        outRect.bottom = MARGIN_TB;

        if (position != POSITION_BUS_ROUTE_FILTER_LIST_CELL) {
            outRect.left = MARGIN_LR;
            outRect.right = MARGIN_LR;
        }
    }
}
