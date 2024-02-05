package io.sapl.springdatar2dbcdemo;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import io.sapl.springdatar2dbcdemo.demo.rest.integration.TestContainerBase;

import static org.mockito.Mockito.*;

class SaplSpringDataR2dbcDemoApplicationTest extends TestContainerBase {

    @Test
    void when_applicationStarts_then_runApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {

            mocked.when(() -> SpringApplication.run(SaplSpringDataR2dbcDemoApplication.class
                    ))
                    .thenReturn(mock(ConfigurableApplicationContext.class));

            SaplSpringDataR2dbcDemoApplication.main(new String[] {});

            mocked.verify(() -> SpringApplication.run(SaplSpringDataR2dbcDemoApplication.class), times(1));
        }
    }
}