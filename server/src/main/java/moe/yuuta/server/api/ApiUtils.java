package moe.yuuta.server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ApiUtils {
    public static String objectToJson (Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public static String tryObjectToJson (Object object) {
        try {
            return objectToJson(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <V> V jsonToObject (String json, Class<V> t) throws IOException {
        return new ObjectMapper().readValue(json, t);
    }

    public static <V> V jsonToObject (String json, TypeReference<V> t) throws IOException {
        return new ObjectMapper().readValue(json, t);
    }
}
