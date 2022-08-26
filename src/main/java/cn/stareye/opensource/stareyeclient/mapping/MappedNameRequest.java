package cn.stareye.opensource.stareyeclient.mapping;

import cn.stareye.opensource.stareyeclient.DataCarrier;
import cn.stareye.opensource.stareyeclient.NameValueDataPair;
import cn.stareye.opensource.stareyeclient.Request;
import cn.stareye.opensource.stareyeclient.Response;
import cn.stareye.opensource.stareyeclient.utils.PredicateUtils;
import cn.stareye.opensource.stareyeclient.utils.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 映射名称的请求, 此请求的子类实现应该配合{@link MappedName}一起使用.
 *
 * @author: wjf
 * @date: 2022/8/4
 */
public abstract class MappedNameRequest<Res extends Response> implements Request<Res> {

    protected MappedNameRequest() {}

    @Override
    public NameValueDataPair initRequest() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        if (this.dataCarrier() == DataCarrier.JSON) {
            this.intelligentAnalyzeJson(parameters, this, "", true);
        } else {
            this.intelligentAnalyzeForm(parameters, this, "");
        }
        NameValueDataPair dataPair = new NameValueDataPair();
        parameters.forEach(dataPair::addPropertyValuePair);
        return dataPair;
    }

    private void intelligentAnalyzeJson(Map<String, Object> parentNode, Object target, String parentPropertyName, boolean isRoot) {
        Object currentTarget = target;
        Class<?> currentClass = target.getClass();
        while (currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            Map<String, Object> currentNode = null;
            for (Field field : fields) {
                if (!field.isAnnotationPresent(MappedName.class)) {
                    continue;
                }
                Object propertyValue = ReflectUtils.invokeGetter(currentTarget, field.getName());
                if (propertyValue == null) {
                    continue;
                }
                MappedName annotation = field.getAnnotation(MappedName.class);
                String mappedName = PredicateUtils.assertPassesAndReturnOrElse(StringUtils::isNotBlank, annotation.name(), field.getName());
                if (annotation.nested()) {
                    this.intelligentAnalyzeJson(parentNode, propertyValue, mappedName, false);
                } else {
                    if (currentNode == null && !isRoot) {
                        currentNode = new LinkedHashMap<>();
                        parentNode.put(parentPropertyName, currentNode);
                    }
                    if (!isRoot) {
                        currentNode.put(mappedName, propertyValue);
                    } else {
                        parentNode.put(mappedName, propertyValue);
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private void intelligentAnalyzeForm(Map<String, Object> container, Object target, String prefix) {
        Object currentTarget = target;
        Class<?> currentClass = target.getClass();
        while (currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(MappedName.class)) {
                    continue;
                }
                Object propertyValue = ReflectUtils.invokeGetter(currentTarget, field.getName());
                if (propertyValue == null) {
                    continue;
                }
                MappedName annotation = field.getAnnotation(MappedName.class);
                String mappedName = assembleMappedName(annotation, field.getName(), prefix);
                if (annotation.nested()) {
                    this.intelligentAnalyzeForm(container, propertyValue, mappedName);
                } else {
                    container.put(mappedName, propertyValue);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private String assembleMappedName(MappedName annotation, String propertyName, String prefix) {
        String mappedName = PredicateUtils.assertPassesAndReturnOrElse(StringUtils::isNotBlank, annotation.name(), propertyName);
        if (StringUtils.isBlank(prefix)) {
            return mappedName;
        }
        return prefix + "." + mappedName;
    }

}
