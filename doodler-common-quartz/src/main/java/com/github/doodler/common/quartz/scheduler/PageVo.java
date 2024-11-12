package com.github.doodler.common.quartz.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: PageVo
 * @Author: Fred Feng
 * @Date: 22/10/2023
 * @Version 1.0.0
 */
@ApiModel("Page Data List")
@NoArgsConstructor
@Getter
@Setter
public class PageVo<T> {

    @ApiModelProperty("Data list for each page")
    private List<T> content;

    @ApiModelProperty("Current page number")
    private int pageNumber;

    @ApiModelProperty("Each page display size")
    private int pageSize;

    @ApiModelProperty("Total Pages")
    private long totalPages;

    @ApiModelProperty("Total Records")
    private long totalRecords;
}