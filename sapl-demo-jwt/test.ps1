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