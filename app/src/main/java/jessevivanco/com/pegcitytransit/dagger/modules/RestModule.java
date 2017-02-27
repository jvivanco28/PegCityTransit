package jessevivanco.com.pegcitytransit.dagger.modules;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.rest.RestApi;
import jessevivanco.com.pegcitytransit.rest.deserializers.WinnipegTransitResponseDeserializer;
import jessevivanco.com.pegcitytransit.rest.interceptors.ApiKeySignatureInterceptor;
import jessevivanco.com.pegcitytransit.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.rest.models.base.WinnipegTransitResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RestModule {

    private Context context;
    private String baseUrl;
    private String apiKey;
    private boolean debug;

    public RestModule(Context context,
                      String baseUrl,
                      String apiKey,
                      boolean debug) {

        this.context = context;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.debug = debug;
    }

    @Provides
    @Singleton
    Gson provideGson() {

        // NOTE: This isn't the actually GSON instance that we return from this method. This
        // instance is for the TypeAdapters only.
        Gson gsonForTypeAdapters = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setDateFormat(context.getString(R.string.date_format))
                .create();

        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setDateFormat(context.getString(R.string.date_format))

                .registerTypeAdapter(new TypeToken<WinnipegTransitResponse<List<BusStop>>>() {}.getType(),
                        new WinnipegTransitResponseDeserializer<List<BusStop>>("stops", new TypeToken<List<BusStop>>() {}.getType(), gsonForTypeAdapters)) // TODO string const
                .create();
    }

    @Provides
    @Singleton
    @Inject
    ApiKeySignatureInterceptor provideApiKeySignatureInterceptor(Context context) {
        return new ApiKeySignatureInterceptor(context, apiKey);
    }

    @Provides
    @Singleton
    OkHttpClient generateOkHttpClient(ApiKeySignatureInterceptor apiKeySignatureInterceptor) {

        // Attach a logger so we can see which requests are going out in LOGCAT.
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(debug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(apiKeySignatureInterceptor)
                .build();
    }

    @Provides
    @Singleton
    Retrofit generateRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    RestApi generateRestApi(Retrofit retrofit) {
        return retrofit.create(RestApi.class);
    }
}
