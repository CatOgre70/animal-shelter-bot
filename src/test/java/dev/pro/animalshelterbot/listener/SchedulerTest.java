package dev.pro.animalshelterbot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import dev.pro.animalshelterbot.constants.AnimalKind;
import dev.pro.animalshelterbot.constants.Messages;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.repository.AnimalRepository;
import dev.pro.animalshelterbot.repository.DailyReportRepository;
import dev.pro.animalshelterbot.service.AnimalService;
import dev.pro.animalshelterbot.service.ChatConfigService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchedulerTest {

    @Mock
    private AnimalService animalService;

    @Mock
    private DailyReportRepository dailyReportRepository;

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private Scheduler scheduler;

    @LocalServerPort
    private int port;

    @Test
    public void schedulerTest() {
        Animal animal = new Animal("Sharik", AnimalKind.DOG, "Cur", "Brown",
                "Любит жрать тухлую селедку с помойки, но слаб желудком, не позволять ни в коем случае!",
                null, 0L, null,
                LocalDateTime.of(2023, 1, 7, 9, 0)
                        .truncatedTo(ChronoUnit.DAYS), null);
        User user = new User("Vasily", "Demin", "CatOgre", null,
                null, 1234567890L);
        user.setId(1L);
        animal.setId(2L);
        animal.setOwner(user);
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDate adoptionDate1 = LocalDateTime.of(2023, 1, 7, 9, 0)
                .truncatedTo(ChronoUnit.DAYS).toLocalDate();
        LocalDate today1 = dateTime.toLocalDate();
        Period period = Period.between(today1, adoptionDate1);
        DailyReport dailyReport = new DailyReport(dateTime, null, 0L, null, null,
                "Жрал тухлую селедку с помойки", "Блевал, но выглядел счастливым",
                "Пока не отучил его жрать то, что находит на помойке", 2L);
        Mockito.when(animalService.getAllAdoptedAnimals()).thenReturn(List.of(animal));
        Mockito.when(dailyReportRepository.getDailyReportByAnimalId(any(Long.class))).thenReturn(List.of(dailyReport));

        scheduler.sendNotification();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(1234567890L);
        Assertions.assertThat(actual.getParameters().get("text"))
                .isEqualTo(Messages.DR_MORE_THAN_TWO_FAYS.messageText);

    }


}
