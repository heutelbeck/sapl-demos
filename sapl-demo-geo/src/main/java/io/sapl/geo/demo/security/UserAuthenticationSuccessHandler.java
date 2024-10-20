package io.sapl.geo.demo.security;

import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import io.sapl.geo.demo.domain.GeoUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        var user  =(GeoUser)authentication.getPrincipal(); 
        if(user.getUsername().equals("alice")) {
        	user.setPositions(new Coordinate[] {new Coordinate(48.856613, 2.352222), new Coordinate(49.051864, 2.657535), new Coordinate(49.283110, 4.009421)});
        } else if(user.getUsername().equals("bob")) {        	
        	user.setPositions(new Coordinate[] {new Coordinate(51.34533, 7.4057), new Coordinate(51.37442, 7.49254)});
        }
        response.sendRedirect("/mainView"); 
    }
}