package com.github.doodler.common;

import java.sql.SQLException;
import org.springframework.core.NestedRuntimeException;

/**
 * @Description: DefaultExceptionTransformer
 * @Author: Fred Feng
 * @Date: 20/12/2023
 * @Version 1.0.0
 */
public class DefaultExceptionTransformer implements ExceptionTransformer {

    @Override
    public Throwable transform(Throwable e) {
        if (e instanceof SQLException) {
            return new BizException(ErrorCode.sqlError("Sql error: " + e.getClass().getName()));
        } else if (e instanceof NestedRuntimeException) {
            return new BizException(
                    ErrorCode.webServerError("Web server error: " + e.getClass().getName()));
        } else if (e instanceof RuntimeException) {
            return new BizException(
                    ErrorCode.internalServerError("Unknown error: " + e.getClass().getName()));
        }
        return e;
    }
}
