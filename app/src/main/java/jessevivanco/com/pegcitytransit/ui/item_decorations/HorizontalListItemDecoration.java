package jessevivanco.com.pegcitytransit.ui.item_decorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HorizontalListItemDecoration extends RecyclerView.ItemDecoration {

    private final int MARGIN_TB;
    private final int MARGIN_LR;

    public HorizontalListItemDecoration(int tbMargin, int marginBetweenCells) {
        MARGIN_TB = tbMargin;
        MARGIN_LR = marginBetweenCells;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        // Only add left margin for the first item
        if (position == 0) {
            outRect.left = MARGIN_LR;
        }
        outRect.top = MARGIN_TB;
        outRect.bottom = MARGIN_TB;
        outRect.right = MARGIN_LR;
    }
}
