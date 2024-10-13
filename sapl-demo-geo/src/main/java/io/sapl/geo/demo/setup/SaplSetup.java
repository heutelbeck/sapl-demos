package io.sapl.geo.demo.setup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaplSetup implements CommandLineRunner{

	private final PostgreSQLContainer<?> postgisContainer;
	private final GenericContainer<?> traccarContainer;
	private final GenericContainer<?> ownTracksContainer;
	
	@Override
    public void run(String... args) throws Exception {
		
		var pdpTemplate = """
                {
                 "algorithm": "DENY_OVERRIDES",
                 "variables":
                     {
                        "TRACCAR_DEFAULT_CONFIG":
                        {
				        	"user":"testuser@example.com",
                            "password":"password",
                            "server":"%s",
                            "protocol": "http"
                        },
                         "OWNTRACKS_DEFAULT_CONFIG":
				 		{
                			"server":"%s",
                			"protocol": "http"
                		},
                		"POSTGIS_DEFAULT_CONFIG":
                  		{
                  			"user":"%s",
                  			"password":"%s",
                  			"server":"%s",
                  			"port": %s,
                  			"dataBase":"%s",
                  			"dataBaseType" : "POSTGIS"
                  		}
                     }
                 }
               """;
		
		var traccarServer = String.format("%s:%s", traccarContainer.getHost(), traccarContainer.getMappedPort(8082));
		var ownTracksServer = String.format("%s:%s", ownTracksContainer.getHost(), ownTracksContainer.getMappedPort(8083));
		var pdp = String.format(pdpTemplate, traccarServer, ownTracksServer, postgisContainer.getUsername(), 
				postgisContainer.getPassword(), postgisContainer.getHost(), postgisContainer.getMappedPort(5432), 
				postgisContainer.getDatabaseName() );
		//writeFile(pdp, "src/main/resources/policies/pdp.json");
		writeFile(pdp, "D:/Test/pdp.json");
		
		
		var traccarPolicy = """
                policy "permit traccar" 
					permit
					where
					  subject.principal.geoTracker == "TRACCAR";
					  var position = <traccar.position({"responseFormat":"GEOJSON", "deviceId":subject.principal.trackerDeviceId})>;
					  var hagen = <postGis.geometry({"table":"geometries", "geoColumn":"geom", "defaultCRS": 3857, "where":"name='Hagen'", "singleResult": true, "latitudeFirst":false})>;
					  geoFunctions.within(position.position, hagen.geo);
               """;
		writeFile(traccarPolicy, "D:/Test/permit_traccar.sapl");
		
		var ownTracksPolicy = """
                policy "permit owntracks" 
					permit
					where
					  subject.principal.geoTracker == "OWNTRACKS";  
					  var position=<ownTracks.positionAndFences({"user": subject.principal.username, "deviceId": subject.principal.trackerDeviceId})>;
					  var hagen = <postGis.geometry({"table":"geometries", "geoColumn":"geom", "defaultCRS": 3857, "where":"name='Hagen'", "singleResult": true, "latitudeFirst":false})>;
					  geoFunctions.within(position.position, hagen.geo);
               """;
		writeFile(ownTracksPolicy, "D:/Test/permit_owntracks.sapl");
	}
	
	private void writeFile(String json, String path) throws IOException {

        Path filePath = Paths.get(path);
        try (var writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(json);
        }
    }
	
}
