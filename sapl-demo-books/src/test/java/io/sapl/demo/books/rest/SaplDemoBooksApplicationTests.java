package io.sapl.demo.books.rest;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import io.sapl.demo.books.data.DemoData;
import io.sapl.demo.books.domain.LibraryUser;

@SpringBootTest
class SaplDemoBooksApplicationTests {

	@Autowired
	BookController controller;

	private static Collection<LibraryUser> userSource() {
		return List.of(DemoData.DEMO_USERS);
	}

	@Disabled
	@ParameterizedTest
	@MethodSource("userSource")
	public void contextLoads(LibraryUser user) {
		System.out.println(user);
		var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		System.out.println(controller.findAll());
	}

}
