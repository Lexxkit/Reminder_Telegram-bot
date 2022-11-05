package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.exception.DateTimeFromThePastException;
import pro.sky.telegrambot.exception.TextPatternDoesNotMatchException;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeParseException;
import java.util.List;

import static pro.sky.telegrambot.constants.TelegramBotConstants.*;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();
            // If the server connection was lost, then message object can be null
            // So we ignore it in this case
            if (message == null) {
                return;
            }

            Long chatId = message.chat().id();

            // Search for the INITIAL_MSG
            if (INITIAL_MSG.equals(message.text())) {
                // Send GREETINGS_MSG if INITIAL_MSG was found
                logger.info("Bot initial message received: {}", message.text());
                sendMessage(chatId, GREETING_MSG);
            } else {
                try {
                    // Save task to DB if no problems found and send success message to user
                    NotificationTask notificationTask = notificationTaskService.saveTask(chatId, message.text());
                    sendMessage(chatId, createSuccessMsg(notificationTask));
                } catch (TextPatternDoesNotMatchException | DateTimeParseException e) {
                    // In case of wrong user input don't save to DB and send message to user
                    logger.warn("User input doesn't match the pattern: {}", message.text());
                    sendMessage(chatId, PROBLEM_OCCURS_MSG);
                } catch (DateTimeFromThePastException e) {
                    logger.warn("User tries to set a remind in the past. {}", e.getMessage());
                    sendMessage(chatId, USE_DATE_IN_FUTURE);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * Calls the NotificationTaskService method and send notification to the telegram users
     * according to the cron expression at @Scheduled annotation.
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void remindAboutScheduledTasks() {
        notificationTaskService.findTasksToRemind(this::sendRemainderMessage);
    }

    /**
     * Creates string with proper text from NotificationTask instance.
     *
     * @param notificationTask instance of NotificationTask class which information to be sent
     * @return string of proper format with info from param
     */
    private String createSuccessMsg(NotificationTask notificationTask) {
        return String.format("OK, I will remind you about '%s' on %s",
                notificationTask.getNotificationMessage(),
                notificationTask.getNotificationDate());
    }

    /**
     * Creates SendMessage instance for telegram chat with some text
     * and sends it to the chat.
     *
     * @param chatId index of a telegram chat to which the message is sent
     * @param textToSend string to be sent
     */
    private void sendMessage(Long chatId, String textToSend) {
        // Create message to send and send it to chat defined by id
        SendMessage sendMessage = new SendMessage(chatId, textToSend);
        SendResponse response = telegramBot.execute(sendMessage);

        // Check if msg was not sent and log the error
        if (!response.isOk()) {
            logger.warn("Message was not sent, error code: {}", response.errorCode());
        }
    }

    /**
     * Forms a string from NotificationTask instance and send it to the telegram chat.
     *
     * @param notificationTask instance of NotificationTask class which information to be sent
     */
    private void sendRemainderMessage(NotificationTask notificationTask) {
        String reminderText = String.format("You've asked to remind you about '%s' on %s.",
                notificationTask.getNotificationMessage(), notificationTask.getNotificationDate());
        sendMessage(notificationTask.getChatId(), reminderText);
    }

}
