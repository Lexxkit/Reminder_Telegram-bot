package pro.sky.telegrambot.service;

import pro.sky.telegrambot.entity.NotificationTask;

import java.util.function.Consumer;

public interface NotificationTaskService {
    NotificationTask saveTask(Long chatId, String message);

    void findTasksToRemind(Consumer<NotificationTask> consumer);
}
