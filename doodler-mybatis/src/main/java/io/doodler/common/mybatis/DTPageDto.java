package io.doodler.common.mybatis;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @Description: DTPageDto
 * @Author: Fred Feng
 * @Date: 12/11/2023
 * @Version 1.0.0
 */
@ApiModel(description = "Date time Page query dto")
public class DTPageDto extends PageInfo {

    @ApiModelProperty(value = "from time", example = "2022-12-01 10:00:00")
    private String from;

    @ApiModelProperty(value = "to time", example = "2022-12-01 10:00:00")
    private String to;

    public <T> QueryWrapper<T> toQueryWrapper(String fromColumn, String toColumn) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.ge(StringUtils.isNotBlank(from), fromColumn, from);
        queryWrapper.ge(StringUtils.isNotBlank(from), toColumn, from);
        queryWrapper.le(StringUtils.isNotBlank(to), toColumn, to);
        return queryWrapper;
    }
}
