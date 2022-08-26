/**
 * @author: wjf
 * @date: 2022/8/4
 */
module stareye.client {

    requires java.base;
    requires java.desktop;
    requires org.slf4j;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpmime;
    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires jdk.unsupported;

    exports cn.stareye.opensource.stareyeclient.mapping;
    exports cn.stareye.opensource.stareyeclient;

}