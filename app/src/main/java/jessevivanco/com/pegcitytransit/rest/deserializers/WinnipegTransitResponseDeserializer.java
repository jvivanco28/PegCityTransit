package jessevivanco.com.pegcitytransit.rest.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import jessevivanco.com.pegcitytransit.rest.models.base.WinnipegTransitResponse;

/**
 * Deserializer for all WinnipegTransit REST responses.
 * NOTE: All REST responses have the same format, the only difference being the
 * "elementSerializedName" node within the JSON response.
 *
 * @param <T>
 */
public class WinnipegTransitResponseDeserializer<T> implements JsonDeserializer<WinnipegTransitResponse<T>> {

    private String elementSerializedName;
    private Type dataType;
    private Gson gson;

    /**
     * @param elementSerializedName The <code>@SerializedName</code> for {@link WinnipegTransitResponse#element}
     *                              (aka the field name for the inner-root Json object).
     * @param dataType              This should be the generic type <code>T</code> that's defined
     *                              within <code>WinnipegTransitResponseDeserializer&lt;T&gt;</code>.
     *                              This is kind of shitty, but necessary workaround for
     *                              <a href="http://stackoverflow.com/questions/1082050/linking-to-an-external-url-in-javadoc">this issue</a>.
     * @param gson                  We need this to convert the JSON responses to POJOs.
     */
    public WinnipegTransitResponseDeserializer(String elementSerializedName, Type dataType, Gson gson) {
        this.elementSerializedName = elementSerializedName;
        this.dataType = dataType;
        this.gson = gson;
    }

    @Override
    public WinnipegTransitResponse<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        WinnipegTransitResponse<T> response = null;

        T element = null;
        Date queryTime = null;

        JsonObject root = json.getAsJsonObject();

        if (root.has(elementSerializedName)) {
            JsonElement mainJsonElement = root.get(elementSerializedName);

            // FYI typeOfT == WinnipegTransit<T>
            // But we just want an instance of T here
            // SPECIAL NOTE: If we have nested generics then things get ugly. Using TypeToken<T>
            // doesn't work properly. See http://stackoverflow.com/questions/27253555/com-google-gson-internal-linkedtreemap-cannot-be-cast-to-my-class
            // We have to use another type token fed in from the constructor. This isn't particularly
            // safe during runtime, so make sure you test out all of your REST responses!
            element = gson.fromJson(mainJsonElement, dataType);
        }

        // We'll always have a "query-time" json node.
        if (root.has("query-time")) {
            JsonElement queryTimeJsonElement = root.get("query-time");
            queryTime = gson.fromJson(queryTimeJsonElement, Date.class);
        }

        // Create the response object if either field is non-null.
        if (element != null || queryTime != null) {
            response = new WinnipegTransitResponse<>(element, queryTime);
        }
        return response;
    }
}
