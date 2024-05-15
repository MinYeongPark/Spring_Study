package com.example.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageListenService implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 채널 정보와 body 정보 출력
        log.info("Received {} channel: {}", new String(message.getChannel()), new String(message.getBody()));
    }
}
