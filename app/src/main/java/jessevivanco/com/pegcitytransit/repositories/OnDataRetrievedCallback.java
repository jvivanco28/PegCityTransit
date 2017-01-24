package jessevivanco.com.pegcitytransit.repositories;

import android.support.annotation.Nullable;

public interface OnDataRetrievedCallback<D> {

    /**
     * Returns the response data.
     *
     * @param data
     */
    void onDataRetrieved(@Nullable D data);

    void onError(String message);
}
