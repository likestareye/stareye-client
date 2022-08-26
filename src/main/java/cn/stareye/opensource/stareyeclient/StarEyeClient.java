package cn.stareye.opensource.stareyeclient;

/**
 * StarEyeClient请求客户端, 所有请求一律采用POST方法.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
@FunctionalInterface
public interface StarEyeClient {

    /**
     * 执行http-post请求.
     * @param req 请求体.
     * @return 响应体.
     * @param <Req> 请求.
     * @param <Res> 响应.
     */
    <Req extends Request<Res>, Res extends Response> Res execute(Req req);

}
