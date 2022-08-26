package cn.stareye.opensource.stareyeclient;

import cn.stareye.opensource.stareyeclient.utils.HttpUtils;
import org.apache.http.HttpResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 标准的StarEyeClient请求客户端.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
public class StandardStarEyeClient implements StarEyeClient {

    private final StarEyeClientConf conf;

    public StandardStarEyeClient(StarEyeClientConf conf) {
        this.conf = conf;
    }

    @Override
    public <Req extends Request<Res>, Res extends Response> Res execute(Req req) {
        NameValueDataPair requestPair = req.initRequest();
        Map<String, Object> parameters = new LinkedHashMap<>();
        requestPair.forEach(parameters::put);
        this.conf.getRequestHandler().accept(parameters);
        HttpResponse httpResponse = HttpUtils.exec(buildUrl(req.requestPath()), parameters, req.dataCarrier());
        NameValueDataPair responsePair = new NameValueDataPair();
        this.conf.getResponseHandler().accept(httpResponse, responsePair);
        Res response = req.createResponseInstance();
        response.initResponse(responsePair);
        return response;
    }

    private String buildUrl(String api) {
        String serverAddress = this.conf.getServerAddress();
        StringBuilder urlBuilder = new StringBuilder(serverAddress);
        String contentPath = this.conf.getContentPath();
        this.assembleUrl(urlBuilder, serverAddress, contentPath);
        this.assembleUrl(urlBuilder, contentPath, api);
        return urlBuilder.toString();
    }

    private void assembleUrl(StringBuilder urlBuilder, String pre, String next) {
        boolean preEndsWithSlash = pre.endsWith("/");
        boolean nextStartsWithSlash = next.startsWith("/");
        if (preEndsWithSlash) {
            if (nextStartsWithSlash) {
                urlBuilder.append(next.substring(1));
            } else {
                urlBuilder.append(next);
            }
        } else {
            if (nextStartsWithSlash) {
                urlBuilder.append(next);
            } else {
                urlBuilder.append("/");
                urlBuilder.append(next);
            }
        }
    }
}
