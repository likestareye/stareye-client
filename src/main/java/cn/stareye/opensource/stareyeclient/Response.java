package cn.stareye.opensource.stareyeclient;

/**
 * StarEye响应.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
public interface Response {

    /**
     * 初始化响应结果.
     * @param result result.
     */
    void initResponse(NameValueDataPair result);

}
