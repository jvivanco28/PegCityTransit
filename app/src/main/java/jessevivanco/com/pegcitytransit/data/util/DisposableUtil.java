package jessevivanco.com.pegcitytransit.data.util;

import io.reactivex.disposables.Disposable;

final public class DisposableUtil {

    private DisposableUtil() {
    }

    public static void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
