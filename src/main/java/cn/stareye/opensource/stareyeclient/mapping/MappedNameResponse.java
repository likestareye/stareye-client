package cn.stareye.opensource.stareyeclient.mapping;

import cn.stareye.opensource.stareyeclient.NameValueDataPair;
import cn.stareye.opensource.stareyeclient.Response;
import cn.stareye.opensource.stareyeclient.utils.PredicateUtils;
import cn.stareye.opensource.stareyeclient.utils.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 映射名称的响应, 此响应的子类实现应该配合{@link MappedName}一起使用.
 *
 * @author: wjf
 * @date: 2022/8/8
 */
public abstract class MappedNameResponse implements Response {

    protected MappedNameResponse() {
    }

    @Override
    public void initResponse(NameValueDataPair result) {
        Map<String, Object> response = new LinkedHashMap<>();
        result.forEach(response::put);
        this.intelligentAnalyze(response, this);
    }

    private void intelligentAnalyze(Map<String, Object> currentNode, Object target) {
        Object currentTarget = target;
        Class<?> currentClass = target.getClass();
        while (currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            Map<String, Field> fieldMap = Arrays.stream(fields)
                    .filter(f -> f.isAnnotationPresent(MappedName.class))
                    .collect(Collectors.toMap(
                            f -> {
                                MappedName annotation = f.getAnnotation(MappedName.class);
                                return PredicateUtils.assertPassesAndReturnOrElse(StringUtils::isNotBlank, annotation.name(), f.getName());
                            },
                            f -> f
                    ));
            fieldMap.forEach((mappedName, field) -> {
                if (currentNode.containsKey(mappedName)) {
                    Object propertyValue = currentNode.get(mappedName);
                    MappedName annotation = field.getAnnotation(MappedName.class);
                    if (annotation.nested()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> childNode = (Map<String, Object>) propertyValue;
                        Object childPropertyValue = ReflectUtils.newInstance(field.getType());
                        this.intelligentAnalyze(childNode, childPropertyValue);
                        ReflectUtils.invokeSetter(currentTarget, field.getName(), childPropertyValue);
                    } else {
                        ReflectUtils.invokeSetter(currentTarget, field.getName(), propertyValue);
                    }
                }
            });
            currentClass = currentClass.getSuperclass();
        }
    }

}
