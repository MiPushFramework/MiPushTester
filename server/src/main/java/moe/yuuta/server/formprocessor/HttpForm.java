package moe.yuuta.server.formprocessor;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

import io.vertx.core.buffer.Buffer;

public class HttpForm {
    public static Buffer toBuffer(Object object) {
        StringBuilder builder = new StringBuilder();
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields == null) return Buffer.buffer();
        for (Field field : fields) {
            field.setAccessible(true);
            FormData data = field.getAnnotation(FormData.class);
            if (data == null) continue;
            try {
                Object rawValue = field.get(object);
                if (rawValue == null) continue;
                try {
                    if (Double.parseDouble(rawValue.toString()) == 0) {
                        if (data.ignorable()) continue;

                    }
                } catch (NumberFormatException ignored) {
                }
                rawValue = rawValue.toString();
                if (field.getType().equals(String.class) && data.urlEncode()) {
                    rawValue = URLEncoder.encode(rawValue.toString(), "UTF-8");
                }
                builder.append(data.value());
                builder.append("=");
                builder.append(rawValue);
                builder.append("&");
            } catch (IllegalAccessException | UnsupportedEncodingException ignored) {}
        }
        String rawForm = builder.toString();
        rawForm = rawForm.substring(0, rawForm.length() - 1); // Remove the last '&'
        return Buffer.buffer(rawForm);
    }
}
