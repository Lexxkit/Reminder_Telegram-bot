package pro.sky.telegrambot.constants;

public class TelegramBotConstants {
    public static final String INITIAL_MSG = "/start";
    public static final String PROBLEM_OCCURS_MSG = "Please use this pattern 'dd.mm.yyyy hh:mm Text to remind' " +
            "to save your task.";
    public static final String USE_DATE_IN_FUTURE = "You tried to set a reminder for the past. " +
            "Please change date/time to something in the future!";
    public static final String GREETING_MSG = "Greetings from ReminderBot!\n" +
            "I will remind you about everything you ask me.\n" + PROBLEM_OCCURS_MSG;
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    public static final String REMINDER_TEXT_PATTERN = "([0-9\\.\\:\\s]{16})(\\s)(.+)";
}
