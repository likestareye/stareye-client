package cn.stareye.opensource.stareyeclient.utils;

import cn.stareye.opensource.stareyeclient.StarEyeClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * json工具.
 *
 * @author: wjf
 * @date: 2022/7/12
 */
public class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper O = new ObjectMapper();

    public static String toJson(Object target) {
        try {
            return O.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw StarEyeClientException.newEx(e.toString());
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return O.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw StarEyeClientException.newEx(e.toString());
        }
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        try {
            return O.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            throw StarEyeClientException.newEx(e.toString());
        }
    }

    public static ArrayNode createArrayNode() {
        return O.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return O.createObjectNode();
    }

    public static ObjectNode readObjectTree(String json) {
        try {
            return (ObjectNode) O.readTree(json);
        } catch (JsonProcessingException e) {
            throw StarEyeClientException.newEx(e.toString());
        }
    }

    public static ArrayNode readArrayTree(String json) {
        try {
            return (ArrayNode) O.readTree(json);
        } catch (JsonProcessingException e) {
            throw StarEyeClientException.newEx(e.toString());
        }
    }

}
