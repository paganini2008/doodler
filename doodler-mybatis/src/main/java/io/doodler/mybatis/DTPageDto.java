package io.doodler.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 * Author: Vincent
 * Date: 20/12/2022 2:51 pm
 */
@Data
@ApiModel(description = "Date time Page query dto")
public class DTPageDto extends PageInfo {

    @ApiModelProperty(value = "from time", example = "2022-12-01 10:00:00")
    private String from;

    @ApiModelProperty(value = "to time", example = "2022-12-01 10:00:00")
    private String to;

    public <T> QueryWrapper<T> toQueryWrapper(String fromColumn, String toColumn) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.ge(StringUtils.isNotBlank(from), fromColumn, from);
        // 增加结束时间的分区条件
        queryWrapper.ge(StringUtils.isNotBlank(from), toColumn, from);
        queryWrapper.le(StringUtils.isNotBlank(to), toColumn, to);
        return queryWrapper;
    }
}
