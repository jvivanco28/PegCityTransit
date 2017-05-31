package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;

public class SearchStopsView extends CardView implements TextView.OnEditorActionListener, TextWatcher {

    @BindView(R.id.search_field)
    EditText searchField;

    @BindView(R.id.clear_search)
    Button clearSearch;

    public SearchStopsView(Context context) {
        super(context);
        setup();
    }

    public SearchStopsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public SearchStopsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_search, this, true);
        ButterKnife.bind(this);

        this.setOnClickListener(v -> setKeyboardVisible(true));

        searchField.setOnEditorActionListener(this);
        searchField.addTextChangedListener(this);

        updateViewState(searchField.length());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            // TODO SEARCH
            Log.v("DEBUG", "SEARCH " + v.getText());

            setKeyboardVisible(false);
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateViewState(count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Do nothing.
    }

    private void updateViewState(int textLength) {
        if (textLength == 0 && clearSearch.getVisibility() != GONE) {
            clearSearch.setVisibility(GONE);
        } else if (textLength > 0 && clearSearch.getVisibility() != VISIBLE) {
            clearSearch.setVisibility(VISIBLE);
        }
    }

    private void setKeyboardVisible(boolean visible) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (visible) {
            searchField.requestFocus();
            imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);
        } else {
            searchField.clearFocus();
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        searchField.setText(null);
    }
}
