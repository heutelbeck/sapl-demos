package io.sapl.geo.demo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoveRequest{

	@JsonProperty("deviceId")
    private String deviceId;

	@JsonProperty("username")
    private String username;
	
    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;
}