package moe.yuuta.server.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import java.io.IOException

object ApiUtils {
    @JvmStatic
    @Throws(JsonProcessingException::class)
    fun objectToJson(obj: Any?): String {
        return ObjectMapper().writeValueAsString(obj)
    }

    @JvmStatic
    fun tryObjectToJson(obj: Any?): String? {
        try {
            return objectToJson(obj)
        } catch (e: JsonProcessingException) {
            return null
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun <V> jsonToObject(json: String, t: Class<V>): V {
        return ObjectMapper().readValue(json, t)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun <V> jsonToObject (json: String, t: TypeReference<V>): V {
        return ObjectMapper().readValue(json, t)
    }

    @JvmStatic
    fun separateListToComma(list: List<String>): String {
        val builder = StringBuilder()
        for (value in list) {
            builder.append(value)
            builder.append(",")
        }
        var values = builder.toString()
        values = values.substring(0, values.length - 1)
        return values
    }
}
