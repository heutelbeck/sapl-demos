package io.sapl.playground.models;

public enum ExamplesEnum {
	Basic("Basic"),
	SpringSecurity("Spring Security");
	
	
	private String displayValue;
	 
	ExamplesEnum(String displayValue) {
        this.displayValue = displayValue;
    }
 
    public String getDisplayValue() {
        return displayValue;
    }
}
