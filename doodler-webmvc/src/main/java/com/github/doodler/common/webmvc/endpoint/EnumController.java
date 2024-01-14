package com.github.doodler.common.webmvc.endpoint;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.webmvc.EnumDeclarations;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: EnumController
 * @Author: Fred Feng
 * @Date: 10/03/2021
 * @Version 1.0.0
 */
@Api(tags = "Enum common API")
@RestController
public class EnumController {

    @ApiOperation(value = "Get enumeration list of current application", notes = "Get enumeration list of current application")
    @GetMapping("/enums")
    public ApiResult<Map<String, Map<String, Object>>> getEnums() {
        return ApiResult.ok(EnumDeclarations.getEnums());
    }
}