package com.coder.community.dao;

import com.coder.community.NowcoderApplication;
import com.coder.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    void insertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("123");
        loginTicket.setStatus(0);
        loginTicket.setUserId(101);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);

    }

    @Test
    void selectByTicket() {

        LoginTicket loginTicket = loginTicketMapper.selectByTicket("123");
        System.out.println(loginTicket.getId());

    }

    @Test
    void updateStatus() {

        loginTicketMapper.updateStatus("123",1);
    }
}