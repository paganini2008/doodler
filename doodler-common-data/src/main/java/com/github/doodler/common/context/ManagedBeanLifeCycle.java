package com.github.doodler.common.context;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @Description: ManagedBeanLifeCycle
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
public interface ManagedBeanLifeCycle extends InitializingBean, DisposableBean {

    default void destroy() throws Exception {
    }

}