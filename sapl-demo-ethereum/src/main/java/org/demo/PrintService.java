package org.demo;

import java.io.Serializable;

import org.springframework.stereotype.Service;

@Service
public class PrintService implements Serializable {

	private static final long serialVersionUID = -268367144866825840L;

	public String print(String address) {
		if (address == null || address.isEmpty()) {
			return "Print job started";
		}
		else {
			return "Print job started for address + " + address;
		}
	}

}
