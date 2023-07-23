package id.doodler.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import io.doodler.common.utils.JacksonUtils;

/**
 * @Description: JsonWellformedValidator
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
public class JsonWellformedValidator implements ConstraintValidator<JsonWellformed, String> {

    private Class<?> testClass;

    @Override
    public void initialize(JsonWellformed anno) {
        this.testClass = anno.test();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            try {
                JacksonUtils.parseJson(value, testClass);
            } catch (RuntimeException e) {
                return false;
            }
        }
        return true;
    }
}