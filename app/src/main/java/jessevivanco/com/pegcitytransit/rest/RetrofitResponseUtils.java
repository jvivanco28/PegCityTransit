package jessevivanco.com.pegcitytransit.rest;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.repositories.OnRepositoryDataRetrievedListener;
import retrofit2.Response;

/**
 * Convenience methods for handling Retrofit responses. Checks if requests were successful and calls back
 * <code>onDataRetrieved(T)</code>. If successful, the <code>onListLoadError(String)</code> callback is invoked with the error
 * message that came with the response. if no error message exist, then uses a generic error message.
 */
public class RetrofitResponseUtils {

    private RetrofitResponseUtils() {
    }

    /**
     * Convenience method for handling retrofit responses using Rx.
     *
     * @param context
     * @param responseData
     * @param logTag
     * @param throwable
     * @param callback
     * @param <T>
     */
    public static <T> void handleResponse(Context context,
                                          T responseData,
                                          String logTag,
                                          Throwable throwable,
                                          final OnRepositoryDataRetrievedListener<T> callback) {
        if (responseData != null) {
            callback.onDataRetrieved(responseData);
        } else {
            handleErrorResponse(context, logTag, throwable, callback);
        }
    }

    /**
     * Convenience method for handing retrofit responses <b>without</b> a <code>data</code> node.
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

    public static void handleErrorResponse(Context context, String logTag, Throwable t, OnRepositoryDataRetrievedListener callback) {

        Log.e(logTag, "Error", t);

        callback.onError(t.getLocalizedMessage() != null ?
                t.getLocalizedMessage() :
                context.getString(R.string.generic_error));
    }
}
