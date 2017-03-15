package com.example.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by lineplus on 2017. 3. 15..
 */
@Repository
public class RedisManager {
	@Autowired
	private JedisPool jedisPool;

	public Jedis getJedis() {
		return jedisPool.getResource();
	}

	public void closeJedis(Jedis jedis) {
		jedis.close();
	}

}
