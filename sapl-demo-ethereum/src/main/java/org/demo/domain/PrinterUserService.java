/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.demo.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vaadin.flow.spring.annotation.SpringComponent;

@Service
@SpringComponent
public class PrinterUserService implements UserDetailsService {

	private final HashMap<String, PrinterUser> allPrinterUsers = new HashMap<>();

	private final PasswordEncoder passwordEncoder;

	public PrinterUserService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
		if (allPrinterUsers.isEmpty())
			createDemoData();
	}

	public Collection<PrinterUser> findAll() {
		return allPrinterUsers.values();
	}

	private void createDemoData() {
		allPrinterUsers.put("Alice",
				new PrinterUser("Alice", passwordEncoder.encode("Greenfield"),
						"0xE5a72C7Fa4991920619edCf25eD8828793045A53", null,
						Collections.singletonList(new SimpleGrantedAuthority("USER"))));
		allPrinterUsers.put("Bob",
				new PrinterUser("Bob", passwordEncoder.encode("Springsteen"),
						"0xC4991aAE3621aadE30b9f577c6DA66698bFB7cD8", null,
						Collections.singletonList(new SimpleGrantedAuthority("USER"))));

	}

	public PrinterUser loadUser(String username) {
		return allPrinterUsers.get(username);
	}

	@Override
	public PrinterUser loadUserByUsername(String username) throws UsernameNotFoundException {
		if (allPrinterUsers.isEmpty())
			createDemoData();
		PrinterUser user = allPrinterUsers.get(username);

		if (user != null) {
			return user.copy();
		}
		else {
			throw new UsernameNotFoundException("User " + username + " not found.");
		}

	}

}
