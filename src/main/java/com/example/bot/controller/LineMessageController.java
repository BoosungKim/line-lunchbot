package com.example.bot.controller;

import com.example.bot.constant.Birthday;
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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
    private MessageService messageService;


    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {

        return messageService.actionMessage(event);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
    }

    @Scheduled(cron = "0 0 10 * * WED", zone = "ROK")
    public void scheduledEvent() {
        messageService.lunchScheduler();
    }

    @Scheduled(cron = "0 0 12 * * *", zone = "ROK")
    public void birthdayScheduler() {
        messageService.birthdayScheduler();
    }

}
