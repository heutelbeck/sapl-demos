package org.demo.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vaadin.flow.spring.annotation.SpringComponent;

@Service
@SpringComponent
public class PrinterUserService implements UserDetailsService {

	private final HashMap<String, PrinterUser> allPrinterUsers = new HashMap<>();

	public PrinterUserService(PasswordEncoder passwordEncoder) {
		if (allPrinterUsers.isEmpty())
			createDemoData(passwordEncoder);
	}

	public synchronized void delete(PrinterUser value) {
		allPrinterUsers.remove(value.getEthereumAddress());
	}

	public synchronized void save(PrinterUser entry) {

		try {
			entry = entry.clone();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		allPrinterUsers.put(entry.getEthereumAddress(), entry);
	}

	public Collection<PrinterUser> findAll() {
		return allPrinterUsers.values();
	}

	private void createDemoData(PasswordEncoder passwordEncoder) {
		allPrinterUsers.put("Alice",
				new PrinterUser("Alice", passwordEncoder.encode("Greenfield"),
						"0xE5a72C7Fa4991920619edCf25eD8828793045A53",
						Collections.singletonList(new SimpleGrantedAuthority("USER"))));
		allPrinterUsers.put("Bob",
				new PrinterUser("Bob", passwordEncoder.encode("Springsteen"),
						"0xC4991aAE3621aadE30b9f577c6DA66698bFB7cD8",
						Collections.singletonList(new SimpleGrantedAuthority("USER"))));

	}

	public PrinterUser loadUser(String username) {
		return allPrinterUsers.get(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return allPrinterUsers.get(username);
	}

}
