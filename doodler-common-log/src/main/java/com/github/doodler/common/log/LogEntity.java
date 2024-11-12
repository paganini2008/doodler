package com.github.doodler.common.log;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEntity implements Serializable {

    private static final long serialVersionUID = 3760249198105930853L;
    private String logLevel;
    private String logModule;

    private String logType;

    private String description;

    private Long startTime;

    private Integer spendTime;

    private String query;

    private String url;

    private String method;

    private String ip;

    private Object req;

    private Object res;

    private Object headers;
}
