package io.sapl.playground.models;

public class BasicExample extends Example {

	public BasicExample() {
		
		this.mockDefinition = "[\n"
				+ "  {\n"
				+ "    \"type\"	   : \"ATTRIBUTE\",\n"
				+ "    \"importName\": \"clock.now\",\n"
				+ "    \"sequence\": [1, 2, 3]\n"
				+ "  }, \n"
				+ "  {\n"
				+ "    \"type\": \"FUNCTION\",\n"
				+ "    \"importName\": \"time.dayOfWeekFrom\",\n"
				+ "    \"sequence\": [\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]\n"
				+ "  }\n"
				+ "]";
		
		this.policy = "policy \"policy 1\"\n"
				+ "permit\n"
				+ "    action == \"read\"\n"
				+ "where\n"
				+ "    subject == \"WILLI\";\n"
				+ "    time.dayOfWeekFrom(\"UTC\".<clock.now>) =~ \"MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY\";";
		
		
		this.authSub = "{\n"
    			+ " \"subject\": \"WILLI\",\n"
    			+ " \"action\"      : \"read\",\n"
    			+ " \"resource\"    : \"something\"\n"
    			+ "}";
	}
}
