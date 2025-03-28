package com.zagvladimir.repository;

import com.zagvladimir.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findByTelegramChatId(Long chatId);
}