package io.sapl.geo.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.sapl.geo.demo.domain.Data;
import io.sapl.geo.demo.domain.DataRepository;
import io.sapl.geo.demo.domain.GeoTracker;
import io.sapl.geo.demo.domain.GeoUser;
import io.sapl.geo.demo.service.GeometryService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;


@RestController
@RequiredArgsConstructor
@RequestMapping(value="/app")
public class AppController {

    private final GeometryService geometryService;
	private final DataRepository dataRepository;
    
	
	@GetMapping(value="/move")
	public String moveOwnTracks(@AuthenticationPrincipal GeoUser userDetails) {
		
		var coordinate = userDetails.getNextCoordinate();
		var tracker = userDetails.getGeoTracker();
		if (tracker == GeoTracker.OWNTRACKS) {
			geometryService.addOwntracksPosition(userDetails.getUsername(), userDetails.getTrackerDeviceId(), coordinate.x, coordinate.y)
	    	.then()
	    	.block();	
		}else if (tracker == GeoTracker.TRACCAR) {
			
			geometryService.addTraccarPosition(userDetails.getUniqueDeviceId(), coordinate.x, coordinate.y).block();
		}
		
    	return "Move action received";				
	}	
	
	@GetMapping(value="/getData")
	public Flux<Data> getData() {

    	return dataRepository.findAll();		
		
	}
	
}


