/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.response.BotApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@LineMessageHandler
@EnableScheduling
public class EchoApplication {

    private Set<Source> sources = Sets.newHashSet();

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        System.out.println("event: " + event);

        if(event.getMessage().getText().startsWith("@")) {
            return new TextMessage(event.getMessage().getText());
        } else if(event.getMessage().getText().contentEquals("#등록")) {
            sources.add(event.getSource());
            return new TextMessage("Source를 등록하였습니다: " + event.getSource());
        } else if(event.getMessage().getText().contentEquals("#해제")) {
            sources.remove(event.getSource());
            return new TextMessage("Source를 해제하였습니다: " + event.getSource());
        } else if(event.getMessage().getText().contentEquals("#목록")) {
            StringBuilder sb = new StringBuilder("목록: \n");

            for(Source source : sources) {
                sb.append(source).append('\n');
            }
            return new TextMessage(sb.toString());

        } else {
            return null;
        }
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }

    @Value("${channel.token}")
    private String channelToken;
    
    @Scheduled(cron = "0 */1 15 * * *", zone = "ROK")
    public void scheduledEvent() {
        if(sources.isEmpty()) {
            return;
        }

        for(Source source : sources) {
            TextMessage textMessage = new TextMessage("Test message: 점심 봇입니다. 빠셍!");
            PushMessage pushMessage = new PushMessage(source.getSenderId(), textMessage);

            try {
                Response<BotApiResponse> response =
                        LineMessagingServiceBuilder
                                .create(channelToken)
                                .build()
                                .pushMessage(pushMessage)
                                .execute();

                System.out.println("Send push to " + source);
            } catch(IOException e) {
                System.out.println(e.toString());
            }
        }
    }
}