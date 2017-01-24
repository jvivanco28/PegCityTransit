package jessevivanco.com.pegcitytransit.repositories;

import android.content.Context;
import android.util.Log;

import java.util.List;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.rest.RestApi;
import jessevivanco.com.pegcitytransit.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.rest.models.list.RoutesList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static jessevivanco.com.pegcitytransit.repositories.RepositoryUtils.handleErrorInformation;

public class BusRoutesRepository {

    private static final String LOG_TAG = BusStopRepository.class.getClass().getSimpleName();

    private Context context;
    private RestApi restApi;

    public BusRoutesRepository(Context context, RestApi restApi) {
        this.context = context;
        this.restApi = restApi;
    }

    public void getBusStopsNearLocation(Integer busStop,
                                        final OnDataRetrievedCallback<List<BusRoute>> callback) {

        if (busStop != null) {
            restApi.getRoutesForStop(busStop).enqueue(new Callback<RoutesList>() {
                @Override
                public void onResponse(Call<RoutesList> call, Response<RoutesList> response) {
                    if (response.isSuccessful()) {
                        callback.onDataRetrieved(response.body().getRoutes());
                    } else {
                        callback.onError(handleErrorInformation(context, response));
                    }
                }

                @Override
                public void onFailure(Call<RoutesList> call, Throwable t) {
                    RepositoryUtils.handleErrorResponse(context, t, callback);
                }
            });
        } else {
            Log.e(LOG_TAG, "Bus stop # can't be null!");
            callback.onError(context.getString(R.string.generic_error));
        }
    }
}
