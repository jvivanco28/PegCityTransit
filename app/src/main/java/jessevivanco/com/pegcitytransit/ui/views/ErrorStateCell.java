package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;

public class ErrorStateCell extends RelativeLayout {

    private static final String STATE_KEY_MESSAGE = ErrorStateCell.class.getSimpleName() + "_message";

    @BindView(R.id.no_results_text)
    TextView noResultsText;

    @BindView(R.id.try_again_button)
    Button tryAgainButton;

    private OnRefreshButtonClickedListener onRefreshButtonClickedListener;

    public ErrorStateCell(Context context) {
        super(context);
        setup();
    }

    public ErrorStateCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ErrorStateCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        LayoutInflater.from(getContext()).inflate(R.layout.cell_no_results, this, true);
        ButterKnife.bind(this);

        int padding = getResources().getDimensionPixelSize(R.dimen.material_spacing_medium);
        setPadding(padding, padding, padding, padding);
    }

    public void setNoResultsText(String text) {
        noResultsText.setText(text);
    }

    public void setOnRefreshButtonClickedListener(OnRefreshButtonClickedListener onRefreshButtonClickedListener) {
        this.onRefreshButtonClickedListener = onRefreshButtonClickedListener;
    }

    @OnClick(R.id.try_again_button)
    protected void onRefreshButtonClicked() {
        tryAgainButton.setOnClickListener(v -> onRefreshButtonClickedListener.onRefreshButtonClicked());
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_KEY_MESSAGE, noResultsText.getText().toString());
    }

    public void onRestoreInstanceState(@Nullable Bundle outState) {
        if (outState != null) {
            noResultsText.setText(outState.getString(STATE_KEY_MESSAGE));
        }
    }

    public interface OnRefreshButtonClickedListener {

        void onRefreshButtonClicked();
    }
}

