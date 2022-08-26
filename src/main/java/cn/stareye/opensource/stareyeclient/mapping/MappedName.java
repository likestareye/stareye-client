package cn.stareye.opensource.stareyeclient.mapping;

import java.lang.annotation.*;

/**
 * 用来映射属性名称, 使用此注解的类必须遵循JavaBean规范.
 * 注意:
 * 1.
 * 此注解仅支持对象类型, 不支持集合类型,
 * 包括{@link java.util.Collection}, {@link java.util.Map}, 各种类型的数组, 后续会逐步支持.
 * 2.
 * 相同对象不得相互嵌套, 如果将对象本身作为对象的一个属性, 则此字段不会被解析.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MappedName {

    /**
     * 对象嵌套名称, 如果是嵌套对象则此属性必须存在, 默认则是字段名称.
     * @return String.
     */
    String name() default "";

    /**
     * 是否是嵌套属性, 如果是嵌套属性, 则属性前缀则会生效.
     * @return boolean.
     */
    boolean nested() default false;
}
