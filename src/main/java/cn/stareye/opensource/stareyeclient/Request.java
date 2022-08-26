package cn.stareye.opensource.stareyeclient;

/**
 * StarEye请求.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
public interface Request<Res extends Response> {

    /**
     * 请求路径, 假设请求此接口的全路径为:
     *      http://localhost:8080/content-path/controller/request-method,
     *      则接口的请求路径应该为: /controller/request-method.
     * @return 请求路径.
     */
    String requestPath();

    /**
     * 初始化请求参数.
     * @return NameValuePair.
     */
    NameValueDataPair initRequest();

    /**
     * 创建响应实例.
     * @return Res.
     */
    Res createResponseInstance();

    /**
     * 获取当此请求的数据载体格式.
     * @return DataCarrier.
     */
    DataCarrier dataCarrier();

}
