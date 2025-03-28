package com.zagvladimir.service;

import com.zagvladimir.model.Group;
import com.zagvladimir.model.Schedule;
import com.zagvladimir.model.Subscription;
import com.zagvladimir.repository.GroupRepository;
import com.zagvladimir.repository.ScheduleRepository;
import com.zagvladimir.repository.SubscriptionRepository;
import com.zagvladimir.util.MessageTextGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zagvladimir.util.MessageConstants.*;
@Service
@RequiredArgsConstructor
public class ProduceService {

    private static final Pattern COMMAND_PATTERN = Pattern.compile("/(\\w+)\\s*(.*)");

    private final SubscriptionRepository subscriptionRepository;
    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessageTextGenerator textGenerator;

    private final Map<String, Consumer<CommandContext>> commandHandlers = Map.of(
            "get_groups", ctx -> getListOfGroups(ctx.chatId()),
            "get_schedule", ctx -> getSchedule(ctx.chatId()),
            "set_group", ctx -> handleGroupSubscription(ctx.chatId(), ctx.arguments())
    );


    public void processMessage(SendMessage message) {
        final String text = message.getText();
        final Long chatId = Long.valueOf(message.getChatId());

        Matcher matcher = COMMAND_PATTERN.matcher(text);
        if (!matcher.find()) {
            sendResponse(INVALID_COMMAND_MESSAGE, chatId);
            return;
        }

        String command = matcher.group(1).toLowerCase();
        String arguments = matcher.group(2).trim();

        commandHandlers.getOrDefault(command, ctx -> sendResponse(UNKNOWN_COMMAND_MESSAGE, ctx.chatId()))
                .accept(new CommandContext(chatId, arguments));
    }

    public void getSchedule(Long chatId) {
        subscriptionRepository.findByTelegramChatId(chatId)
                .ifPresentOrElse(
                        subscription -> sendScheduleForGroup(chatId, subscription.getGroup()),
                        () -> sendResponse(FAILED_SCHEDULE_FETCH, chatId)
                );
    }

    public void handleGroupSubscription(Long chatId, String groupName) {
        groupRepository.findGroupByName(groupName)
                .ifPresentOrElse(
                        group -> subscriptionRepository.findByTelegramChatId(chatId)
                                .ifPresentOrElse(
                                        sub -> updateExistingSubscription(sub, group),
                                        () -> createNewSubscription(chatId, group)
                                ),
                        () -> sendResponse(String.format(GROUP_NOT_FOUND_TEMPLATE, FAILED_SUBSCRIPTION), chatId)
                );
    }

    public void getListOfGroups(Long chatId) {
        String groups = groupRepository.findAll().stream()
                .map(Group::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse(NO_GROUPS_AVAILABLE);

        sendResponse(groups, chatId);
    }

    private void sendScheduleForGroup(Long chatId, Group group) {
        List<Schedule> schedules = scheduleRepository.findByGroupAndDate(group, LocalDate.now());
        String text = schedules.isEmpty()
                ? NO_SCHEDULE_FOR_TODAY
                : textGenerator.createTextForMessage(schedules);

        sendResponse(text, chatId);
    }


    private void updateExistingSubscription(Subscription subscription, Group group) {
        if (!subscription.getGroup().equals(group)) {
            subscription.setGroup(group);
            subscriptionRepository.save(subscription);
        }
        sendResponse(SUCCESS_SUBSCRIPTION, subscription.getTelegramChatId());
    }

    private void createNewSubscription(Long chatId, Group group) {
        Subscription subscription = Subscription.builder()
                .withTelegramChatId(chatId)
                .withGroup(group)
                .build();
        subscriptionRepository.save(subscription);
        sendResponse(SUCCESS_SUBSCRIPTION, chatId);
    }

    private void sendResponse(String text, Long chatId) {
        sendResponse(new SendMessage(chatId.toString(), text));
    }

    private void sendResponse(SendMessage message) {
        rabbitTemplate.convertAndSend(ANSWER_TEXT_QUEUE, message);
    }

    private record CommandContext(Long chatId, String arguments) {
    }
}
