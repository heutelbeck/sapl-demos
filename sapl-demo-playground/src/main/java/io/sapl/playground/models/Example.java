package io.sapl.playground.models;

public abstract class Example {
	
	protected String mockDefinition;

	protected String policy;
	
	protected String authSub;
	
	protected String displayName;
	
	public String getMockDefinition() {
		return this.mockDefinition;
	}
	
	public String getPolicy() {
		return this.policy;
	}
	
	public String getAuthSub() {
		return this.authSub;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
