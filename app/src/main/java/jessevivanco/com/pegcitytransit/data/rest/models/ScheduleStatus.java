package jessevivanco.com.pegcitytransit.data.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class ScheduleStatus {

    @SerializedName("status")
    @Expose
    Status status;

    @SerializedName("query-time")
    @Expose
    Date queryTime;

    public Status getStatus() {
        return status;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    @Override
    public String toString() {
        return "ScheduleStatus{" +
                "status=" + status +
                ", queryTime='" + queryTime + '\'' +
                '}';
    }

    /**
     * The first thing that you should always do is check the Service Status under which Winnipeg Transit is
     * operating. If Blue or Red Priority Service is in effect, then any results received through the Open Data Web
     * Service may not be reliable.
     *
     * @see <a href="https://api.winnipegtransit.com/home/api/v2/example">https://api.winnipegtransit
     * .com/home/api/v2/example</a>
     */
    public enum Code {

        /**
         * Schedule info should be accurate.
         */
        REGULAR("regular"),

        /**
         * Schedule info might not be reliable. You may want to check out
         * <a href="http://winnipegtransit.com/">http://winnipegtransit.com/en</a>.
         */
        BLUE("blue"),

        /**
         * Schedule info might not be reliable. You may want to check out
         * <a href="http://winnipegtransit.com/">http://winnipegtransit.com/en</a>.
         */
        RED("red");

        private String code;

        Code(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "Code{" +
                    "code='" + code + '\'' +
                    '}';
        }
    }
}
