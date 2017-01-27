package jessevivanco.com.pegcitytransit.repositories;

import android.content.Context;

import java.io.IOException;

import jessevivanco.com.pegcitytransit.R;
import retrofit2.Response;

/**
 * Convenience methods for handling Retrofit responses. Checks if requests were successful and calls back
 * <code>onDataRetrieved(T)</code>. If successful, the <code>onListLoadError(String)</code> callback is invoked with the error
 * message that came with the response. if no error message exist, then uses a generic error message.
 */
public class RepositoryUtils {

    private RepositoryUtils() {
    }

    /**
     * Convenience method for retrieving responses <b>without</b> a <code>data</code> node.
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public static <T> void handleResponse(Context context, Response<T> response, final OnRepositoryDataRetrievedListener<T>
            callback) {
        if (response.isSuccessful()) {
            callback.onDataRetrieved(response.body());
        } else {
            callback.onError(handleErrorInformation(context, response));
        }
    }

    public static String handleErrorInformation(Context context, Response response) {

        String message = null;
        try {
            message = response.errorBody().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message != null ?
                message :
                context.getString(R.string.generic_error);
    }

    public static void handleErrorResponse(Context context, Throwable t, OnRepositoryDataRetrievedListener callback) {
        callback.onError(t.getLocalizedMessage() != null ?
                t.getLocalizedMessage() :
                context.getString(R.string.generic_error));
    }
}
