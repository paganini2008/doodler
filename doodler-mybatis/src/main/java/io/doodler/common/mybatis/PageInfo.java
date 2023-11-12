package io.doodler.common.mybatis;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 * Description: 分页查询对象
 * Author: Vincent
 * Date: 2021/5/25 2:56 下午
 */
@Data
@ApiModel(description = "Page query dto")
public class PageInfo {

    @ApiModelProperty(value = "Current Page", required = true, notes = "To start from 1")
    private Integer current = 1;

    @ApiModelProperty(value = "Page Size", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "Sort Data", required = false)
    private List<SortInfo> sorts;

    @ApiModelProperty(value = "Need to search total records or not", required = false, hidden = true)
    private boolean searchCount = true;

    public PageInfo() {
    }

    public PageInfo(Integer current, Integer pageSize) {
        this.current = Optional.ofNullable(current).orElse(1);
        this.pageSize = Optional.ofNullable(pageSize).orElse(10);
    }

    private void addSortInfo(String key, String dir) {
        if (sorts == null) {
            sorts = new ArrayList<>();
        }
        sorts.add(new SortInfo(key, dir));
    }

    public void addDescSort(String key) {
        this.addSortInfo(key, SqlKeyword.DESC.getSqlSegment());
    }

    public void addAscSort(String key) {
        this.addSortInfo(key, SqlKeyword.ASC.getSqlSegment());
    }
}