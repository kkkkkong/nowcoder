package com.coder.community.dao;

import com.coder.community.NowcoderApplication;
import com.coder.community.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
class MessageMapperTest {
    @Autowired
    private MessageMapper messageMapper;
    @Test
    void selectConversations() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
    }

    @Test
    void selectConversationCount() {
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
    }

    @Test
    void selectLetters() {
        messageMapper.selectLetters("111_112", 0, 10).forEach(System.out::println);
    }

    @Test
    void selectLetterCount() {
        int i = messageMapper.selectLetterCount("111_112");
        System.out.println(i);
    }

    @Test
    void selectLetterUnreadCount() {
        int i = messageMapper.selectLetterUnreadCount(131, null);
        System.out.println(i);

    }

    @Test
    void insertMessage() {
        Message message = new Message();
        message.setFromId(111);
        message.setToId(112);
        message.setContent("你好");

        int i = messageMapper.insertMessage(message);
        System.out.println(i);
    }
}