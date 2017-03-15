package com.example.bot.service;

import com.example.bot.repository.RedisRepository;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lineplus on 2017. 3. 15..
 */

@Service
public class MessageService {

	@Autowired
	private RedisRepository redisRepository;

	public TextMessage actionMessage(MessageEvent<TextMessageContent> messageEvent) {

		StringBuilder sb = new StringBuilder();
		String action = messageEvent.getMessage().getText();
		String senderId = messageEvent.getSource().getSenderId();

		if (action.contentEquals("#점심봇등록")) {

			sb.append(redisRepository.registLunchbot(senderId));

		} else if (action.contentEquals("#점심봇해제")) {

			sb.append(redisRepository.removeLunchBot(senderId));

		} else if (action.contentEquals("#점심봇목록")) {

			sb.append(redisRepository.getLunchBotList());

		} else if (action.contentEquals("#그렇지?")) {

			sb.append("응!");

		} else if (action.contains("#욕해줘")) {

			sb.append(redisRepository.getRandomYOK());

		} else if (action.startsWith("#욕등록 ")) {

			sb.append(redisRepository.appendYOK(action.substring(5)));

		} else if (action.contentEquals("#욕목록")) {

			sb.append(redisRepository.getYOKList());
		}

		return new TextMessage(sb.toString());
	}

}
