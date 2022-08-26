package cn.stareye.opensource.stareyeclient;

import cn.stareye.opensource.stareyeclient.utils.HttpUtils;
import cn.stareye.opensource.stareyeclient.utils.JsonUtils;
import cn.stareye.opensource.stareyeclient.utils.PredicateUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * StarEyeClient请求客户端配置.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
public class StarEyeClientConf {

    /**
     * 服务器地址, 例如: http://[host]:[port].
     */
    private String serverAddress;

    /**
     * 内容路径, 例如: /contentPath.
     */
    private String contentPath;

    /**
     * 请求处理器, 可以对请求参数进行统一的额外处理, 例如: 对参数进行数字签名.
     */
    private Consumer<Map<String, Object>> requestHandler;

    /**
     * 响应处理器, 应该对{@link HttpResponse}进行处理, 然后将结果以键值对的形式存放入{@link NameValueDataPair}中,
     * 此过程是必须的.
     */
    private BiConsumer<HttpResponse, NameValueDataPair> responseHandler;

    private HttpClientComponent httpClientComponent;

    public StarEyeClientConf() {
        this.requestHandler = parameters -> {
        };
        this.responseHandler = (httpResponse, nameValueDataPair) -> {
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() != HttpUtils.SUCCESS) {
                throw new StarEyeClientException(String.format("call failed, Code: [%s], ReasonPhrase: [%s], Caused by: [%s]", statusLine.getStatusCode(), statusLine.getReasonPhrase(), statusLine.toString()));
            }
            HttpEntity entity = httpResponse.getEntity();
            try {
                String result = EntityUtils.toString(entity, "UTF-8");
                HttpUtils.logger.info("The execution result of this request: {}", result);
                Map<String, Object> responseMap = JsonUtils.fromJson(result, new TypeReference<Map<String, Object>>() {
                });
                responseMap.forEach(nameValueDataPair::addPropertyValuePair);
            } catch (IOException e) {
                throw new StarEyeClientException(e.toString());
            }
        };
        this.httpClientComponent = new HttpClientComponent(3, 500, 50, 60000L);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = PredicateUtils.assertPassesAndReturn(StringUtils::isNotBlank, serverAddress);
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = PredicateUtils.assertPassesAndReturn(StringUtils::isNotBlank, contentPath);
    }

    public BiConsumer<HttpResponse, NameValueDataPair> getResponseHandler() {
        return responseHandler;
    }

    public void setResponseHandler(BiConsumer<HttpResponse, NameValueDataPair> responseHandler) {
        this.responseHandler = PredicateUtils.assertPassesAndReturn(Objects::nonNull, responseHandler);
    }

    public Consumer<Map<String, Object>> getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(Consumer<Map<String, Object>> requestHandler) {
        this.requestHandler = PredicateUtils.assertPassesAndReturn(Objects::nonNull, requestHandler);
    }

    public HttpClientComponent getHttpClientComponent() {
        return httpClientComponent;
    }

    public void setHttpClientComponent(HttpClientComponent httpClientComponent) {
        this.httpClientComponent = httpClientComponent;
    }

    public static class HttpClientComponent extends HttpUtils.HttpUtilsInit {

        private static final Logger logger = LoggerFactory.getLogger(HttpClientComponent.class);

        private final CloseableHttpClient httpClient;

        public HttpClientComponent(int retryCount, int maxConnTotal, int defaultMaxPerRoute, long httpIdleTimeout) {
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
            poolingHttpClientConnectionManager.setMaxTotal(maxConnTotal);
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
            this.httpClient = HttpClients.custom()
                    .setConnectionManager(poolingHttpClientConnectionManager)
                    .evictExpiredConnections()
                    .evictIdleConnections(httpIdleTimeout, TimeUnit.MILLISECONDS)
                    .setRetryHandler(((exception, executionCount, context) -> {
                        if (executionCount > retryCount) {
                            // 重试超过retryCount次, 放弃请求
                            logger.error("retry has more than 3 time, give up request");
                            return false;
                        }
                        if (exception instanceof NoHttpResponseException) {
                            // 服务器没有响应, 可能是服务器断开了连接, 应该重试
                            logger.error("receive no response from server, retry", exception);
                            return true;
                        }
                        if (exception instanceof SSLHandshakeException) {
                            // SSL握手异常
                            logger.error("SSL hand shake exception", exception);
                            return false;
                        }
                        if (exception instanceof ConnectTimeoutException) {
                            // 连接超时
                            logger.error("Connection Time out", exception);
                            return false;
                        }
                        if (exception instanceof InterruptedIOException) {
                            // 超时
                            logger.error("InterruptedIOException", exception);
                            return false;
                        }
                        if (exception instanceof UnknownHostException) {
                            // 服务器不可达
                            logger.error("server host unknown", exception);
                            return false;
                        }
                        if (exception instanceof SSLException) {
                            // SSL异常
                            logger.error("SSLException", exception);
                            return false;
                        }
                        HttpClientContext httpClientContext = HttpClientContext.adapt(context);
                        HttpRequest request = httpClientContext.getRequest();
                        // 如果请求不是关闭连接的请求
                        return !(request instanceof HttpEntityEnclosingRequest);
                    }))
                    .setDefaultRequestConfig(RequestConfig.custom().build())
                    .build();
            init(httpClient);

            // JVM 停止或重启时，关闭连接池释放掉连接(跟数据库连接池类似)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {

                try {
                    if (httpClient != null) {
                        httpClient.close();
                    }
                } catch (IOException e) {
                    logger.error("error when close httpClient: {}", e.toString());
                }

            }));
        }

    }

}
