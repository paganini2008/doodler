package com.github.doodler.common.utils;/**
 * @Description: KeyVal
 * @Author: Fred Feng
 * @Date: 12/03/2024
 * @Version: 1.0.0
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: KeyVal
 * @Author: Fred Feng
 * @Date: 12/03/2024
 * @Version: 1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KeyVal {

    private String key;
    private Object value;
}