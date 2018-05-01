package jessevivanco.com.pegcitytransit.ui.item_decorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HorizontalListItemDecoration extends RecyclerView.ItemDecoration {

    private final int MARGIN_LR;
    private final int MARGIN_BETWEEN;

    public HorizontalListItemDecoration(int lrMargin, int marginBetweenCells) {
        MARGIN_LR = lrMargin;
        MARGIN_BETWEEN = marginBetweenCells;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        // Only add left margin for the first item
        if (position == 0) {
            outRect.left = MARGIN_LR;
        } else if ( position == parent.getAdapter().getItemCount() - 1) {
            outRect.right = MARGIN_LR;
        } else {
            outRect.left = MARGIN_BETWEEN;
        }
    }
}
