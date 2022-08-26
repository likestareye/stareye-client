package cn.stareye.opensource.stareyeclient.utils;

import cn.stareye.opensource.stareyeclient.DataCarrier;
import cn.stareye.opensource.stareyeclient.StarEyeClientException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * http工具.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
public class HttpUtils {

    private static HttpClient httpClient;

    private HttpUtils() {}

    public static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static final int SUCCESS = 200;

    public static HttpResponse exec(String url, Map<String, Object> parameters, DataCarrier dataCarrier) {
        if (StringUtils.isBlank(url)) {
            throw StarEyeClientException.newEx("url cannot be blank");
        }
        return switch (Objects.requireNonNull(dataCarrier)) {
            case JSON -> execJson(url, parameters);
            case FORM -> execForm(url, parameters);
            case MULTIPART_FORM -> execMultipartForm(url, parameters);
        };
    }

    private static HttpResponse execMultipartForm(String url, Map<String, Object> parameters) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(parameters)) {
            parameters.forEach((name, value) -> {
                if (value instanceof File file) {
                    entityBuilder.addBinaryBody(name, file);
                } else if (value instanceof byte[] bytes) {
                    entityBuilder.addBinaryBody(name, bytes);
                } else if (value instanceof InputStream is) {
                    entityBuilder.addBinaryBody(name, is);
                } else {
                    entityBuilder.addTextBody(name, value != null ? value.toString() : "");
                }
            });
            entityBuilder.setCharset(StandardCharsets.UTF_8);
            httpPost.setEntity(entityBuilder.build());
        }
        return internalExec(httpPost);
    }

    private static HttpResponse execForm(String url, Map<String, Object> parameters) {
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(parameters)) {
            List<NameValuePair> formParameters = new ArrayList<>();
            parameters.forEach((name, value) -> formParameters.add(new BasicNameValuePair(name, value.toString())));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(formParameters, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new StarEyeClientException(String.format("call failed, %s", e.getMessage()));
            }
        }
        return internalExec(httpPost);
    }

    private static HttpResponse execJson(String url, Map<String, Object> parameters) {
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(parameters)) {
            httpPost.setEntity(new StringEntity(JsonUtils.toJson(parameters), "UTF-8"));
            httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
        }
        return internalExec(httpPost);
    }


    private static HttpResponse internalExec(HttpPost httpPost) {
        try {
            return httpClient.execute(httpPost);
        } catch (Exception exception) {
            throw new StarEyeClientException(String.format("call failed, %s", exception.getMessage()));
        }
    }

    public static class HttpUtilsInit {

        protected HttpUtilsInit() {}

        protected static void init(HttpClient httpClient) {
            HttpUtils.httpClient = Objects.requireNonNull(httpClient);
        }

    }

}
