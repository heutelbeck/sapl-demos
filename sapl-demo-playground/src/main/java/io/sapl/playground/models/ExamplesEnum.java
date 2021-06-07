package io.sapl.playground.models;

public enum ExamplesEnum {
	Basic("Basic"),
	SpringSecurity("Spring Security"), 
	SpringData("Spring Data");
	
	
	private String displayValue;
	 
	ExamplesEnum(String displayValue) {
        this.displayValue = displayValue;
    }
 
    public String getDisplayValue() {
        return displayValue;
    }
}
