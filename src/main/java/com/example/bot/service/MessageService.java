package com.example.bot.service;

import com.example.bot.constant.Birthday;
import com.example.bot.repository.RedisRepository;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * Created by lineplus on 2017. 3. 15..
 */

@Service
public class MessageService {

	@Autowired
	private JedisPool jedisPool;

	@Value("${channel.token}")
	private String channelToken;

	@Autowired
	private RedisRepository redisRepository;

	public TextMessage actionMessage(MessageEvent<TextMessageContent> messageEvent) {

		StringBuilder sb = new StringBuilder();
		String action = messageEvent.getMessage().getText();
		String senderId = messageEvent.getSource().getSenderId();
		if (action.contains("한테") && action.contains("#욕해줘")) {
			String target = tokenizeTarget(action.substring(0, action.indexOf("한테")));
			sb.append(target + " ");
			sb.append(redisRepository.getRandomYOK());

		} else if (action.contentEquals("#점심봇등록")) {

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

	private Set getJedisMember(){
		Jedis jedis = jedisPool.getResource();
		Set<String> sources = jedis.smembers("bot");

		if(sources.isEmpty()) {
			return Collections.emptySet();
		}
		return sources;
	}

	public void lunchScheduler(){

		sendMessage("오늘은 다함께 점심 먹는 날입니다. 12시 30분에 보아요. 빠셍!");

	}

	public void birthdayScheduler(){

		DateTime date = new DateTime().withMillis(System.currentTimeMillis());
		String today =  date.toString(DateTimeFormat.forPattern("MMdd"));

		for(Birthday birthday :Birthday.values()){

			if(today.equals(birthday.getDay())) {
				sendMessage(birthday.name()+"생일축하!!!!!!!!!");
			}
		}
	}

	private void sendMessage(String message){

		Set<String> sources = getJedisMember();
		for(String source : sources) {
			TextMessage textMessage = new TextMessage(message);
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

	private String tokenizeTarget(String target) {

		String[] splitResult = target.split(" ");
		String lastString = splitResult[splitResult.length - 1];
		if (lastString.equals("나")) {
			return "너";
		} else {
			return lastString;
		}
	}
}
