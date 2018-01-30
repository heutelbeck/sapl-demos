package io.sapl.demo.pip.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.resource.PatientResource;
import io.sapl.demo.repository.PatientenRepo;
import io.sapl.spring.StandardSAPLAuthorizator;
import io.sapl.spring.marshall.Resource;
import io.sapl.spring.marshall.Subject;
import io.sapl.spring.marshall.action.HttpAction;
import io.sapl.spring.marshall.action.SimpleAction;
import io.sapl.spring.marshall.resource.HttpResource;
import io.sapl.spring.marshall.subject.AuthenticationSubject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UIController {

	private StandardSAPLAuthorizator sapl;

	private PatientenRepo patientenRepo;

	@Autowired
	public UIController(StandardSAPLAuthorizator sapl, PatientenRepo patientenRepo) {
		this.sapl = sapl;
		this.patientenRepo = patientenRepo;
		LOGGER.debug("created instancewith PolicyEnforcementPoint (!= null:{}) and PatientenRepo (!= null:{})",
				sapl != null, patientenRepo != null);
	}

	@GetMapping("/profiles")
	public String profileList(Model model, Authentication authentication, HttpServletRequest request) {
		LOGGER.debug("Entering /profiles");
		model.addAttribute("profiles", patientenRepo.findAll());
		return "profiles";
	}


	@GetMapping("/patient")
	public String loadProfile(@RequestParam("id") int id, Model model, Authentication authentication) {

		Patient patient = patientenRepo.findById(id).orElse(null);
		if (patient == null) {
			throw new IllegalArgumentException();
		}

		model.addAttribute("patient", patient);

		Subject subject = new AuthenticationSubject(authentication);
		Resource patientResource = new PatientResource(patient);
		model.addAttribute("viewRoomNumberPermission",
				sapl.authorize(subject, new SimpleAction("viewRoomNumber"), patientResource));
		return "patient";
	}



}
