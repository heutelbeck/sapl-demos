<#
   This Windows PowerShell script demonstrates how to access the demo API using OAuth2.
   The service is accessed by the user "Thomas" using the "testingClient"
#>

$clientId     = "testingClient"
$clientSecret = "secret"
$user         = "Thomas"
$password     = "password"

<#
   Construct the basic authentication string for the "testingClient"
#>

$base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}:{1}" -f $clientId,$clientSecret)))

<#
   Fetch the access token from the authorization server while authenticating as "Thomas".
   Attention: This demo only uses http and no TLS. This is for demo purposes only.
   Always use https/TLS for production.
#>

$result = Invoke-RestMethod "http://localhost:8081/oauth/token" `
          -Headers @{Authorization=("Basic {0}" -f $base64AuthInfo)} `
          -Method Post -ContentType "application/x-www-form-urlencoded"`
          -Body @{client_id=$clientId; 
                  client_secret=$clientSecret; 
                  grant_type="password"; 
                  username=$user;
                  password=$password} -ErrorAction STOP

Write-Host ("Successfully got access token '{0}'" -f $result.access_token)        

<#
   Now access the API using the previously acquired access token.
#>

$result2 = Invoke-RestMethod "http://localhost:8081/patients/1" `
            -Headers @{Authorization=("Bearer {0}" -f $result.access_token)} `
            -Method Get`

Write-Host ("Successfully requested with response '{0}'" -f $result2)        
    