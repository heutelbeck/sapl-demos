package org.demo.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PrinterUserService implements UserDetailsService {

	private final HashMap<String, PrinterUser> allPrinterUsers = new HashMap<>();

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public PrinterUserService() {
		createDemoData();
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

	private void createDemoData() {
		allPrinterUsers.put("Alice", new PrinterUser("Alice", passwordEncoder.encode("Greenfield"), "0x12345",
				Arrays.asList(new SimpleGrantedAuthority("USER"))));
		allPrinterUsers.put("Bob", new PrinterUser("Bob", passwordEncoder.encode("Springsteen"), "0x54321",
				Arrays.asList(new SimpleGrantedAuthority("USER"))));

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return allPrinterUsers.get(username);
	}

}
