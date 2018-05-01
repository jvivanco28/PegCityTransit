package jessevivanco.com.pegcitytransit.ui.views.layout_manager;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.SortFunction;
import com.willowtreeapps.spruce.sort.SpruceTimedView;

import java.util.ArrayList;
import java.util.List;

import jessevivanco.com.pegcitytransit.R;

/**
 * A linear layout manager that animates cells the first time {@link LinearLayoutManager#onLayoutChildren(RecyclerView.Recycler, RecyclerView.State)}
 * is called. Subsequent calls will only invoke another animation if the flag is set to {@code true}
 * by calling {@link OneShotAnimatedLinearLayoutManager#setAnimateNextLayout(boolean)}.
 */
public class OneShotAnimatedLinearLayoutManager extends LinearLayoutManager {

    private final int DURATION_MILLIS;

    private RecyclerView recyclerView;
    private boolean animateNextLayout;
    private int omitTopXCells;

    public OneShotAnimatedLinearLayoutManager(Context context, RecyclerView recyclerView) {
        super(context);
        this.recyclerView = recyclerView;
        this.animateNextLayout = false;
        DURATION_MILLIS = context.getResources().getInteger(R.integer.recycler_view_animation_duration_millis);
    }

    public void setAnimateNextLayout(boolean animateNextLayout) {
        this.animateNextLayout = animateNextLayout;
    }

    public void omitTopXCells(int omitTopXCells) {
        this.omitTopXCells = omitTopXCells;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        // Only animate items coming in.
        if (animateNextLayout) {

            // Can't reuse the same spruce builder reference (nothing animates). So, we have to
            // re-instantiate on every animation.
            new Spruce.SpruceBuilder(recyclerView)
                    .sortWith(new FlatAnimationSort(omitTopXCells < 0 ? 0 : omitTopXCells))
                    .animateWith(DefaultAnimations.fadeInAnimator(recyclerView, DURATION_MILLIS),
                            // Slide in from the left.
                            ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView.getWidth(), 0f).setDuration(DURATION_MILLIS))
                    .start();
            animateNextLayout = false;
            omitTopXCells = 0;
        }
    }

    /**
     * Animate all cells in from the left. We have the ability to omit the first X cells; those will
     * remain in place.
     */
    public static class FlatAnimationSort extends SortFunction {

        private final int omitFirstXCells;

        public FlatAnimationSort(int omitFirstXCells) {
            this.omitFirstXCells = omitFirstXCells;
        }

        @Override
        public List<SpruceTimedView> getViewListWithTimeOffsets(ViewGroup parent, List<View> children) {
            List<SpruceTimedView> childTimedViews = new ArrayList<>();

            for (int i = omitFirstXCells; i < children.size(); i++) {
                View childView = children.get(i);
                childTimedViews.add(new SpruceTimedView(childView, 0));
            }

            return childTimedViews;
        }
    }
}
