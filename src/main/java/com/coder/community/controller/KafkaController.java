package com.coder.community.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaController {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private KafkaConsumer kafkaConsumer;
    public void testKafka() {
        kafkaProducer.sendMessage("mingyue", "你好");
        kafkaProducer.sendMessage("mingyue", "在吗");
        kafkaProducer.sendMessage("mingyue", "在吗?");
        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}
@Component
class KafkaConsumer {
    @KafkaListener(topics = {"mingyue"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}
