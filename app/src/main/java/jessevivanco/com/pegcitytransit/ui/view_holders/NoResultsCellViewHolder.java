package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;

public class NoResultsCellViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.no_results_text)
    TextView noResultsText;

    @BindView(R.id.try_again_button)
    Button tryAgainButton;

    public NoResultsCellViewHolder(ViewGroup parent, @NonNull OnRefreshButtonClickedListener onRefreshButtonClickedListener) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false));
        ButterKnife.bind(this, itemView);

        tryAgainButton.setOnClickListener(v -> onRefreshButtonClickedListener.onRefreshButtonClicked());
    }

    public void setNoResultsText(String text) {
        noResultsText.setText(text);
    }

    @LayoutRes
    public static int getLayoutResId() {
        return R.layout.cell_no_results;
    }

    public interface OnRefreshButtonClickedListener {

        void onRefreshButtonClicked();
    }
}

