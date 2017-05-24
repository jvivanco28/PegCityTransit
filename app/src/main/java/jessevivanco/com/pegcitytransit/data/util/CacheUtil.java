package jessevivanco.com.pegcitytransit.data.util;


import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.iainconnor.objectcache.CacheManager;

import java.lang.reflect.Type;

public class CacheUtil {

    private static final String TAG = CacheUtil.class.getSimpleName();

    private CacheUtil() {
    }

    /**
     * Convenience method for retrieving cached data.
     *
     * @return The cached data, or {@code null} if no data exists under that key or the data is expired.
     */
    @SuppressWarnings("unchecked")
    public static @Nullable
    <T> T getFromCache(@Nullable CacheManager cacheManager, String cacheKey, Type typeToken) {

        T cachedResult = null;

        if (cacheManager != null) {

            Object unformattedCachedResult = cacheManager.get(cacheKey, null, typeToken);
            try {
                cachedResult = (T) unformattedCachedResult;
            } catch (ClassCastException e) {

                // This should never happen, but if it does we need to be able to recover from this.
                Crashlytics.logException(e);
                Log.v(TAG, "Error getting data frmo cache.", e);
            }
        }
        return cachedResult;
    }
}
