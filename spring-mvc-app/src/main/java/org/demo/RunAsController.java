package org.demo;

import org.demo.config.MethodSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.sapl.spring.runas.RunAsPolicyEngine;

@Controller
@RequestMapping("/runas")
class RunAsController {
	@Autowired
	MethodSecurityConfig conf;
	
	@Secured({"ROLE_VISITOR"})
	@ResponseBody
    @RequestMapping
	@RunAsPolicyEngine
    public String tryRunAs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Current User Authorities inside this RunAS method only " + 
          auth.getAuthorities().toString()+" ---- "+conf.runAsManager().getClass();
    }
 
}