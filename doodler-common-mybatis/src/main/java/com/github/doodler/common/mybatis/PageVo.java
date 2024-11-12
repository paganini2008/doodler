package com.github.doodler.common.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.doodler.common.utils.BeanCopyUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: PageVo
 * @Author: Fred Feng
 * @Date: 16/11/2022
 * @Version 1.0.0
 */
@ApiModel(description = "Page vo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageVo<T> implements Serializable {

    private static final long serialVersionUID = 2176041783324956909L;

    @ApiModelProperty("Data records")
    private List<T> records;

    @ApiModelProperty("Current page")
    private long current;

    @ApiModelProperty("Page size")
    private long pageSize;

    @ApiModelProperty("Total")
    private long total;

    public PageVo(IPage<T> page) {
        this.setRecords(page.getRecords());
        this.setPageSize(page.getSize());
        this.setRecords(page.getRecords());
        this.setTotal(page.getTotal());
        this.setCurrent(page.getCurrent());
    }

    public <S> PageVo(IPage<S> page, Class<T> clz) {
        List<T> records = BeanCopyUtils.copyBeanList(page.getRecords(), clz);
        this.setPageSize(page.getSize());
        this.setRecords(records);
        this.setTotal(page.getTotal());
        this.setCurrent(page.getCurrent());
    }
}