#token=$(curl -X POST -u testingClient:secret -F "grant_type=password" -F "client_id=testingClient" -F "username=Julia" -F "password=password" http://localhost:8081/oauth/token | grep -Po '"access_token":(.*?[^\\])"' | grep -shoP ':"\K([^"]*)')
#curl -X GET --header "Authorization: Bearer $token" http://localhost:8081/person/readDiag/1
client="testingClient"
secret="secret"
userName="Julia"
password="password"
baseAddress="http://localhost:8081"
request="person/readDiag/1"
httpVerb="GET"
curl -X $httpVerb --header "Authorization: Bearer $(curl -X POST -u $client:$secret -F "grant_type=password" -F "client_id=$client" -F "username=$userName" -F "password=$password" $baseAddress/oauth/token | grep -Po '"access_token":(.*?[^\\])"' | grep -shoP ':"\K([^"]*)')" $baseAddress/$request