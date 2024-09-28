package io.sapl.geo.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.sapl.geo.demo.service.GeometryService;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping(value="/app")
public class AppController {

    private final GeometryService geometryService;
	
    
	
	@PostMapping(value="/moveTraccar")
	public String moveTraccar(@RequestBody MoveRequest request) {

    	geometryService.addTraccarPosition(request.getDeviceId(), request.getLat(), request.getLon()).block();
    	return "Move action received";	
		
	}
	
	@PostMapping(value="/moveOwnTracks")
	public String moveOwnTracks(@RequestBody MoveRequest request) {

    	geometryService.addOwntracksPosition(request.getUsername(), request.getDeviceId(), request.getLat(), request.getLon())
    	.then()
    	.block();
    	return "Move action received";		
		
	}
	
}


