package com.zagvladimir.service;

import com.zagvladimir.model.Schedule;
import com.zagvladimir.model.Subscription;
import com.zagvladimir.repository.ScheduleRepository;
import com.zagvladimir.repository.SubscriptionRepository;
import com.zagvladimir.util.MessageTextGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.List;

import static com.zagvladimir.util.MessageConstants.ANSWER_TEXT_QUEUE;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ScheduleService {

    private final RabbitTemplate rabbitTemplate;
    private final SubscriptionRepository subscriptionRepository;
    private final ScheduleRepository scheduleRepository;
    private final MessageTextGenerator textGenerator;

    @Scheduled(cron = "${schedule.delivery-time}")
    public void sendDailySchedule() {
        subscriptionRepository.findAll().forEach(this::processSubscription);
    }

    private void processSubscription(Subscription subscription) {
        LocalDate targetDate = LocalDate.now();
        List<Schedule> schedules = scheduleRepository.findByGroupAndDate(
                subscription.getGroup(),
                targetDate
        );

        if (schedules.isEmpty()) return;

        String text = textGenerator.createDailyScheduleText(schedules, targetDate);
        SendMessage message = new SendMessage(
                subscription.getTelegramChatId().toString(),
                text
        );
        rabbitTemplate.convertAndSend(ANSWER_TEXT_QUEUE, message);
    }
}