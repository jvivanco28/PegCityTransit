package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;

public class LoadingIndicatorCell extends RecyclerView.ViewHolder {

    public LoadingIndicatorCell(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_loading_indicator;
    }
}
