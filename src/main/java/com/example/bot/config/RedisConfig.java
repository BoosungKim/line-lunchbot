package com.example.bot.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by busungkim on 2017. 2. 1..
 */

@Configuration
public class RedisConfig {

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(1);

        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedisPool() throws URISyntaxException {
        return new JedisPool(jedisPoolConfig(), new URI(System.getenv("REDIS_URL")));
    }
}
