package io.doodler.i18n;

import static io.doodler.i18n.I18nConstants.I18N_CACHE_KEY_GENERATOR;
import static io.doodler.i18n.I18nConstants.I18N_CACHE_NAME;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import feign.Param;
import feign.RequestLine;
import io.doodler.common.ApiResult;
import io.doodler.feign.RestClient;

/**
 * @Description: IRemoteI18nService
 * @Author: Fred Feng
 * @Date: 31/12/2022
 * @Version 1.0.0
 */
@RestClient(serviceId = "crypto-common-service")
public interface IRemoteI18nService {

	@Cacheable(value = I18N_CACHE_NAME, keyGenerator = I18N_CACHE_KEY_GENERATOR, unless = "#result==null")
    @RequestLine("GET /common/i18n/message/{group}/{lang}/{messageKey}")
    ApiResult<String> getMessage(@Param("group") String group, @Param("lang") String lang,
                                 @Param("messageKey") String messageKey);

	@Cacheable(value = I18N_CACHE_NAME, keyGenerator = I18N_CACHE_KEY_GENERATOR, unless = "#result==null")
    @RequestLine("GET /common/i18n/message/{group}/{lang}")
    ApiResult<List<I18nMessageVO>> getMessages(@Param("group") String group, @Param("lang") String lang);
	
	@Cacheable(value = I18N_CACHE_NAME, keyGenerator = I18N_CACHE_KEY_GENERATOR, unless = "#result==null")
    @RequestLine("GET /common/i18n/messages/{group}/{messageKey}")
    ApiResult<List<I18nMessageVO>> getMessagesOfLangs(@Param("group") String group, @Param("messageKey") String messageKey);
}