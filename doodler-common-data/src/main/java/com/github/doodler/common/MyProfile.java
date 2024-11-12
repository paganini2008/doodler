package com.github.doodler.common;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @Description: MyProfile
 * @Author: Fred Feng
 * @Date: 27/02/2023
 * @Version 1.0.0
 */
@Data
public class MyProfile {

    private Long userId;
    private String username;
    private String avatar;
    private Boolean u2fEnable;
    private Boolean emailVerified;
    private String referralCode;
    private Long affiliateUserId;
    private String levelUpPercent;
    private String securityKey;
    
    private Level level;
    private Chat chat;

    @Data
    public static class Level {

    	private Integer levelId;
        private Integer levelNumber;
        private String levelName;
        private String levelIcon;
    }

    @Data
    public static class Chat {

    	private Boolean isAdmin;
        private Boolean enabled;
        private Boolean agreed;
        private LocalDateTime expiredAt;
    }
}