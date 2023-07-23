package io.doodler.mybatis;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author John Chen
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortInfo {

    @ApiModelProperty("Sort Key")
    private String key;
    @ApiModelProperty("Sort type, ASC, DESC")
    private String dir;
}