package com.coder.community.controller;

import com.coder.community.NowcoderApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
class KafkaControllerTest {
    @Autowired
    private KafkaController kafkaController;

    @Test
    void testKafka() {
        kafkaController.testKafka();
    }
}