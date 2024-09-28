package io.sapl.geo.demo.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.sapl.geo.demo.domain.GeoUser;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/")
public class LoginController {



	
	@RequestMapping
	public ModelAndView index() {
		var mav = new ModelAndView("index");
		mav.addObject("message", "Hallo");
		
		return mav;
	}


	
	@RequestMapping(value="/mainView")
    public String login(@AuthenticationPrincipal GeoUser geoUser, Model model) {
        
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var name = authentication.getName();
		
            model.addAttribute("message", "Hallo " + name);
            model.addAttribute("name", name);
            model.addAttribute("geoUser", geoUser);
            return "mainView";
    }
	
}
