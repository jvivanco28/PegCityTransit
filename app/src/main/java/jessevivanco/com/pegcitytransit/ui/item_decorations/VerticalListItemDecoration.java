package jessevivanco.com.pegcitytransit.ui.item_decorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalListItemDecoration extends RecyclerView.ItemDecoration {

    private final int MARGIN_TB;
    private final int MARGIN_LR;
    private final int SKIP_NUM_HEADER_CELLS;

    public VerticalListItemDecoration(int lrMargin, int marginBetweenCells) {
        MARGIN_TB = lrMargin;
        MARGIN_LR = marginBetweenCells;
        SKIP_NUM_HEADER_CELLS = 0;
    }

    public VerticalListItemDecoration(int lrMargin, int marginBetweenCells, int skipNumHeaderCells) {
        MARGIN_TB = lrMargin;
        MARGIN_LR = marginBetweenCells;
        SKIP_NUM_HEADER_CELLS = skipNumHeaderCells;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        // Only add top margin to fist cell.
        if (position > SKIP_NUM_HEADER_CELLS - 1) {

            if (position == 0 || position == SKIP_NUM_HEADER_CELLS) {
                outRect.top = MARGIN_TB;
            }
            outRect.bottom = MARGIN_TB;
            outRect.left = MARGIN_LR;
            outRect.right = MARGIN_LR;
        }
    }
}
