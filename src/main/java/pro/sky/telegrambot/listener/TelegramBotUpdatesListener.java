package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
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

            // Search for the INITIAL_MSG
            if (message.text().equals("/start")) {
                // Send GREETINGS_MSG if INITIAL_MSG was found
                logger.info("Bot initial message received: {}", message.text());

                // Create message to send and send it to chat defined by id
                SendMessage sendMessage = new SendMessage(message.chat().id(), "Greetings from ReminderBot!");
                telegramBot.execute(sendMessage);
            } else {
                // Create message to send and send it to chat defined by id
                SendMessage sendMessage = new SendMessage(message.chat().id(), "There's nothing more I can do for now \uD83D\uDE14");
                telegramBot.execute(sendMessage);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
