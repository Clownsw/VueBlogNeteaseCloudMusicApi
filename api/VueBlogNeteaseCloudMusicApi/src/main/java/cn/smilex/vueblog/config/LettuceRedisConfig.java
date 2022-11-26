package cn.smilex.vueblog.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author smilex
 * @date 2022/9/17/17:31
 * @since 1.0
 */
@Configuration
public class LettuceRedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private Integer port;

    @Value("${spring.redis.database}")
    private Integer database;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.lettuce.pool.max-idle}")
    private Integer maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private Integer minIdle;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private Integer maxActive;

    @Value("${spring.redis.lettuce.pool.max-wait}")
    private Long maxWait;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        GenericObjectPoolConfig<Object> objectGenericObjectPoolConfig = new GenericObjectPoolConfig<>();
        objectGenericObjectPoolConfig.setMaxIdle(maxIdle);
        objectGenericObjectPoolConfig.setMinIdle(minIdle);
        objectGenericObjectPoolConfig.setMaxTotal(maxActive);
        objectGenericObjectPoolConfig.setMaxWait(Duration.ofMillis(maxWait));
        objectGenericObjectPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(100));

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(database);
        configuration.setPassword(RedisPassword.of(password));
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(objectGenericObjectPoolConfig)
                .build();

        return new LettuceConnectionFactory(configuration, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(RedisSerializer.json());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
