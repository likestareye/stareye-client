package cn.stareye.opensource.stareyeclient.test;

import cn.stareye.opensource.stareyeclient.DataCarrier;
import cn.stareye.opensource.stareyeclient.mapping.MappedName;
import cn.stareye.opensource.stareyeclient.mapping.MappedNameRequest;

import java.io.File;

/**
 * @author: wjf
 * @date: 2022/8/22
 */
public class PostReq extends MappedNameRequest<PostRes> {

    @MappedName
    private String name;

    @MappedName(name = "Age")
    private Integer age;

    @MappedName(name = "PostData", nested = true)
    private PostData postData;

    @Override
    public String requestPath() {
        return "/postTest";
    }

    @Override
    public PostRes createResponseInstance() {
        return new PostRes();
    }

    @Override
    public DataCarrier dataCarrier() {
        return DataCarrier.FORM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public PostData getPostData() {
        return postData;
    }

    public void setPostData(PostData postData) {
        this.postData = postData;
    }
}
