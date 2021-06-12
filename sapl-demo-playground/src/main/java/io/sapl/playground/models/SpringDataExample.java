package io.sapl.playground.models;

public class SpringDataExample extends Example {

	public SpringDataExample() {
		
		this.mockDefinition = "[\r\n"
				+ "  {\r\n"
				+ "    \"type\"	   : \"ATTRIBUTE\",\r\n"
				+ "    \"importName\": \"patient.relatives\",\r\n"
				+ "    \"sequence\": [\"dominic\"]\r\n"
				+ "  }\r\n"
				+ "]";
		
		this.policy = "/*\r\n"
				+ " * Visitors which are relatives may see the name, phone number and room number.\r\n"
				+ " */\r\n"
				+ "policy \"visiting relatives access patient data\"\r\n"
				+ "permit \r\n"
				+ "       action.java.name == \"findById\"\r\n"
				+ "where \r\n"
				+ "       \"ROLE_VISITOR\" in subject..authority;\r\n"
				+ "        /*\r\n"
				+ "         * The next condition invokes the \"patient\" policy information point and \r\n"
				+ "         * determines the \"relatives\" attribute of id of the patient.\r\n"
				+ "         * The policy information policy point accesses the database to determine \r\n"
				+ "         * the relatives of the patient and it is checked if the subject is in the \r\n"
				+ "         * list of relatives.\r\n"
				+ "         */\r\n"
				+ "       subject.name in resource.id.<patient.relatives>; \r\n"
				+ "transform \r\n"
				+ "		// Subtractive template with filters removing content\r\n"
				+ "		resource |- { \r\n"
				+ "						@.medicalRecordNumber 	: remove,\r\n"
				+ "						@.icd11Code 			: remove,\r\n"
				+ "						@.diagnosisText 		: remove,\r\n"
				+ "						@.attendingDoctor 		: remove,\r\n"
				+ "						@.attendingNurse 		: remove \r\n"
				+ "					}";
		
		
		this.authSub = "{\r\n"
				+ "  	\"action\": {\r\n"
				+ "      \"http\":{\r\n"
				+ "        \"characterEncoding\":\"UTF-8\",\"protocol\":\"HTTP/1.1\",\"scheme\":\"http\",\"serverName\":\"localhost\",\"serverPort\":8080,\"remoteAddress\":\"0:0:0:0:0:0:0:1\",\"remoteHost\":\"0:0:0:0:0:0:0:1\",\"remotePort\":55317,\"isSecure\":false,\"localName\":\"0:0:0:0:0:0:0:1\",\"localAddress\":\"0:0:0:0:0:0:0:1\",\"localPort\":8080,\r\n"
				+ "        \"method\":\"GET\",\r\n"
				+ "        \"contextPath\":\"\",\"requestedSessionId\":\"DF998C0CF0DD33417488187D5338674D\",\r\n"
				+ "        \"requestedURI\":\"/patients/1\",\"requestURL\":\"http://localhost:8080/patients/1\",\"servletPath\":\"/patients/1\",\"headers\":{\"host\":[\"localhost:8080\"],\"connection\":[\"keep-alive\"],\"sec-ch-ua\":[\"\\\" Not;A Brand\\\";v=\\\"99\\\", \\\"Google Chrome\\\";v=\\\"91\\\", \\\"Chromium\\\";v=\\\"91\\\"\"],\"accept\":[\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\"],\"upgrade-insecure-requests\":[\"1\"],\"sec-ch-ua-mobile\":[\"?0\"],\"user-agent\":[\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36\"],\"sec-fetch-site\":[\"same-origin\"],\"sec-fetch-mode\":[\"same-origin\"],\"sec-fetch-dest\":[\"empty\"],\"referer\":[\"http://localhost:8080/patients\"],\"accept-encoding\":[\"gzip, deflate, br\"],\"accept-language\":[\"de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6\"]},\"cookies\":[{\"name\":\"JSESSIONID\",\"value\":\"DF998C0CF0DD33417488187D5338674D\",\"comment\":null,\"domain\":null,\"maxAge\":-1,\"path\":null,\"secure\":false,\"version\":0,\"httpOnly\":false}],\"locale\":\"de_DE\",\"locales\":[\"de_DE\",\"de\",\"en_DE\",\"en\",\"en_US\"]},\r\n"
				+ "      	\"java\":{\r\n"
				+ "          \"name\":\"findById\",\"declaringTypeName\":\"org.demo.domain.PatientRepository\",\"modifiers\":[\"public\"],\"instanceof\":[{\"name\":\"com.sun.proxy.$Proxy133\",\"simpleName\":\"$Proxy133\"},{\"name\":\"org.demo.domain.JpaPatientRepository\",\"simpleName\":\"JpaPatientRepository\"},{\"name\":\"org.springframework.data.repository.CrudRepository\",\"simpleName\":\"CrudRepository\"},{\"name\":\"org.springframework.data.repository.Repository\",\"simpleName\":\"Repository\"},{\"name\":\"org.demo.domain.PatientRepository\",\"simpleName\":\"PatientRepository\"},{\"name\":\"org.springframework.data.repository.Repository\",\"simpleName\":\"Repository\"},{\"name\":\"org.springframework.transaction.interceptor.TransactionalProxy\",\"simpleName\":\"TransactionalProxy\"},{\"name\":\"org.springframework.aop.SpringProxy\",\"simpleName\":\"SpringProxy\"},{\"name\":\"org.springframework.aop.framework.Advised\",\"simpleName\":\"Advised\"},{\"name\":\"org.springframework.aop.TargetClassAware\",\"simpleName\":\"TargetClassAware\"},{\"name\":\"org.springframework.core.DecoratingProxy\",\"simpleName\":\"DecoratingProxy\"},{\"name\":\"java.lang.reflect.Proxy\",\"simpleName\":\"Proxy\"},{\"name\":\"java.io.Serializable\",\"simpleName\":\"Serializable\"},{\"name\":\"java.lang.Object\",\"simpleName\":\"Object\"}]\r\n"
				+ "        },\"arguments\":[1]\r\n"
				+ "    },\r\n"
				+ "	\"resource\":{\r\n"
				+ "      \"id\":1,\r\n"
				+ "      \"medicalRecordNumber\":\"123456\",\r\n"
				+ "      \"name\":\"Lenny\",\r\n"
				+ "      \"icd11Code\":\"DA63.Z/ME24.90\",\r\n"
				+ "      \"diagnosisText\":\"Duodenal ulcer with acute haemorrhage.\",\r\n"
				+ "      \"attendingDoctor\":\"Julia\",\r\n"
				+ "      \"attendingNurse\":\"Thomas\",\r\n"
				+ "      \"phoneNumber\":\"+78(0)456-789\",\r\n"
				+ "      \"roomNumber\":\"A.3.47\"},\r\n"
				+ "	\"subject\":{\r\n"
				+ "      \"authorities\":[\r\n"
				+ "        {\"authority\":\"ROLE_VISITOR\"}\r\n"
				+ "      ],\r\n"
				+ "      \"details\":{\"remoteAddress\":\"0:0:0:0:0:0:0:1\",\"sessionId\":\"15C444A3B41D914F0B61AD68916DEEF3\"},\"authenticated\":true,\r\n"
				+ "      \"principal\":{\r\n"
				+ "        \"password\":null,\r\n"
				+ "        \"username\":\"Dominic\",\r\n"
				+ "        \"authorities\":[\r\n"
				+ "          {\"authority\":\"ROLE_VISITOR\"}\r\n"
				+ "        ],\r\n"
				+ "        \"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"enabled\":true\r\n"
				+ "      },\r\n"
				+ "      \"credentials\":null,\"name\":\"Dominic\"}\r\n"
				+ "}";
		
		this.displayName = "Spring Data";
	}
}
