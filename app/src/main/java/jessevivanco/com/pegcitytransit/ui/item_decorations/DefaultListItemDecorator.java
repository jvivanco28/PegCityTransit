package jessevivanco.com.pegcitytransit.ui.item_decorations;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jessevivanco.com.pegcitytransit.R;

public class DefaultListItemDecorator extends RecyclerView.ItemDecoration {

    private final int MARGIN_TB;
    private final int MARGIN_LR;

    public DefaultListItemDecorator(Context context) {
        MARGIN_TB = context.getResources().getDimensionPixelSize(R.dimen
                .material_spacing_x_small);

        MARGIN_LR = context.getResources().getDimensionPixelSize(R.dimen
                .material_spacing_small);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.top = MARGIN_TB;
        outRect.bottom = MARGIN_TB;

        outRect.left = MARGIN_LR;
        outRect.right = MARGIN_LR;
    }
}
