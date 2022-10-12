package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.constants.TelegramBotMsgConstants;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.constants.TelegramBotMsgConstants.*;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final Logger logger = LoggerFactory.getLogger(NotificationTaskServiceImpl.class);
    // TODO: 12.10.2022 Regexp only for Cyrillic language. How to use any?
    private final Pattern pattern = Pattern.compile(REMINDER_TEXT_PATTERN);

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public NotificationTask saveTask(Long chatId, String message) throws DateTimeParseException, NullPointerException {
        NotificationTask task = notificationTaskRepository.save(createNotificationTaskEntity(chatId, message));
        logger.info("Saved notification: {}", task);
        return task;
    }

    @Override
    public void findTasksToRemind(Consumer<NotificationTask> taskConsumer) {
        LocalDateTime dateTimeForSearch = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasksToRemindNow = notificationTaskRepository.findByNotificationDateEqualsAndDoneIsFalse(dateTimeForSearch);
        for (NotificationTask notificationTask : tasksToRemindNow) {
            taskConsumer.accept(notificationTask);
            notificationTask.setDone(true);
        }
        notificationTaskRepository.saveAll(tasksToRemindNow);
    }

    private NotificationTask createNotificationTaskEntity(Long chatId, String message) {
        List<String> messagePieces = parseMessage(message).orElseThrow();
        LocalDateTime dateTime = convertToDateTime(messagePieces.get(0));
        NotificationTask task = new NotificationTask(messagePieces.get(1), dateTime);
        task.setChatId(chatId);
        return task;
    }

    private LocalDateTime convertToDateTime(String date) throws DateTimeParseException {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    private Optional<List<String>> parseMessage(String message) {
        List<String> messagePieces = null;
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String reminderText = matcher.group(3);
            messagePieces = List.of(date, reminderText);
        }

        return Optional.of(messagePieces);
    }
}
