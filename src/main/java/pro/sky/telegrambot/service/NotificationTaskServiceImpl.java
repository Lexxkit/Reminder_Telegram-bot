package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final Logger logger = LoggerFactory.getLogger(NotificationTaskServiceImpl.class);

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public NotificationTask saveTask(Message message) {
        // TODO: 11.10.2022 extract creation of entity to private method
        List<String> messagePieces = parseMessage(message);
        LocalDateTime dateTime = convertToDateTime(messagePieces.get(0));
        NotificationTask task = new NotificationTask(messagePieces.get(1), dateTime);
        task.setChatId(message.chat().id());
        notificationTaskRepository.save(task);
        logger.info("Saved notification: {}", task);
        return task;
    }

    private LocalDateTime convertToDateTime(String date) {
        // TODO: 11.10.2022 add try-catch for DateTimeParseException or method throws
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    private List<String> parseMessage(Message message) {
        List<String> messagePieces = Collections.emptyList();
        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(message.text());
        if (matcher.matches()) {
            String date = matcher.group(1);
            String reminderText = matcher.group(3);
            messagePieces = List.of(date, reminderText);
        }
        // TODO: 11.10.2022 If there is no matches - throw new exception???
        return messagePieces;
    }
}
