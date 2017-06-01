package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.util.Log;

import com.squareup.phrase.Phrase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.data.util.DisposableUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class SearchStopsPresenter {

    private static final String TAG = SearchStopsPresenter.class.getSimpleName();

    @Inject
    Context context;
    @Inject
    BusStopRepository busStopRepository;

    private ViewContract viewContract;
    private Disposable searchSubscription;

    public SearchStopsPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);

        this.viewContract = viewContract;
    }

    public void searchBusStops(String query) {

        DisposableUtil.dispose(searchSubscription);

        searchSubscription = busStopRepository.searchBusStops(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> viewContract.showSearchBarProgressIndicator(true))
                .doFinally(() -> viewContract.showSearchBarProgressIndicator(false))
                .subscribe(busStopViewModels -> {
                    if (busStopViewModels != null && busStopViewModels.size() > 0) {
                        viewContract.showSearchStopResults(busStopViewModels);
                    } else {
                        viewContract.showErrorMessage(Phrase.from(context.getString(R.string.no_search_results)).put("query", query).format().toString());
                    }
                }, throwable -> {
                    Log.e(TAG, "Error searching for bus stops", throwable);
                    viewContract.showErrorMessage(context.getString(R.string.error_searching_for_bus_stops));
                });
    }

    public void tearDown() {
        DisposableUtil.dispose(searchSubscription);
    }

    public interface ViewContract extends ErrorMessageViewContract {

        void showSearchBarProgressIndicator(boolean visible);

        void showSearchStopResults(List<BusStopViewModel> results);
    }
}
