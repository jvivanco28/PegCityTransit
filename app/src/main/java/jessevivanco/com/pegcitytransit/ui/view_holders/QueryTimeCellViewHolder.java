package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;

public class QueryTimeCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.query_time)
    TextView queryTimeTextView;

    public QueryTimeCellViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);
    }

    public void bind(String queryTime) {
        if (queryTime != null) {
            queryTimeTextView.setText(queryTime);
        } else {
            queryTimeTextView.setText(null);
        }
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_query_time;
    }

}