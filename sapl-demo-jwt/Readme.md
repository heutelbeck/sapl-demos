# Tutorial  sapl-demo-jwt

This demo shows how to use JSON Web tokens together with an annotation that uses Sapl Policies. 

## Tutorial for using the Voter

Obtaining a decision from SAPL Policies we need a `PolicyDecisionPoint`(`PDP`). A `PDP` as a `bean`  is  available as dependency for
a Spring Boot Starter Project, configured in the submodule [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter)
from project <https://github.com/heutelbeck/sapl-policy-engine> .
Remote or embedded `PDP` can be integrated into a Spring Boot Project with:

```java
<dependency>
        <groupId>io.sapl</groupId>
        <artifactId>sapl-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>
```


When using the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) , an annotation `PdpAuthorize` will be automatically provided. You can use it right after adding the dependency.

The only thing you need to provide is the TokenStore, which is needed to decrypt the token header:

```java
  @Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey(privateKey);
		converter.setVerifierKey(publicKey);
		return converter;
	}
```

The usage is pretty straight forward:
```java
	
	import io.sapl.spring.annotation.PdpAuthorize;
	...
	
	@PdpAuthorize
	@GetMapping("{id}")
	public ResponseType myMethod(@PathVariable int id) {
		...
	}


```
Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that shows you the parameters that are actually provided to the `SAPLAuthorizator`, which can help you write your policies.

## How to use the demo

To use this demo, there is a specific workflow that needs to be done to use it. This is needed for every token based security.

1) Obtain a token
	To do this, you need to use a client that is allowed to interact with the system. For the demo project, the client "testingClient" with its secret "secret" is allowed to do a password based attempt.
	This could be done using the tool of your choice. 
	
	With CURL this could look like this:
	```
	curl -X POST -u testingClient:secret -F "grant_type=password" -F "client_id=testingClient" ↩
	-F "username=[USERNAME]" -F "password=[PASSWORD]" http://localhost:8081/oauth/token
	```
	If you prefer Postman, use the client and secret combo as Basic Auth and fill in the other variables as body of type form-data.
2) Use the obtained token to access a restricted method
	For this, you could for example use the user ```Julia``` which is doctor and has the password ```password```. She is allowed to access the diagnosis of patient with id ```1``` by calling ```http://localhost:8081/person/readDiag/1```
	With CURL this could look like this:
	```
	curl -X GET --header "Authorization: Bearer [TOKEN_HERE]" http://localhost:8081/person/readDiag/1
	```
	Using Postman, you just take the token value and use it as header ```Authorization``` with value ```Bearer [TOKEN_HERE]``` whereas you replace [TOKEN_HERE] with the obtained token.
	
## Scripts to see it work
As a comfort feature, two scripts were tinkered, which do all the work.<br>
A script for Linux users utilizing CURL:<br>
test.sh
```sh
client="testingClient"
secret="secret"
userName="Julia"
password="password"
baseAddress="http://localhost:8081"
request="person/readDiag/1"
httpVerb="GET"
curl -X $httpVerb --header "Authorization: Bearer $(curl -X POST -u $client:$secret -F "grant_type=password" ↩
-F "client_id=$client" -F "username=$userName" -F "password=$password" $baseAddress/oauth/token ↩
| grep -Po '"access_token":(.*?[^\\])"' | grep -shoP ':"\K([^"]*)')" $baseAddress/$request
```
And a script for Windows users utilizing Powershell:<br>
test.ps1
```powershell
$clientId = "testingClient"
$clientSecret = "secret"
$user="Julia"
$password="password"
try {
	$base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}:{1}" -f $clientId,$clientSecret)))
	$result = Invoke-RestMethod "http://localhost:8081/oauth/token" `
	-Headers @{Authorization=("Basic {0}" -f $base64AuthInfo)} `
	-Method Post -ContentType "application/x-www-form-urlencoded"`
	-Body @{client_id=$clientId; 
	   client_secret=$clientSecret; 
	   grant_type="password"; 
	   username=$user;
	   password=$password} -ErrorAction STOP
	$success = $true
}
catch
{
	write-host ("Error while getting token:{0}" -f  $_)
}
if ($success)
{
	 try
	 {
		$result2 = Invoke-RestMethod "http://localhost:8081/person/readDiag/1" `
		-Headers @{Authorization=("Bearer {0}" -f $result.access_token)} `
		-Method Get`
		write-host $result2        
	 }
	 catch
	 {
	    	write-host ("Error while calling method:{0}" -f  $_)
	 }
}
```
If there is an issue with ```Invoke-RestMethod``` not found, then your version of powershell is too low (need 3+)
