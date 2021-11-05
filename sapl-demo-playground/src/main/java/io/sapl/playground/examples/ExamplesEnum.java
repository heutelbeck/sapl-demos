package io.sapl.playground.examples;

public enum ExamplesEnum {
	Basic("Basic"),
	SpringSecurity("Spring Security"), 
	SpringData("Spring Data");
	
	
	private final String displayValue;
	 
	ExamplesEnum(String displayValue) {
        this.displayValue = displayValue;
    }
 
    public String getDisplayValue() {
        return displayValue;
    }
}
