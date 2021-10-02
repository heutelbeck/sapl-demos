package io.sapl.playground.examples;

public class SpringSecurityExample extends Example {

	public SpringSecurityExample() {
		
		this.mockDefinition = "[]";
		
		this.policy = "/*\r\n"
				+ " * All doctors and nurses have full read access on all patient records.\r\n"
				+ " */\r\n"
				+ "policy \"doctor and nurse access to patient data\"\r\n"
				+ "permit \r\n"
				+ "       action.java.name == \"findById\"\r\n"
				+ "where \r\n"
				+ "       \"ROLE_DOCTOR\" in subject..authority || \"ROLE_NURSE\" in subject..authority; ";
		
		
		this.authzSub = "{\r\n"
				+ "\"action\":{\"java\":{\"name\":\"findById\"}},\r\n"
				+ "\"resource\":\"ui:view:patients:createPatientButton\", \r\n"
				+ "\"subject\":{\"authorities\":[{\"authority\":\"ROLE_DOCTOR\"}],\"details\":{\"remoteAddress\":\"0:0:0:0:0:0:0:1\",\"sessionId\":\"102486A0FD0D716DDF4A8D4DD38940D0\"},\"authenticated\":true,\"principal\":{\"password\":null,\"username\":\"Alina\",\"authorities\":[{\"authority\":\"ROLE_DOCTOR\"}],\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"enabled\":true},\"credentials\":null,\"name\":\"Alina\"}\r\n"
				+ "}";
		
		this.displayName = "Spring Security";
	}
}
