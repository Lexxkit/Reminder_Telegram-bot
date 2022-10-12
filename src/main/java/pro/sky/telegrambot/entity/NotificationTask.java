package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String notificationMessage;
    private LocalDateTime notificationDate;
    @Column(name = "is_done")
    private boolean done;

    public NotificationTask() {
    }

    public NotificationTask(String notificationMessage, LocalDateTime notificationDate) {
        this.notificationMessage = notificationMessage;
        this.notificationDate = notificationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return done == that.done && Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(notificationMessage, that.notificationMessage) && Objects.equals(notificationDate, that.notificationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, notificationMessage, notificationDate, done);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", notificationMessage='" + notificationMessage + '\'' +
                ", notificationDate=" + notificationDate +
                ", isDone=" + done +
                '}';
    }
}
