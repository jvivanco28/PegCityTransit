package jessevivanco.com.pegcitytransit.data.rest.models.base;

import java.util.Date;

/**
 * All Winnipeg Transit responses have the same JSON structure with the exception of
 * <code>@SerializedName</code> on the response member <code>T</code>.
 *
 * @param <T>
 */
public class WinnipegTransitResponse<T> {

    public WinnipegTransitResponse(T element, Date queryTime) {
        this.element = element;
        this.queryTime = queryTime;
    }

    // @SerializedName for this member is different for most REST calls, so we'll use a TypeAdapter
    // to deserialize the majority of our REST responses.
    private T element;

    private Date queryTime;

    public T getElement() {
        return element;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    @Override
    public String toString() {
        return "WinnipegTransitResponse{" +
                "element=" + element +
                ", queryTime=" + queryTime +
                '}';
    }
}
