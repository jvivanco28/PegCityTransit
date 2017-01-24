package jessevivanco.com.pegcitytransit.repositories;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import jessevivanco.com.pegcitytransit.rest.RestApi;
import jessevivanco.com.pegcitytransit.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.rest.models.list.BusStopsList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static jessevivanco.com.pegcitytransit.repositories.RepositoryUtils.handleErrorInformation;

public class BusStopRepository {

    private static final String LOG_TAG = BusStopRepository.class.getClass().getSimpleName();

    private Context context;
    private RestApi restApi;

    public BusStopRepository(Context context, RestApi restApi) {
        this.context = context;
        this.restApi = restApi;
    }

    public void getBusStopsNearLocation(Double latitude,
                                        Double longitude,
                                        @Nullable Integer radius,
                                        final OnDataRetrievedCallback<List<BusStop>> callback) {

        restApi.getBusStopsNearLocation(latitude, longitude, radius)
                .enqueue(new Callback<BusStopsList>() {
                    @Override
                    public void onResponse(Call<BusStopsList> call, Response<BusStopsList> response) {

                        if (response.isSuccessful()) {
                            callback.onDataRetrieved(response.body().getBusStops());
                        } else {
                            callback.onError(handleErrorInformation(context, response));
                        }
                    }

                    @Override
                    public void onFailure(Call<BusStopsList> call, Throwable t) {
                        RepositoryUtils.handleErrorResponse(context, t, callback);
                    }
                });
    }
}
