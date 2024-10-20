package io.sapl.geo.demo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Table("GeoUser")
@RequiredArgsConstructor
@ToString(callSuper = true)
public class GeoUser implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column("Id")
	private int id;
	
	@Column("UserName")
	private final String username;
	
	@Column("Password")
	private final String password;
	
	@Column("GeoTracker")
	private final GeoTracker geoTracker;
	
	@Column("TrackerDeviceId")
	private final String trackerDeviceId;
	
	@Column("UniqueDeviceId")
	private final String uniqueDeviceId;
		
	@Setter
	@Transient
	private Coordinate[] positions = new Coordinate[] {};
	
	@Transient
	private int currentPositionIndex = -1; //getNextCoordinate() ist called by webUi automatically
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("demouser"));
        return authorities;
	}
	
	public Coordinate getNextCoordinate() {

		if (currentPositionIndex == -1) {
			currentPositionIndex = 0; 
			return null;//getNextCoordinate() ist called by webUi automatically
		} else {
			var currentPosition = currentPositionIndex;
			currentPositionIndex = (currentPositionIndex + 1) % positions.length; 
			return positions[currentPosition]; 
		}		
	}
}
