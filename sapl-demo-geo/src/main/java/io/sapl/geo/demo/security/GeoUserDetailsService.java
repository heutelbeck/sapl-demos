package io.sapl.geo.demo.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.sapl.geo.demo.domain.GeoUser;
import io.sapl.geo.demo.domain.GeoUserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GeoUserDetailsService implements UserDetailsService {

	private final GeoUserRepository geoUserRepository;
	
	Map<String, GeoUser> users = new HashMap<>();


    public void load(GeoUser user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return geoUserRepository.findByUsername(username)
                .map(geoUser -> new GeoUser(geoUser.getUsername(), geoUser.getPassword(), geoUser.getGeoTracker(), geoUser.getTrackerDeviceId(), geoUser.getUniqueDeviceId(), geoUser.getNextLat(), geoUser.getNextLon()))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .block();
        
    }
	
}
