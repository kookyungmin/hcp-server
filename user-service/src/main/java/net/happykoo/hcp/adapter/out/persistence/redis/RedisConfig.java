package net.happykoo.hcp.adapter.out.persistence.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.result.GetLoginUserInfo;
import net.happykoo.hcp.infrastructure.properties.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Cacheable 사용 위함
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

  private final RedisProperties redisProperties;
  private final ObjectMapper objectMapper;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
  }

  @Bean
  public RedisCacheManager userProfileCacheManager(RedisConnectionFactory redisConnectionFactory) {
    ObjectMapper om = objectMapper.copy();

    Jackson2JsonRedisSerializer<GetLoginUserInfo> valueSer =
        new Jackson2JsonRedisSerializer<>(om, GetLoginUserInfo.class);

    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(jsonCacheConfiguration(
            Duration.ofHours(3L),
            RedisSerializationContext.SerializationPair.fromSerializer(valueSer)
        ))
        .build();
  }


  //JSON 으로 serialize 하는 경우(디폴트는 JDK serialize) -> 클래스에 Serialize를 구현 안해도 됨
  private RedisCacheConfiguration jsonCacheConfiguration(
      Duration ttl,
      RedisSerializationContext.SerializationPair<?> serializer
  ) {

    return RedisCacheConfiguration
        .defaultCacheConfig()
        .disableCachingNullValues()
        .entryTtl(ttl)
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(serializer);
  }
}
