// package com.github.doodler.common.gateway;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.buffer.DataBufferFactory;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.web.server.SecurityWebFilterChain;
//
// import com.github.doodler.common.ApiResult;
// import com.github.doodler.common.utils.JacksonUtils;
//
// import reactor.core.publisher.Mono;
//
/// **
// *
// * @Description: HttpSecurityConfig
// * @Author: Fred Feng
// * @Date: 03/11/2024
// * @Version 1.0.0
// */
// @Configuration(proxyBeanMethods = false)
// @EnableWebFluxSecurity
// public class HttpSecurityConfig {
//
// @Bean
// public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
// http.csrf().disable().httpBasic().disable().cors().and().authorizeExchange(exchange ->
// exchange.pathMatchers(
// "/oauth/**").permitAll().anyExchange().authenticated()).oauth2ResourceServer(
// ServerHttpSecurity.OAuth2ResourceServerSpec::jwt).exceptionHandling().authenticationEntryPoint((swe,
// e) -> {
// swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
// DataBufferFactory dbf = swe.getResponse().bufferFactory();
// return swe.getResponse().writeWith(Mono.just(dbf.wrap(writeValueAsJsonString(ApiResult.failed(
// "您没有访问权限")).getBytes())));
// }).accessDeniedHandler((swe, e) -> {
// swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
// DataBufferFactory dbf = swe.getResponse().bufferFactory();
// return swe.getResponse().writeWith(Mono.just(dbf.wrap(writeValueAsJsonString(ApiResult.failed(
// "您的访问是被禁止的")).getBytes())));
// });
// return http.build();
// }
//
// private String writeValueAsJsonString(ApiResult<?> result) {
// return JacksonUtils.toJsonString(result);
// }
// }
