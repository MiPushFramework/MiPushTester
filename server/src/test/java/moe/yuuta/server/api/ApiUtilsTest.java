package moe.yuuta.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApiUtilsTest {

    @Test
    public void separateListToComma() {
        assertEquals("Rikka,haoye", ApiUtils.separateListToComma(Arrays.asList("Rikka", "haoye")));
    }

    public static class SampleObject {
        private static final String TARGET_JSON =
                "{" +
                        "\"string\":\"Rikka\"," +
                        "\"integer\":2333," +
                        "\"map\":{" +
                        "\"Rikka\":2333" +
                        "}," +
                        "\"list\":[" +
                        "\"Rikka\"," +
                        "\"haoye\"" +
                        "]" +
                        "}";

        @JsonProperty("string")
        private String string = "Rikka";
        @JsonProperty("integer")
        private int integer = 2333;
        @JsonProperty("map")
        private Map<String, Integer> map = Collections.singletonMap("Rikka", 2333);
        @JsonProperty("list")
        private List<String> list = Arrays.asList("Rikka", "haoye");

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SampleObject that = (SampleObject) o;
            return integer == that.integer &&
                    Objects.equals(string, that.string) &&
                    Objects.equals(map, that.map) &&
                    Objects.equals(list, that.list);
        }

        @Override
        public int hashCode() {
            return Objects.hash(string, integer, map, list);
        }
    }

    @Test
    public void objectToJson() throws IOException {
        assertEquals(ApiUtils.objectToJson(new SampleObject()).trim(), SampleObject.TARGET_JSON.trim());
    }

    @Test
    public void tryObjectToJson() {
        String validResponse = ApiUtils.tryObjectToJson(new SampleObject());
        assertNotNull(validResponse);
        assertEquals(SampleObject.TARGET_JSON, validResponse.trim());
    }

    @Test
    public void jsonToObject() throws IOException {
        assertEquals(ApiUtils.jsonToObject(SampleObject.TARGET_JSON, SampleObject.class), new SampleObject());
    }

    @Test
    public void jsonToObject1() {
    }
}