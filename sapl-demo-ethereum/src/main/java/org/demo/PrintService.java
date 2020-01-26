package org.demo;

import java.io.Serializable;

import org.springframework.stereotype.Service;

@Service
public class PrintService implements Serializable {

	private static final long serialVersionUID = -6922547661863493568L;

	public String print(String address) {
        if (address == null || address.isEmpty()) {
            return "Print job started";
        } else {
            return "Print job started for address + " + address;
        }
    }

}
