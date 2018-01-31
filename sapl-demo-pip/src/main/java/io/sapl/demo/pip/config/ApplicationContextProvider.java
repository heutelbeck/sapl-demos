package io.sapl.demo.pip.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext theContext;

	public static ApplicationContext getApplicationContext() {
		LOGGER.debug("context is null? ->{}", theContext == null);
		return theContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) {
		// synchronized (this) {
		LOGGER.trace("setting context");
		theContext = ac;
		LOGGER.trace("context is null? ->{}", theContext == null);
		// }
	}

}
