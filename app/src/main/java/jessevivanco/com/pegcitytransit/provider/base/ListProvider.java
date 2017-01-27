package jessevivanco.com.pegcitytransit.provider.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import jessevivanco.com.pegcitytransit.repositories.OnRepositoryDataRetrievedListener;

/**
 * A generic interface for our adapters. Each adapter in this app sends a request for a list of items. This layer
 * forwards the request to the appropriate <Code>Repository</Code> layer, and is also in charge of saving/restoring
 * the state of the list of items. This is the layer between the <code>Adapter</code> layer and the
 * <code>Repository</code> layer.
 *
 * @param <D>
 */
public interface ListProvider<D> {

    /**
     * Send a request to fetch a list data of type &lt;D&gt;.
     *
     * @param onDataRetrievedCallback
     */
    void loadData(OnRepositoryDataRetrievedListener<D> onDataRetrievedCallback);

    /**
     * Signal to save our adapter's state (the main list, or any other field members that need to be retained).
     *
     * @param outState
     * @return
     */
    Bundle onSaveInstanceState(Bundle outState);

    /**
     * Signal to restore the state of our adater.
     *
     * @param state
     */
    void onRestoreInstanceState(@Nullable Bundle state);

    /**
     * Callbacks to any View who is hosting a <code>ListProvider</code>. If you're using a
     * <code>ListProvider</code>, then you must implement these methods.
     */
    interface ListProviderViewContract {

        /**
         * Signal that the list finished loading.
         */
        void onFinishedLoading();

        /**
         * Signal that the list had finished loading with an error.
         *
         * @param message
         */
        void onListLoadError(String message);
    }
}
