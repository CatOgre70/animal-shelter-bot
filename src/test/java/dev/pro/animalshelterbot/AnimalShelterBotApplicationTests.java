package dev.pro.animalshelterbot;

import com.pengrad.telegrambot.TelegramBot;
import dev.pro.animalshelterbot.controller.AnimalController;
import dev.pro.animalshelterbot.controller.DailyReportController;
import dev.pro.animalshelterbot.controller.UserController;
//import dev.pro.animalshelterbot.listener.TelegramBotUpdatesListener;
import dev.pro.animalshelterbot.listener.TelegramBotUpdatesListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnimalShelterBotApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private UserController userController;

	@Autowired
	private AnimalController animalController;

	@Autowired
	private DailyReportController dailyReportController;

	@Autowired
	private TelegramBot telegramBot;

	@Autowired
	private TelegramBotUpdatesListener telegramBotUpdatesListener;

	@Test
	void contextLoads() {
		Assertions.assertThat(telegramBot).isNotNull();
		Assertions.assertThat(userController).isNotNull();
		Assertions.assertThat(animalController).isNotNull();
		Assertions.assertThat(dailyReportController).isNotNull();
		Assertions.assertThat(telegramBotUpdatesListener).isNotNull();
	}

}
