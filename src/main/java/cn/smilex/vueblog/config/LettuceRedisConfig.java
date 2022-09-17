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
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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

    @Value("${spring.redis.url}")
    private String url;

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
        configuration.setHostName(url);
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
        var redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setDefaultSerializer(RedisSerializer.json());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
