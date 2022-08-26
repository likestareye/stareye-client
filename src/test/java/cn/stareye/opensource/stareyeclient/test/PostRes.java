package cn.stareye.opensource.stareyeclient.test;

import cn.stareye.opensource.stareyeclient.mapping.MappedName;
import cn.stareye.opensource.stareyeclient.mapping.MappedNameResponse;

/**
 * @author: wjf
 * @date: 2022/8/22
 */
public class PostRes extends MappedNameResponse {

    @MappedName
    private String name;

    @MappedName
    private Integer age;

    @MappedName(nested = true)
    private PostData postData;

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

    @Override
    public String toString() {
        return "PostRes{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", postData=" + postData +
                '}';
    }
}
