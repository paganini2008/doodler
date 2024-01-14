package com.github.doodler.common.mybatis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: PageDto
 * @Author: Fred Feng
 * @Date: 14/03/2022
 * @Version 1.0.0
 */
@Data
@ApiModel(description = "Page query dto")
public class PageDto {

    @ApiModelProperty(value = "Current Page", required = true, notes = "To start from 1")
    private Integer current = 1;

    @ApiModelProperty(value = "Page Size", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "Sort Data", required = false)
    private List<SortInfo> sorts;

    @ApiModelProperty(value = "Need to search total records or not", required = false, hidden = true)
    private boolean searchCount = true;

    public PageDto() {
    }

    public PageDto(Integer current, Integer pageSize) {
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
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SortInfo {

        @ApiModelProperty("Sort Field")
        private String sortField;
        @ApiModelProperty("Sort Type, ASC or DESC")
        private String sortType;
    }
}