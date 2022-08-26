package cn.stareye.opensource.stareyeclient.test;

import cn.stareye.opensource.stareyeclient.DataCarrier;
import cn.stareye.opensource.stareyeclient.Response;
import cn.stareye.opensource.stareyeclient.mapping.MappedName;
import cn.stareye.opensource.stareyeclient.mapping.MappedNameRequest;

/**
 * @author: wjf
 * @date: 2022/8/8
 */
public class User extends MappedNameRequest {

    @MappedName(name = "parentName")
    private String name;

    @MappedName
    private Integer age;

    @MappedName(nested = true)
    private User child;

    @Override
    public String requestPath() {
        return "/child";
    }

    @Override
    public Response createResponseInstance() {
        return null;
    }

    @Override
    public DataCarrier dataCarrier() {
        return DataCarrier.JSON;
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

    public User getChild() {
        return child;
    }

    public void setChild(User child) {
        this.child = child;
    }
}
