package io.sapl.springdatamongoreactivedemo;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.*;


@SpringBootTest(classes = SaplSpringDataMongoReactiveDemoApplication.class)
class SaplSpringDataMongoReactiveDemoApplicationTest {

	@Test
	void when_applicationStarts_then_runApplication() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {

			mocked.when(() -> SpringApplication.run(SaplSpringDataMongoReactiveDemoApplication.class
					))
					.thenReturn(mock(ConfigurableApplicationContext.class));

			SaplSpringDataMongoReactiveDemoApplication.main(new String[] {});

			mocked.verify(() -> SpringApplication.run(SaplSpringDataMongoReactiveDemoApplication.class), times(1));
		}
	}
}