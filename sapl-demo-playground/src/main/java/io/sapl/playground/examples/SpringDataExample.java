/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.playground.examples;

public class SpringDataExample extends Example {
	
	public SpringDataExample() {

		this.mockDefinition = """
				[
				  {
				    "type" : "ATTRIBUTE",
				    "importName" : "patient.relatives",
				    "always" : ["Dominic"]
				  }
				]""";

		this.policy = """
				/*
				 * Visitors which are relatives may see the name, phone number and room number.
				 */
				import filter.*
				policy "visiting relatives access patient data"
				permit
				        action.java.name == "findById"
				where
				       "ROLE_VISITOR" in subject..authority;
				        /*
				         * The next condition invokes the "patient" policy information point and
				         * determines the "relatives" attribute of id of the patient.
				         * The policy information policy point accesses the database to determine
				         * the relatives of the patient and it is checked if the subject is in the
				         * list of relatives.
				         */
				        subject.name in resource.id.<patient.relatives>;
				transform
				        // Subtractive template with filters removing content
				        resource |- {
				                      @.medicalRecordNumber : remove,
				                      @.icd11Code           : remove,
				                      @.diagnosisText       : remove,
				                      @.attendingDoctor     : remove,
				                      @.attendingNurse      : remove
				                    }""";

		this.authzSub = """
				{
				    "action" : {
				      "http" : {
				        "characterEncoding"  : "UTF-8",
				        "protocol"           : "HTTP/1.1",
				        "scheme"             : "http",
				        "serverName"         : "localhost",
				        "serverPort"         : 8080,
				        "remoteAddress"      : "0:0:0:0:0:0:0:1",
				        "remoteHost"         : "0:0:0:0:0:0:0:1",
				        "remotePort"         : 55317,
				        "isSecure"           : false,
				        "localName"          : "0:0:0:0:0:0:0:1",
				        "localAddress"       : "0:0:0:0:0:0:0:1",
				        "localPort"          : 8080,
				        "method"             : "GET",
				        "contextPath"        : "",
				        "requestedSessionId" : "DF998C0CF0DD33417488187D5338674D",
				        "requestedURI"       : "/patients/1",
				        "requestURL"         : "http://localhost:8080/patients/1",
				        "servletPath"        : "/patients/1",
				        "headers"            : {
				          "host"                     : ["localhost:8080"],
				          "connection"                : ["keep-alive"],
				          "accept"                    : ["text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"],
				          "upgrade-insecure-requests" : ["1"],
				          "sec-ch-ua-mobile"          : ["?0"],
				          "user-agent"                : ["Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36"],
				          "sec-fetch-site"            : ["same-origin"],
				          "sec-fetch-mode"            : ["same-origin"],
				          "sec-fetch-dest"            : ["empty"],
				          "referer"                   : ["http://localhost:8080/patients"],
				          "accept-encoding"           : ["gzip, deflate, br"],
				          "accept-language"           : ["de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6"]
				        },
				        "cookies"    : [ {
				          "name"     : "JSESSIONID",
				          "value"    : "DF998C0CF0DD33417488187D5338674D",
				          "comment"  : null,
				          "domain"   : null,
				          "maxAge"   : -1,
				          "path"     : null,
				          "secure"   : false,
				          "version"  : 0,
				          "httpOnly" : false
				        }],
				        "locale"  : "de_DE",
				        "locales" : ["de_DE","de","en_DE","en","en_US"]
				      },
				      "java":{
				          "name":"findById","declaringTypeName":"org.demo.domain.PatientRepository","modifiers":["public"],"instanceof":[{"name":"com.sun.proxy.$Proxy133","simpleName":"$Proxy133"},{"name":"org.demo.domain.JpaPatientRepository","simpleName":"JpaPatientRepository"},{"name":"org.springframework.data.repository.CrudRepository","simpleName":"CrudRepository"},{"name":"org.springframework.data.repository.Repository","simpleName":"Repository"},{"name":"org.demo.domain.PatientRepository","simpleName":"PatientRepository"},{"name":"org.springframework.data.repository.Repository","simpleName":"Repository"},{"name":"org.springframework.transaction.interceptor.TransactionalProxy","simpleName":"TransactionalProxy"},{"name":"org.springframework.aop.SpringProxy","simpleName":"SpringProxy"},{"name":"org.springframework.aop.framework.Advised","simpleName":"Advised"},{"name":"org.springframework.aop.TargetClassAware","simpleName":"TargetClassAware"},{"name":"org.springframework.core.DecoratingProxy","simpleName":"DecoratingProxy"},{"name":"java.lang.reflect.Proxy","simpleName":"Proxy"},{"name":"java.io.Serializable","simpleName":"Serializable"},{"name":"java.lang.Object","simpleName":"Object"}]
				        },"arguments":[1]
				    },
				    "resource":{
				      "id":1,
				      "medicalRecordNumber" : "123456",
				      "name"                : "Lenny",
				      "icd11Code"           : "DA63.Z/ME24.90",
				      "diagnosisText"       : "Duodenal ulcer with acute haemorrhage.",
				      "attendingDoctor"     : "Julia",
				      "attendingNurse"      : "Thomas",
				      "phoneNumber"         : "+78(0)456-789",
				      "roomNumber"          : "A.3.47"},
				    "subject":{
				      "authorities" : [{"authority":"ROLE_VISITOR"}],
				      "details"     : {"remoteAddress":"0:0:0:0:0:0:0:1","sessionId":"15C444A3B41D914F0B61AD68916DEEF3"},"authenticated":true,
				      "principal"   : {
				        "password"              : null,
				        "username"              : "Dominic",
				        "authorities"           : [{"authority":"ROLE_VISITOR"}],
				        "accountNonExpired"     : true,
				        "accountNonLocked"      : true,
				        "credentialsNonExpired" : true,
				        "enabled"               :true
				      },
				      "credentials" : null,
				      "name"        : "Dominic"
				   }
				}""";

		this.displayName = "Spring Data";
	}

}
