// ConsumerService.java
package com.zagvladimir.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Service
@AllArgsConstructor
public class ConsumerService {
    public static final String GROUPS_QUEUE = "GROUPS_QUEUE";

    private final ProduceService produceService;

    @RabbitListener(queues = GROUPS_QUEUE)
    public void handleExchangeUpdate(SendMessage message) {
        try {
            log.info("Received message for chat: {}", message.getChatId());
            produceService.processMessage(message);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            handleFailedMessage(message, e);
        }
    }

    private void handleFailedMessage(SendMessage message, Exception e) {
        SendMessage errorMessage = new SendMessage();
        errorMessage.setChatId(message.getChatId());
        errorMessage.setText("Произошла ошибка при обработке запроса");
        produceService.processMessage(errorMessage);
    }
}