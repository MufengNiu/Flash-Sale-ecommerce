package com.ecommerce.config;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;


@Component
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600) // 1 hour lifetime
public class RedisConfiguration {
}
