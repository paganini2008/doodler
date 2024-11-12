/**
 * Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.doodler.common.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.lang.Nullable;

/**
 * 
 * @Description: EnhancedNamedParameterJdbcDaoSupport
 * @Author: Fred Feng
 * @Date: 31/08/2024
 * @Version 1.0.0
 */
public class EnhancedNamedParameterJdbcDaoSupport extends NamedParameterJdbcDaoSupport {

    @Nullable
    private EnhancedNamedParameterJdbcTemplate jdbcTemplate;

    @Override
    protected void initTemplateConfig() {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate != null) {
            this.jdbcTemplate = new EnhancedNamedParameterJdbcTemplate(jdbcTemplate);
        }
    }

    @Override
    public EnhancedNamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return this.jdbcTemplate;
    }

}
