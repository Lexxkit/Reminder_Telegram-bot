package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.exception.DateTimeFromThePastException;
import pro.sky.telegrambot.exception.TextPatternDoesNotMatchException;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.constants.TelegramBotConstants.DATE_TIME_FORMAT;
import static pro.sky.telegrambot.constants.TelegramBotConstants.REMINDER_TEXT_PATTERN;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final Logger logger = LoggerFactory.getLogger(NotificationTaskServiceImpl.class);
    private final Pattern pattern = Pattern.compile(REMINDER_TEXT_PATTERN);
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    /**
     * Method creates NotificationTask instance from params and saves it to the DB.
     *
     * @param chatId from Telegram message instance
     * @param message notification task that will be saved
     * @return NotificationTask instance that was saved in DB
     * @throws DateTimeParseException
     * @throws TextPatternDoesNotMatchException
     */
    @Override
    public NotificationTask saveTask(Long chatId, String message) throws DateTimeParseException, TextPatternDoesNotMatchException {
        NotificationTask task = notificationTaskRepository.save(createNotificationTaskEntity(chatId, message));
        logger.info("Saved task: {}", task);
        return task;
    }

    /**
     * Method find notification tasks in DB where its notification date equals to dateTimeForSearch and done flag is false.
     * If at least one task was found, then each found task delivered to consumer which sends it to telegram bot user.
     *
     * @param taskConsumer from TelegramBotUpdatesListener, use for send notification to the user.
     */
    @Override
    public void findTasksToRemind(Consumer<NotificationTask> taskConsumer) {
        // Truncate seconds from DateTime object, can be adjusted alongside
        // with cron expression settings at TelegramBotUpdatesListener
        LocalDateTime dateTimeForSearch = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<NotificationTask> tasksToRemindNow = notificationTaskRepository
                .findByNotificationDateEqualsAndDoneIsFalse(dateTimeForSearch);
        if (tasksToRemindNow.size() > 0) {
            logger.info("Found {} tasks to remind. Sending notifications...", tasksToRemindNow.size());
            for (NotificationTask notificationTask : tasksToRemindNow) {
                taskConsumer.accept(notificationTask);
                notificationTask.setDone(true);
            }
            notificationTaskRepository.saveAll(tasksToRemindNow);
        }
        logger.info("All tasks were sent to users.");
    }

    /**
     * Creates notification task from the message and chat id.
     *
     * @param chatId index of a telegram chat.
     * @param message text from a telegram chat message instance.
     * @return NotificationTask instance
     */
    private NotificationTask createNotificationTaskEntity(Long chatId, String message) {
        Pair<String, String> messagePieces = parseMessage(message);
        LocalDateTime dateTime = convertToDateTime(messagePieces.getFirst());
        // Check that date is in the future. If NOT - throw Exception.
        if (!dateTime.isAfter(LocalDateTime.now())){
            throw new DateTimeFromThePastException("DateTime is less or equals now: " + dateTime);
        }
        NotificationTask task = new NotificationTask(messagePieces.getSecond(), dateTime);
        task.setChatId(chatId);
        return task;
    }

    /**
     * Converts string with DATE_TIME_FORMAT pattern to LocalDateTime object.
     *
     * @param date The string to convert to a LocalDateTime object
     * @return LocalDateTime instance
     * @throws DateTimeParseException If param doesn't match DATE_TIME_FORMAT pattern
     */
    private LocalDateTime convertToDateTime(String date) throws DateTimeParseException {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    /**
     * Check if string matches the pattern and return list of it parts.
     * If string doesn't match throws exception.
     *
     * @param message string to parse
     * @return pair of strings: first - datetime, second - message text from the initial string
     */
    private Pair<String, String> parseMessage(String message) {
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String reminderText = matcher.group(3);
            return Pair.of(date, reminderText);
        } else {
            throw new TextPatternDoesNotMatchException();
        }
    }
}
