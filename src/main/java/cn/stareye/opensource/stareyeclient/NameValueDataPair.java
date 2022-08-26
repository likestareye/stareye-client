package cn.stareye.opensource.stareyeclient;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 承载请求响应的传递实体.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
public class NameValueDataPair {

    private final Map<String, Object> nameValueData = new LinkedHashMap<>();

    public NameValueDataPair() {
    }

    /**
     * 通用的属性添加方式, 也可以添加二进制数据, 但是对于二进制数据更推荐
     * {@link #addBinaryValuePair(String, File)},
     * {@link #addBinaryValuePair(String, InputStream)},
     * {@link #addBinaryValuePair(String, byte[])}
     * @param name 属性名称.
     * @param value 属性值.
     * @return NameValueDataPair.
     */
    public NameValueDataPair addPropertyValuePair(String name, Object value) {
        add(name, value);
        return this;
    }

    /**
     * 添加文本数据的专用方法.
     * @param name 属性名称.
     * @param value 属性值.
     * @return
     */
    public NameValueDataPair addTextValuePair(String name, String value) {
        add(name, value);
        return this;
    }

    /**
     * 添加二进制数据的专用方法, 也可以看看:
     * {@link #addBinaryValuePair(String, InputStream)},
     * {@link #addBinaryValuePair(String, byte[])}
     * @param name 属性名称.
     * @param value 属性值.
     * @return NameValueDataPair.
     */
    public NameValueDataPair addBinaryValuePair(String name, File value) {
        add(name, value);
        return this;
    }

    /**
     * 添加二进制数据的专用方法, 也可以看看:
     * {@link #addBinaryValuePair(String, File)},
     * {@link #addBinaryValuePair(String, byte[])}
     * @param name 属性名称.
     * @param value 属性值.
     * @return NameValueDataPair.
     */
    public NameValueDataPair addBinaryValuePair(String name, byte[] value) {
        add(name, value);
        return this;
    }

    /**
     * 添加二进制数据的专用方法, 也可以看看:
     * {@link #addBinaryValuePair(String, InputStream)},
     * {@link #addBinaryValuePair(String, File)}
     * @param name 属性名称.
     * @param value 属性值.
     * @return NameValueDataPair.
     */
    public NameValueDataPair addBinaryValuePair(String name, InputStream value) {
        add(name, value);
        return this;
    }

    /**
     * 遍历整个NameValueDataPair.
     * @param consumer BiConsumer<String, Object>.
     */
    public void forEach(BiConsumer<String, Object> consumer) {
        Objects.requireNonNull(consumer);
        this.nameValueData.forEach(consumer);
    }

    /**
     * 深度的复制了一个map, 此方法是深拷贝的.
     * @return Map<String, Object>.
     */
    public Map<String, Object> deepToMap() {
        return new LinkedHashMap<>(this.nameValueData);
    }

    /**
     * 私有的添加方法.
     * @param name String.
     * @param value Object.
     */
    private void add(String name, Object value) {
        this.nameValueData.put(name, value);
    }

}
