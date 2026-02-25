package net.happykoo.hcp.adapter.out.persistence.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.infrastructure.properties.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Cacheable 사용 위함
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

  private final RedisProperties redisProperties;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
  }

  @Bean
  @Primary
  public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultCacheConfiguration())
        .build();
  }

  @Bean
  public RedisCacheManager redisTtl10mCacheManager(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(ttl10mCacheConfiguration())
        .build();
  }

  private RedisCacheConfiguration defaultCacheConfiguration() {
    return jsonCacheConfiguration(Duration.ofHours(1L));
  }

  private RedisCacheConfiguration ttl10mCacheConfiguration() {
    return jsonCacheConfiguration(Duration.ofMinutes(10L));
  }

  //JSON 으로 serialize 하는 경우(디폴트는 JDK serialize) -> 클래스에 Serialize를 구현 안해도 됨
  private RedisCacheConfiguration jsonCacheConfiguration(Duration ttl) {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    return RedisCacheConfiguration
        .defaultCacheConfig()
        .disableCachingNullValues()
        .entryTtl(ttl)
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(objectMapper))
        );
  }
}
