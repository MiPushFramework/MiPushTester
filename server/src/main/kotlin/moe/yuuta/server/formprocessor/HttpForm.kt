package moe.yuuta.server.formprocessor

import io.vertx.core.buffer.Buffer
import java.lang.reflect.Field
import java.net.URLEncoder

object HttpForm {
    @JvmStatic
    fun toBuffer(obj: Any): Buffer {
        val builder = StringBuilder()
        val fields: Array<Field>? = obj::class.java.declaredFields
        if (fields == null) return Buffer.buffer()
        for (field in fields) {
            field.isAccessible = true
            val data: FormData? = field.getAnnotation(FormData::class.java)
            if (data == null) continue
            try {
                var rawValue: Any? = field.get(obj)
                if (rawValue == null) continue
                if (data.ignorable && rawValue.toString() == "") continue
                try {
                    if (rawValue.toString().toDouble() == "0".toDouble()) {
                        if (data.ignorable) continue
                    }
                } catch (ignored: NumberFormatException) {
                }
                rawValue = rawValue.toString()
                if (field.type.equals(String::class.java) && data.urlEncode) {
                    rawValue = URLEncoder.encode(rawValue.toString(), "UTF-8")
                }
                builder.append(data.name)
                builder.append("=")
                builder.append(rawValue)
                builder.append("&")
            } catch (ignored: Exception) {}
        }
        var rawForm = builder.toString()
        rawForm = rawForm.substring(0, rawForm.length - 1) // Remove the last '&'
        return Buffer.buffer(rawForm)
    }
}
