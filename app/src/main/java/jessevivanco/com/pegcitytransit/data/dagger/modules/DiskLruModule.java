package jessevivanco.com.pegcitytransit.data.dagger.modules;

import android.content.Context;
import android.util.Log;

import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.DiskCache;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jessevivanco.com.pegcitytransit.BuildConfig;

@Module
public class DiskLruModule {

    private final String TAG = getClass().getSimpleName();

    /**
     * TODO check why we can't annotate this with @Nullable
     * NOTE: This <b>>might</b return {@code null} if {@link DiskCache} throws an IOException.
     */
    @Provides
    @Singleton
    @Inject
    CacheManager provideCacheManager(Context context) {
        String cachePath = context.getCacheDir().getPath();
        File cacheFile = new File(cachePath + File.separator + BuildConfig.APPLICATION_ID);

        DiskCache diskCache = null;
        try {
            // TODO test if this fails
            diskCache = new DiskCache(cacheFile, BuildConfig.VERSION_CODE, 1024 * 1024 * 10);
        } catch (IOException e) {
            // TODO REPORT THE ERROR TO CRASHLYTICS
            Log.e(TAG, "Error initializing Disk LRU Cache.", e);
        }
        return diskCache != null ? CacheManager.getInstance(diskCache) : null;
    }
}
