package io.sapl.geo.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.geo.demo.service.GeometryService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/stream")
public class StreamController {

    private final GeometryService geometryService;
	
	@RequestMapping(value="/streamGeometriesFromPostGIS", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<JsonNode> streamGeometriesFromPostGIS(Model model) throws JsonProcessingException {
       
            return geometryService.getFencesAndLocationsFromPostgis();  
    }
	
	@RequestMapping(value="/streamPositionsFromTraccar", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<JsonNode> streamPositionsFromTraccar(@RequestParam String id, Model model) throws JsonProcessingException {
       
            return geometryService.getPositionFromTraccar(id);    
    }
	
	@RequestMapping(value="/streamGeofencesFromTraccar", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<JsonNode> streamGeofencesFromTraccar(@RequestParam String id, Model model) throws JsonProcessingException {
       
            return geometryService.getGeofencesFromTraccar(id);    
    }
	
	@RequestMapping(value="/streamFeaturesFromWeb", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<JsonNode> streamGeofencesFromWeb(Model model) throws JsonProcessingException {
       
            return geometryService.getGeometriesFromHttp();       
    }
	
	@RequestMapping(value="/streamPositionsFromOwnTracks", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<JsonNode> streamPositionsFromOwnTracks(@RequestParam String userName, @RequestParam String deviceId, Model model) throws JsonProcessingException {
       
            return geometryService.getPositionFromOwnTracks(userName, deviceId);      
    }
		
}
