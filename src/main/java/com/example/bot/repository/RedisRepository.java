package com.example.bot.repository;

import com.example.bot.service.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by lineplus on 2017. 3. 15..
 */
@Repository
public class RedisRepository {

	@Autowired
	private RedisManager redisManager;

	public StringBuilder registLunchbot(String senderId) {

		Jedis jedis = redisManager.getJedis();
		StringBuilder sb = new StringBuilder();
		Long result = jedis.sadd("bot", senderId);
		redisManager.closeJedis(jedis);
		return result == 1 ? sb.append("Source를 등록하였습니다 : " + senderId) : sb.append("이미 등록되었습니다.");
	}

	public StringBuilder removeLunchBot(String senderId) {

		StringBuilder sb = new StringBuilder();
		Jedis jedis = redisManager.getJedis();
		Long result = jedis.srem("bot", senderId);
		redisManager.closeJedis(jedis);
		return result == 1 ? sb.append("Source를 해제하였습니다: " + senderId) : sb.append("등록도 안됐는데요");
	}

	public StringBuilder getLunchBotList() {

		Jedis jedis = redisManager.getJedis();
		StringBuilder sb = new StringBuilder();
		sb.append("목록: \n");
		Set<String> sources = jedis.smembers("bot");
		redisManager.closeJedis(jedis);
		for (String source : sources) {
			sb.append(source + '\n');
		}
		return sb;
	}

	public StringBuilder getRandomYOK() {

		StringBuilder sb = new StringBuilder();
		Jedis jedis = redisManager.getJedis();
		sb.append(jedis.srandmember("yok"));
		redisManager.closeJedis(jedis);
		return sb;
	}

	public StringBuilder appendYOK(String yok) {

		StringBuilder sb = new StringBuilder();
		Jedis jedis = redisManager.getJedis();
		Long result = jedis.sadd("yok", yok);
		redisManager.closeJedis(jedis);
		return result == 1 ? sb.append("런치봇이 욕을 장착하였습니다: " + yok) : sb.append("욕 같은 거 안해!");
	}

	public StringBuilder getYOKList() {

		StringBuilder sb = new StringBuilder();
		Jedis jedis = redisManager.getJedis();
		Set<String> yokSet = jedis.smembers("yok");
		redisManager.closeJedis(jedis);
		for (String yok : yokSet) {
			sb.append(yok + '\n');
		}
		return sb;
	}

}
