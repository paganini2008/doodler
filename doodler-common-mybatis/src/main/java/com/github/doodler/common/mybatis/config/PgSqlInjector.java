package com.github.doodler.common.mybatis.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.github.yulichang.injector.MPJSqlInjector;
import com.github.yulichang.method.SelectJoinList;
import com.github.yulichang.method.SelectJoinMap;
import com.github.yulichang.method.SelectJoinMaps;
import com.github.yulichang.method.SelectJoinMapsPage;
import com.github.yulichang.method.SelectJoinOne;
import com.github.yulichang.method.SelectJoinPage;
import java.util.List;

/**
 * @Description: PgSqlInjector
 * @Author: Fred Feng
 * @Date: 07/12/2022
 * @Version 1.0.0
 */
public class PgSqlInjector extends MPJSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new SelectJoinOne());
        methodList.add(new SelectJoinList());
        methodList.add(new SelectJoinPage());
        methodList.add(new SelectJoinMap());
        methodList.add(new SelectJoinMaps());
        methodList.add(new SelectJoinMapsPage());
        return methodList;
    }
}