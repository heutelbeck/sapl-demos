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
package io.sapl.ethereum.demo.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PrinterUserService implements UserDetailsService {

	public static final PrinterUser[] DEMO_USERS = new PrinterUser[] {
			new PrinterUser("Alice", "Greenfield", "0xE5a72C7Fa4991920619edCf25eD8828793045A53", null,
					Collections.singletonList(new SimpleGrantedAuthority("USER"))),
			new PrinterUser("Bob", "Springsteen", "0xC4991aAE3621aadE30b9f577c6DA66698bFB7cD8", null,
					Collections.singletonList(new SimpleGrantedAuthority("USER"))) };

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
		for (var demoUser : DEMO_USERS)
			allPrinterUsers.put(demoUser.getUsername(),
					new PrinterUser(demoUser.getUsername(), passwordEncoder.encode(demoUser.getPassword()),
							demoUser.getEthereumAddress(), null, demoUser.getAuthorities()));
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
		} else {
			throw new UsernameNotFoundException("User " + username + " not found.");
		}

	}

}
