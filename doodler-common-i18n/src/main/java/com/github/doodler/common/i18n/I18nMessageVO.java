package com.github.doodler.common.i18n;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: I18nMessageVO
 * @Author: Fred Feng
 * @Date: 20/12/2022
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class I18nMessageVO {
    
    @ApiModelProperty("ID")
	private Integer id;

	@ApiModelProperty("Group")
	private String group;

	@ApiModelProperty("Language")
	private String lang;

	@ApiModelProperty("Message Key")
	private String messageKey;

	@ApiModelProperty("Message Text")
	private String messageText;

	@ApiModelProperty("Created time")
	private LocalDateTime createdAt;

	@ApiModelProperty("Updated time")
	private LocalDateTime updatedAt;
}