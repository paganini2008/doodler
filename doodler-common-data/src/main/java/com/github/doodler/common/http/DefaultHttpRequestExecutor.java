package com.github.doodler.common.http;

import static com.github.doodler.common.http.HttpRequest.CURRENT_HTTP_REQUEST;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.utils.LruMap;
import com.github.doodler.common.utils.MultiMapMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: DefaultHttpRequestExecutor
 * @Author: Fred Feng
 * @Date: 20/07/2023
 * @Version 1.0.0
 */
@Slf4j
public class DefaultHttpRequestExecutor implements HttpRequestExecutor {

    private static final MultiMapMap<String, String, RetryTemplate> retryTemplateCache = new MultiMapMap<>(() -> {
        return new LruMap<String, RetryTemplate>(256);
    });

    private static final MultiMapMap<String, String, Semaphore> semaphoreCache = new MultiMapMap<>(() -> {
        return new ConcurrentHashMap<String, Semaphore>();
    });

    private final RestTemplate restTemplate;

    public DefaultHttpRequestExecutor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> ResponseEntity<T> perform(HttpRequest httpRequest, Class<T> responseType) {
        String provider = httpRequest.getHostUrl();
        String group = StringUtils.isNotBlank(httpRequest.getGroup()) ? httpRequest.getGroup() : httpRequest.getPath();
        if (httpRequest.getMaxAttempts() <= 0) {
            return doExchange(provider, group, httpRequest, responseType);
        }
        RetryTemplate retryTemplate = retryTemplateCache.get(provider, group, () -> {
            return new RetryTemplateBuilder().maxAttempts(httpRequest.getMaxAttempts()).retryOn(httpRequest.getRetryOn())
                    .withListeners(retryListenerContainer.getRetryListeners()).build();
        });
        return retryTemplate.execute(context -> {
            context.setAttribute(CURRENT_HTTP_REQUEST, httpRequest);
            return doExchange(provider, group, httpRequest, responseType);
        }, context -> {
            context.removeAttribute(CURRENT_HTTP_REQUEST);
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e : new RestClientException(e.getMessage(), e);
        });
    }

    @Override
    public <T> ResponseEntity<T> perform(HttpRequest httpRequest, ParameterizedTypeReference<T> typeReference) {
        String provider = httpRequest.getHostUrl();
        String group = StringUtils.isNotBlank(httpRequest.getGroup()) ? httpRequest.getGroup() : httpRequest.getPath();
        if (httpRequest.getMaxAttempts() <= 0) {
            return doExchange(provider, group, httpRequest, typeReference);
        }
        RetryTemplate retryTemplate = retryTemplateCache.get(provider, group, () -> {
            return new RetryTemplateBuilder().maxAttempts(httpRequest.getMaxAttempts()).retryOn(httpRequest.getRetryOn())
                    .withListeners(retryListenerContainer.getRetryListeners()).build();
        });
        return retryTemplate.execute(context -> {
            context.setAttribute(CURRENT_HTTP_REQUEST, httpRequest);
            return doExchange(provider, group, httpRequest, typeReference);
        }, context -> {
            context.removeAttribute(CURRENT_HTTP_REQUEST);
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e : new RestClientException(e.getMessage(), e);
        });
    }

    private HttpEntity<?> getHttpEntity(HttpRequest httpRequest) {
        if (httpRequest.getBody() != null && httpRequest.getHeaders() != null) {
            return new HttpEntity<Object>(httpRequest.getBody(), httpRequest.getHeaders());
        } else if (httpRequest.getBody() != null && httpRequest.getHeaders() == null) {
            return new HttpEntity<Object>(httpRequest.getBody());
        } else if (httpRequest.getBody() == null && httpRequest.getHeaders() != null) {
            return new HttpEntity<>(httpRequest.getHeaders());
        }
        return null;
    }

    private <T> ResponseEntity<T> doExchange(String provider, String group, HttpRequest httpRequest,
                                             Class<T> responseType) {
        ResponseEntity<T> responseEntity = null;
        if (httpRequest.getAllowedPermits() <= 0) {
            responseEntity = getEntity(httpRequest, responseType);
        } else {
            Semaphore lock = semaphoreCache.get(provider, group, () -> new Semaphore(httpRequest.getAllowedPermits()));
            try {
                lock.acquire();
                responseEntity = getEntity(httpRequest, responseType);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.release();
            }
        }
        if (responseEntity == null) {
            responseEntity = ResponseEntity.internalServerError().build();
        }
        return responseEntity;
    }

    private <T> ResponseEntity<T> getEntity(HttpRequest httpRequest,
                                            Class<T> responseType) {
        String url = httpRequest.getHostUrl() + httpRequest.getPath();
        if (httpRequest.getBody() != null) {
            return restTemplate.exchange(URI.create(url), httpRequest.getMethod(),
                    getHttpEntity(httpRequest), responseType);
        } else if (httpRequest.getUrlVariables() != null) {
            return restTemplate.exchange(url, httpRequest.getMethod(),
                    getHttpEntity(httpRequest), responseType, httpRequest.getUrlVariables());
        } else {
            return restTemplate.exchange(URI.create(url), httpRequest.getMethod(),
                    getHttpEntity(httpRequest), responseType);
        }
    }

    private <T> ResponseEntity<T> doExchange(String provider, String group, HttpRequest httpRequest,
                                             ParameterizedTypeReference<T> typeReference) {
        ResponseEntity<T> responseEntity = null;
        if (httpRequest.getAllowedPermits() <= 0) {
            responseEntity = getEntity(httpRequest, typeReference);
        } else {
            Semaphore lock = semaphoreCache.get(provider, group, () -> new Semaphore(httpRequest.getAllowedPermits()));
            try {
                lock.acquire();
                responseEntity = getEntity(httpRequest, typeReference);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }catch (Exception e) {
				if(e instanceof RestClientException) {
					throw (RestClientException)e;
				}else {
					if(log.isErrorEnabled()) {
						log.error(e.getMessage(),e);
					}
				}
			} finally {
                lock.release();
            }
        }
        if (responseEntity == null) {
            responseEntity = ResponseEntity.internalServerError().build();
        }
        return responseEntity;
    }

    private <T> ResponseEntity<T> getEntity(HttpRequest httpRequest,
                                            ParameterizedTypeReference<T> typeReference) {
        String url = httpRequest.getHostUrl() + httpRequest.getPath();
        if (httpRequest.getBody() != null) {
            return restTemplate.exchange(URI.create(url), httpRequest.getMethod(),
                    getHttpEntity(httpRequest), typeReference);
        } else if (httpRequest.getUrlVariables() != null) {
            return restTemplate.exchange(url, httpRequest.getMethod(),
                    getHttpEntity(httpRequest), typeReference, httpRequest.getUrlVariables());
        } else {
            return restTemplate.exchange(url, httpRequest.getMethod(),
                    getHttpEntity(httpRequest), typeReference);
        }
    }

    private final RetryListenerContainer retryListenerContainer = new RetryListenerContainer();

	@Override
	public void addRetryListener(RetryListener listener) {
		retryListenerContainer.addListener(listener);
	}

	@Override
	public void addApiRetryListener(ApiRetryListener listener) {
		retryListenerContainer.addListener(listener);
	}


}