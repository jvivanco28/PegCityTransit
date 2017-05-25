package jessevivanco.com.pegcitytransit.ui.views.layout_manager;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import jessevivanco.com.pegcitytransit.R;

/**
 * A linear layout manager that animates cells the first time {@link LinearLayoutManager#onLayoutChildren(RecyclerView.Recycler, RecyclerView.State)}
 * is called. Subsequent calls will only invoke another animation if the flag is set to {@code true}
 * by calling {@link OneShotAnimatedLinearLayoutManager#setAnimateNextLayout(boolean)}.
 */
public class OneShotAnimatedLinearLayoutManager extends LinearLayoutManager {

    private final int INTER_OBJECT_DELAY_MILLIS;
    private final int DURATION_MILLIS;

    private RecyclerView recyclerView;
    private boolean animateNextLayout;

    public OneShotAnimatedLinearLayoutManager(Context context, RecyclerView recyclerView) {
        super(context);
        this.recyclerView = recyclerView;

        this.animateNextLayout = false;

        INTER_OBJECT_DELAY_MILLIS = context.getResources().getInteger(R.integer.recycler_view_animation_inter_object_delay_millis);
        DURATION_MILLIS = context.getResources().getInteger(R.integer.recycler_view_animation_duration_millis);
    }

    public void setAnimateNextLayout(boolean animateNextLayout) {
        this.animateNextLayout = animateNextLayout;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        // Only animate items coming in.
        if (animateNextLayout) {

            // Can't reuse the same spruce builder reference (nothing animates). So, we have to
            // re-instantiate on every animation.
            new Spruce.SpruceBuilder(recyclerView)
                    .sortWith(new DefaultSort(INTER_OBJECT_DELAY_MILLIS))
                    .animateWith(DefaultAnimations.fadeInAnimator(recyclerView, DURATION_MILLIS),
                            // Slide in from the left.
                            ObjectAnimator.ofFloat(recyclerView, "translationX", -recyclerView.getWidth(), 0f).setDuration(DURATION_MILLIS))
                    .start();

            animateNextLayout = false;
        }
    }
}
