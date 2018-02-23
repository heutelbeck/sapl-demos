package io.sapl.demo.shared.marshalling;


import io.sapl.spring.marshall.mapper.SimpleSaplMapper;
import org.springframework.context.annotation.Bean;


public class MapperUsageExample {

	@Bean
	public SimpleSaplMapper getSaplMapper() {
		SimpleSaplMapper saplMapper = new SimpleSaplMapper();
		/*
		 * Now you can add your own SaplClassMappers with saplMapper.register(new CustomSaplClassMapper mySaplClassMapper);
		 */
		return saplMapper;
	}
}
