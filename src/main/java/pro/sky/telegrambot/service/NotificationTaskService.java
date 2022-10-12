package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Message;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public interface NotificationTaskService {
    NotificationTask saveTask(Long chatId, String message);

    void findTasksToRemind(Consumer<NotificationTask> consumer);
}
