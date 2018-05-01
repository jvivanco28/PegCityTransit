package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BasicViewHolder<V extends View> extends RecyclerView.ViewHolder {

    private V view;

    public BasicViewHolder(V view) {
        super(view);

    }

    public V getView() {
        return view;
    }
}
