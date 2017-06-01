package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.ui.presenters.SearchStopsPresenter;

public class SearchStopsView extends CardView implements TextView.OnEditorActionListener, TextWatcher {

    @BindView(R.id.search_field)
    EditText searchField;

    @BindView(R.id.search_loading_view)
    LottieAnimationView loadingView;

    @BindView(R.id.clear_search)
    Button clearSearch;

    private SearchStopsPresenter searchPresenter;

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

        searchField.addTextChangedListener(this);
        updateViewState(searchField.length());
    }

    public void initialize(AppComponent injector, SearchStopsPresenter.ViewContract viewContract) {
        searchPresenter = new SearchStopsPresenter(injector, viewContract);

        searchField.setOnEditorActionListener(this);
    }

    public void showLoadingIndicator(boolean visible) {
        if (visible) {
            loadingView.playAnimation();
        } else {
            loadingView.cancelAnimation();
        }
        loadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
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

    public void tearDown() {
        if (searchPresenter != null) {
            searchPresenter.tearDown();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            searchPresenter.searchBusStops(v.getText().toString());

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

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        searchField.setText(null);
    }
}
