package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Message;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationTaskServiceImplTest {

    @Mock
    private NotificationTaskRepository taskRepository;

    @InjectMocks
    private NotificationTaskServiceImpl out;

    @Test
    void shouldCreateTaskEntityAndReturnIt_whenSaveTask() {
        when(taskRepository.save(any(NotificationTask.class))).thenReturn(new NotificationTask());
        NotificationTask test_task = new NotificationTask("Тестовое сообщение",
                LocalDateTime.of(2023, 01, 01, 00, 00));
        test_task.setChatId(1L);

        NotificationTask result = out.saveTask(1L, "01.01.2023 00:00 Тестовое сообщение");
        Assertions.assertThat(result)
                .isNotNull()
                .isInstanceOf(NotificationTask.class)
                .isEqualTo(test_task);
    }
}