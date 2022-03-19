# Demo: Multi Tenant PDP

## What does it do?

A simple implementation of a multi-tenant PDP

Can only be run with mvn spring-boot:run. Not from JAR. 

1. Login as 'horsta' password 'password'

Will enforce policies resources/policies/tenant_a  (is configured for file access. change to other path in application.yml, not working from JAR)

2. Go to http://localhost:8080/patients/1

You will see strings blackened with hearts.

3. Go to http://localhost:8080/logout

4. Login as 'horstb' password 'password'

Will enforce policies resources/policies/tenant_b  (is configured for file access. change to other path in application.yml, not working from JAR)

5. Go to http://localhost:8080/patients/1

You will see strings blackened with black squares.

