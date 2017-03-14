package com.example.bot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import retrofit2.Response;

/**
 * Created by busungkim on 2017. 2. 7..
 */

@LineMessageHandler
@EnableScheduling
public class LineMessageController {

    @Autowired
    private JedisPool jedisPool;

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        // 부성: 여기는 리펙토링이 매우 필요합니다.

        String message = event.getMessage().getText();
        StringBuilder sb = new StringBuilder();

        if(message.contentEquals("#점심봇등록")) {
            String senderId = event.getSource().getSenderId();

            Jedis jedis = jedisPool.getResource();
            Long res = jedis.sadd("bot", senderId);

            if(res == 1) {
                sb.append("Source를 등록하였습니다: " + senderId);
            } else {
                sb.append("이미 등록됐는데요");
            }

        } else if(message.contentEquals("#점심봇해제")) {
            String senderId = event.getSource().getSenderId();

            Jedis jedis = jedisPool.getResource();
            Long res = jedis.srem("bot", senderId);

            if(res == 1) {
                sb.append("Source를 해제하였습니다: " + senderId);
            } else {
                sb.append("등록도 안됐는데요");
            }

        } else if(message.contentEquals("#점심봇목록")) {
            sb.append("목록: \n");

            Jedis jedis = jedisPool.getResource();
            Set<String> sources = jedis.smembers("bot");
            for(String source : sources) {
                sb.append(source + '\n');
            }

        } else if(message.contentEquals("#그렇지?")) {
            sb.append("응!");
        } else if(message.contentEquals("#욕해줘")) {
            Jedis jedis = jedisPool.getResource();
            Set<String> yokSet = jedis.smembers("yok");

            List<String> yokList = new ArrayList(yokSet);
            Collections.shuffle(yokList);

            sb.append(yokList.get(0));

        } else if(message.startsWith("#욕등록 ")) {
            String yok = message.substring(5);

            Jedis jedis = jedisPool.getResource();
            Long res = jedis.sadd("yok", yok);

            if(res == 1) {
                sb.append("런치봇이 욕을 장착하였습니다: " + yok);
            } else {
                sb.append("욕 같은 거 안해!");
            }
        }

        return new TextMessage(sb.toString());
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
    }

    @Value("${channel.token}")
    private String channelToken;

    @Scheduled(cron = "0 0 10 * * WED", zone = "ROK")
    public void scheduledEvent() {
        Jedis jedis = jedisPool.getResource();
        Set<String> sources = jedis.smembers("bot");

        if(sources.isEmpty()) {
            return;
        }

        for(String source : sources) {
            TextMessage textMessage = new TextMessage("오늘은 다함께 점심 먹는 날입니다. 12시 30분에 보아요. 빠셍!");
            PushMessage pushMessage = new PushMessage(source, textMessage);

            try {
                Response<BotApiResponse> response =
                        LineMessagingServiceBuilder
                                .create(channelToken)
                                .build()
                                .pushMessage(pushMessage)
                                .execute();

                System.out.println(String.format("Send push to %s, code: %s", source, response.code()));
            } catch(IOException e) {
                System.out.println(e);
            }
        }
    }

}
