package com.example.bot.controller;

import com.example.bot.service.MessageService;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import retrofit2.Response;

import java.io.IOException;
import java.util.Set;

/**
 * Created by busungkim on 2017. 2. 7..
 */

@LineMessageHandler
@EnableScheduling
public class LineMessageController {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private MessageService messageService;
    @Value("${channel.token}")
    private String channelToken;

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {

        return messageService.actionMessage(event);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
    }

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
