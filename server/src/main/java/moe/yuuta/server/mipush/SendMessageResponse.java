package moe.yuuta.server.mipush;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMessageResponse {
    public static final String RESULT_OK = "ok";
    public static final String RESULT_ERROR = "error";

    public static final int CODE_SUCCESS = 0;

    @JsonProperty("result")
    private String result;

    @JsonProperty("description")
    private String description;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("code")
    private int code;

    @JsonProperty("info")
    private String info;

    public String getResult() {
        return result;
    }

    public String getDescription() {
        return description;
    }

    public Data getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public static class Data {
        private String id;

        public String getId() {
            return id;
        }
    }
}
