package com.github.doodler.common.mybatis;

/**
 * Description:
 * Author: Vincent
 * Date: 2022/11/27 1:02 下午
 */
public interface StringPool {

    String COLUMN_START_TIME = "start_time";
    String COLUMN_END_TIME = "end_time";
    String COLUMN_TRANS_TIME = "trans_time";
    String COLUMN_LAST_LOGIN_TIME = "last_login_time";
    String COLUMN_GAME_TYPE = "game_type";
    String COLUMN_GAME_CODE = "game_code";
    String COLUMN_CREATED_AT = "created_at";
    String COLUMN_UPDATED_AT = "updated_at";
    String COLUMN_PROVIDER_CODE = "provider_code";
    String COLUMN_STATUS = "status";
    String COLUMN_VIP_LEVEL = "vip_level";
    String COLUMN_CREA = "vip_level";
    String COLUMN_DEPOSIT = "deposit";
    String COLUMN_WITHDRAWAL = "withdrawal";
    String COLUMN_BONUS = "bonus";
    String COLUMN_RAKEBACK = "rakeback";
    String COLUMN_CASHBACK = "cashback";
    String COLUMN_LEVEL_UP = "levelUp";
    String COLUMN_USER_CURRENCY = "user_currency";
    String COLUMN_USER_NAME = "username";
    String COLUMN_EMAIL_IGNORE_CASE = "lower(email)";

    String UNDERSCORE = "_";
    String STAR = "*";
    String AND = "&";
    String EQUALS = "=";

    String SQL_NULLS_LAST = "NULLS LAST";
    String SQL_LIMIT_1 = "limit 1";

    String KEY_CURRENCY = "currency";
    String KEY_ROUND_ID = "roundId";
    String KEY_TABLE_ALIAS = "tableAlias";
    String KEY_GAME_CODE = "gameCode";
    String KEY_CURRENCY_TYPE = "currency_type";

    String CONFIG_LAUNCH_URL = "launchUrl";
    String COLUMN_USER_ID = "user_id";
    String COLUMN_BALANCE = "balance";
    String KEY_USERNAME = "username";

    String KEY_VIP_LEVEL = "vipLevel";

    String COLUMN_EMAIL_SUBJECT = "subject";
    String COLUMN_EMAIL_RETRY_TIMES = "retry_times";
    String COLUMN_EMAIL_SENDER = "from_addr";
    String COLUMN_EMAIL_RECEIVER = "to_addr";
    String COLUMN_EMAIL_TEMPLATE_ID = "template_id";

    String SQL_LIMIT_SYNTAX_FORMAT = "limit %d offset %d";

    String COLUMN_CREATED_BY = "created_by";

    String COLUMN_PROVIDER_NAME = "provider_name";

    String COLUMN_DESCRIPTION = "description";
}