package org.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerData {

	private String title;

	private String name;

	private String date;

	@Override
	public String toString() {
		return title + name + ": " + date;
	}

}
