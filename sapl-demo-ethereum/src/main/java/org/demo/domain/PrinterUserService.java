package org.demo.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

public class PrinterUserService {

	private static PrinterUserService instance;

	private final HashMap<String, PrinterUser> allPrinterUsers = new HashMap<>();

	private PrinterUserService() {
	}

	public static PrinterUserService getInstance() {
		if (instance == null) {
			instance = new PrinterUserService();
			instance.createDemoData();
		}
		return instance;
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
		allPrinterUsers.put("0x12345", new PrinterUser("Alice", "Greenfield", "0x12345", LocalDate.of(1975, 12, 20)));
		allPrinterUsers.put("0x54321", new PrinterUser("Bob", "Springsteen", "0x54321", LocalDate.of(1989, 5, 12)));

	}

}
