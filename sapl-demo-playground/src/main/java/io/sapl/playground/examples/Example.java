package io.sapl.playground.examples;

public abstract class Example {
	
	protected String mockDefinition;

	protected String policy;
	
	protected String authzSub;
	
	protected String displayName;
	
	public String getMockDefinition() {
		return this.mockDefinition;
	}
	
	public String getPolicy() {
		return this.policy;
	}
	
	public String getAuthzSub() {
		return this.authzSub;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
