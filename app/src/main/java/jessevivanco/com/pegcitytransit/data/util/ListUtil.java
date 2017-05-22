package jessevivanco.com.pegcitytransit.data.util;

import android.support.v4.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

final public class ListUtil {

    private ListUtil() {
    }

    /**
     * Ripped from <a href="https://stackoverflow.com/questions/17008115/how-to-convert-a-sparsearray-to-arraylist">this</a>
     * Stack post.
     */
    public static <C> List<C> asList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

}
