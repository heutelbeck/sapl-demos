package org.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeScheduleData {

	private String title;

	private String name;

	private String date;

	@Override
	public String toString() {
		if (title == null && name == null && date == null) {
			return "";
		}
		return title + name + ": " + date;
	}

}
