package io.doodler.redis;

import java.util.List;
import java.util.stream.Collectors;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import io.doodler.common.utils.JacksonUtils;

/**
 * @Description: RedissonConfig
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@AutoConfigureAfter(RedisConfig.class)
@Configuration(proxyBeanMethods = false)
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(RedisConnectionFactory redisConnectionFactory) {
        Config config = new Config();
        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) redisConnectionFactory;
        if (connectionFactory.isClusterAware()) {
            RedisClusterConfiguration configuration = connectionFactory.getClusterConfiguration();
            String[] serverLocations = configuration.getClusterNodes().stream()
                    .map(e -> "redis://" + e.getHost() + ":" + e.getPort()).toArray(String[]::new);
            ClusterServersConfig clusterConfig = config.useClusterServers().addNodeAddress(serverLocations);
            if (configuration.getPassword().isPresent()) {
                clusterConfig.setPassword(new String(configuration.getPassword().get()));
            }
        } else if (connectionFactory.isRedisSentinelAware()) {
            RedisSentinelConfiguration configuration = connectionFactory.getSentinelConfiguration();
            List<String> serverLocations = configuration.getSentinels().stream()
                    .map(e -> "redis://" + e.getHost() + ":" + e.getPort()).collect(Collectors.toList());
            SentinelServersConfig clusterConfig = config.useSentinelServers();
            clusterConfig.setSentinelAddresses(serverLocations);
            clusterConfig.setMasterName(configuration.getMaster().getName());
            clusterConfig.setDatabase(configuration.getDatabase());
            if (configuration.getPassword().isPresent()) {
                clusterConfig.setPassword(new String(configuration.getPassword().get()));
            }
        } else {
            RedisStandaloneConfiguration configuration = connectionFactory.getStandaloneConfiguration();
            SingleServerConfig singleConfig = config.useSingleServer()
                    .setAddress("redis://" + configuration.getHostName() + ":" + configuration.getPort());
            singleConfig.setDatabase(configuration.getDatabase());
            if (configuration.getPassword().isPresent()) {
                singleConfig.setPassword(new String(configuration.getPassword().get()));
            }
        }
        config.setCodec(new JsonJacksonCodec(JacksonUtils.getGenericObjectMapper()));
        return Redisson.create(config);
    }
}