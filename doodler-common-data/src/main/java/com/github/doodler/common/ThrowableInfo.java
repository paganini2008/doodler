package com.github.doodler.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: ThrowableInfo
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThrowableInfo implements Comparable<ThrowableInfo> {

    private @Nullable String path;
    private String message;
    private String[] content;
    private LocalDateTime createTime;

    public String[] getContent() {
        List<String> lines = new ArrayList<>();
        Arrays.stream(content).forEach(c -> {
            if (c.contains("\n")) {
                String[] args = c.split("\n");
                for (String arg : args) {
                    lines.add(arg);
                }
            } else {
                lines.add(c);
            }
        });
        return lines.toArray(new String[0]);
    }

    @Override
    public int compareTo(ThrowableInfo other) {
        return other.getCreateTime().compareTo(getCreateTime());
    }

}
