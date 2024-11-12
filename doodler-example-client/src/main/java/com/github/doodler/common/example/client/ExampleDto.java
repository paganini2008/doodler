package com.github.doodler.common.example.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: GlobalExceptionHandler
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class ExampleDto {

    private Integer intValue;
    private Long longValue;
    private Double doubleValue;
    private BigDecimal bigValue;
    private Date dateValue;
    private LocalDateTime jdk8DateValue;
}