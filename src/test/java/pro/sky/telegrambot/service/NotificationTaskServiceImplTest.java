package pro.sky.telegrambot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.exception.DateTimeFromThePastException;
import pro.sky.telegrambot.exception.TextPatternDoesNotMatchException;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTaskServiceImplTest {

    @Mock
    private NotificationTaskRepository taskRepository;

    @InjectMocks
    private NotificationTaskServiceImpl out;

    @Test
    void shouldCreateTaskEntityAndReturnIt_whenSaveTask() {
        NotificationTask test_task = new NotificationTask("Тестовое сообщение",
                LocalDateTime.of(2023, 01, 01, 00, 00));
        test_task.setChatId(1L);

        when(taskRepository.save(any(NotificationTask.class))).thenReturn(test_task);

        NotificationTask result = out.saveTask(1L, "01.01.2023 00:00 Тест");
        assertThat(result)
                .isNotNull()
                .isInstanceOf(NotificationTask.class);
    }

    @Test
    void shouldThrowDateTimeParseException_whenDateTimeHasWrongPattern() {

        assertThatThrownBy(() -> out.saveTask(1L, "01:01:2023 00.00 Тестовое сообщение")).isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void shouldThrowTextPatternDoesNotMatchException_whenTextDoesNotMatchRegexp() {

        assertThatThrownBy(() -> out.saveTask(1L, "01.01.2023 00:00Тестовое сообщение")).isInstanceOf(TextPatternDoesNotMatchException.class);
    }

    @Test
    void shouldThrowDateTimeFromThePastException_whenUserInputsDateTimeFromThePast() {

        assertThatThrownBy(() -> out.saveTask(1L, "01.01.2000 00:00 Тестовое сообщение")).isInstanceOf(DateTimeFromThePastException.class);
    }
}