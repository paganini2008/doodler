package com.github.doodler.common.jpa;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;

import lombok.Data;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: LambdaUtils
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
@UtilityClass
public class LambdaUtils {

    @Data
    static class LambdaInfo {

        private String className;
        private String attributeName;

    }

    public <X> LambdaInfo inspect(SerializedFunction<X, ?> function) {
        SerializedLambda object;
        try {
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            object = (SerializedLambda) method.invoke(function);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        String className = object.getImplClass().replace('/', '.');
        String methodName = object.getImplMethodName();
        String propertyName;
        if (methodName.startsWith("is")) {
            propertyName = methodName.substring(2);
        } else if (methodName.startsWith("get") || methodName.startsWith("set")) {
            propertyName = methodName.substring(3);
        } else {
            throw new IllegalArgumentException("Error parsing property name '" + methodName +
                    "'.  Didn't start with 'is', 'get' or 'set'.");
        }
        if (propertyName.length() == 1 || (propertyName.length() > 1 && !Character.isUpperCase(propertyName.charAt(1)))) {
            propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ENGLISH) + propertyName.substring(1);
        }
        LambdaInfo lambdaInfo = new LambdaInfo();
        lambdaInfo.setClassName(className);
        lambdaInfo.setAttributeName(propertyName);
        return lambdaInfo;
    }

}
