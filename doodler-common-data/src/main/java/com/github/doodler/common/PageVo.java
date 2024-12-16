package com.github.doodler.common;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: PageVo
 * @Author: Fred Feng
 * @Date: 14/12/2024
 * @Version 1.0.0
 */
@ApiModel(description = "Page Vo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageVo<T> {

    @ApiModelProperty("Data list")
    private List<T> content;

    @ApiModelProperty("Current page")
    private int page;

    @ApiModelProperty("Page size")
    private int pageSize;

    @ApiModelProperty("Total Records")
    private long totalRecords;

    @ApiModelProperty("Next Token")
    private Object nextToken;
}
