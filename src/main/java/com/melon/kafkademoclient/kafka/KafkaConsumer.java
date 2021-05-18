package com.melon.kafkademoclient.kafka;

import com.melon.kafkademoclient.ansible.AnsibleService;
import com.melon.kafkademoclient.util.JsonHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KafkaConsumer {

    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final AnsibleService ansibleService;

    @Autowired
    public KafkaConsumer(AnsibleService ansibleService) {
        this.ansibleService = ansibleService;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.application.name}")
    public void onMessage(ConsumerRecord<?, ?> record) {
        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object o = message.get();
            LOGGER.info("Success Receives the message : [{}]", o);
            AppDto appDto = JsonHelper.string2Obj(String.valueOf(o), AppDto.class);
            ansibleService.upgradeApplication(appDto);
        }
    }
}
