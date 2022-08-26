package cn.stareye.opensource.stareyeclient.utils;

import cn.stareye.opensource.stareyeclient.StarEyeClientException;
import sun.misc.Unsafe;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 反射工具.
 *
 * @author: wjf
 * @date: 2022/8/9
 */
public class ReflectUtils {

    private ReflectUtils() {}

    /**
     * 执行setter.
     * @param target 目标对象.
     * @param propertyName 目标对象的属性.
     * @param propertyValue 此属性需要设置的值.
     */
    public static void invokeSetter(Object target, String propertyName, Object propertyValue) {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, target.getClass());
            propertyDescriptor.getWriteMethod().invoke(target, propertyValue);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException exception) {
            throw StarEyeClientException.newEx(exception.toString());
        }
    }

    /**
     * 执行getter.
     * @param target 目标对象.
     * @param propertyName 目标对象的属性.
     * @return 此属性的值.
     */
    public static Object invokeGetter(Object target, String propertyName) {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, target.getClass());
            return propertyDescriptor.getReadMethod().invoke(target);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException exception) {
            throw StarEyeClientException.newEx(exception.toString());
        }
    }

    /**
     * 创建一个新实例.
     * @param cls cls.
     * @return 目标对象.
     */
    public static Object newInstance(Class<?> cls) {
        return InstanceCreator.newInstance(cls);
    }

    /**
     * 实例创建者, 用来创建新实例.
     * 通过调用无参构造方法(任意修饰符都可)创建一个新实例,
     * 当没有无参构造方法的时候, 将通过直接操作内存的方式
     * 创建(分配)一个新实例, 通过此方式创建的新实例不会调
     * 用任何构造方法.
     */
    private static final class InstanceCreator {

        private static final Unsafe U;

        static {
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.trySetAccessible();
                U = (Unsafe) field.get(null);
            } catch (IllegalAccessException | NoSuchFieldException exception) {
                throw StarEyeClientException.newEx(exception.toString());
            }
        }

        private InstanceCreator() {}

        @SuppressWarnings("unchecked")
        private static <T> T newInstance(Class<T> instanceClass) {
            try {
                Constructor<T> declaredConstructor = instanceClass.getDeclaredConstructor();
                declaredConstructor.trySetAccessible();
                return declaredConstructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                try {
                    return (T) U.allocateInstance(instanceClass);
                } catch (InstantiationException e) {
                    throw StarEyeClientException.newEx(exception.toString());
                }
            }
        }

    }

}
