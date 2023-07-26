### Reminder_Telegram-bot
Telegram bot that receives scheduled tasks from users and reminds about them at desired date and time.

#### Stack
```
Java 11
Spring Boot 2
[Java Telegram Bot API](https://github.com/pengrad/java-telegram-bot-api)
Maven
Liquibase
PostreSQL
```

#### Functionality
User can send messages for the Reminder Bot with desired date, time and text to remind.
If Bot couldn't parse the message correctly it will respond with proper format example and asks to change the initial message.
The bot will send a reminder when the event occurs.
