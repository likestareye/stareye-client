package cn.stareye.opensource.stareyeclient.utils;

import cn.stareye.opensource.stareyeclient.StarEyeClientException;

import java.util.function.Predicate;

/**
 * Predicate工具.
 *
 * @author: wjf
 * @date: 2022/8/9
 */
public class PredicateUtils {

    private PredicateUtils() {}

    public static <T> T assertPassesAndReturn(Predicate<T> predicate, T target) {
        if (predicate.test(target)) {
            return target;
        }
        throw StarEyeClientException.newEx("assert fail, target: {}", target);
    }

    public static <T> T assertPassesAndReturnOrElse(Predicate<T> predicate, T target, T elseVal) {
        return predicate.test(target) ? target : elseVal;
    }

}
