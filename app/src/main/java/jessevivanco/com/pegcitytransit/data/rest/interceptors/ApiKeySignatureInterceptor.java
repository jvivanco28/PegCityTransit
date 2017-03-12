package jessevivanco.com.pegcitytransit.data.rest.interceptors;

import android.content.Context;

import java.io.IOException;

import jessevivanco.com.pegcitytransit.R;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Signs each outgoing request with the API key as an additional query param (let's us avoid having to explicitly add
 * it to each method in <code>WinnipegTransitApi</code>.
 */
public class ApiKeySignatureInterceptor implements Interceptor {

    private final String apiKeyName;
    private final String apiKeyValue;

    public ApiKeySignatureInterceptor(Context context, String apiKeyValue) {
        this.apiKeyName = context.getString(R.string.api_key_query_name);
        this.apiKeyValue = apiKeyValue;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        // The original request.
        Request original = chain.request();

        // Get the the url from the original request and add the API key as a query param.
        HttpUrl signedRequestUrl = original.url().newBuilder()
                .addQueryParameter(apiKeyName, apiKeyValue)
                .build();

        // Generate the new request with the signed url from above.
        Request.Builder requestBuilder = original.newBuilder().url(signedRequestUrl);

        return chain.proceed(requestBuilder.build());
    }
}
