package cn.stareye.opensource.stareyeclient.test;

import cn.stareye.opensource.stareyeclient.mapping.MappedName;

/**
 * @author: wjf
 * @date: 2022/8/22
 */
public class PostData {

    @MappedName
    private String data1;

    @MappedName
    private String data2;

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }
}
