package org.demo;

import javax.servlet.http.HttpServletRequest;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.sapl.api.pdp.Response;
import io.sapl.pep.BlockingSAPLAuthorizer;
import io.sapl.spring.annotation.EnforcePolicies;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UIController {

	private static final String REDIRECT_PROFILES = "redirect:profiles";
	private static final String UPDATE = "update";

	private final BlockingSAPLAuthorizer sapl;
	private final PatientRepository patientenRepo;

	/**
	 * This controller is responsible for retrieving the list of patients for an
	 * overview table.
	 * 
	 * This controller contains two applications of the SAPL policy engine.
	 * 
	 * 1. A policy enforcement point for accessing the Model for the view is
	 * established by the @EnforcePolicies annotation. The subject is the current
	 * "user", the action is "getProfiles" and the resource are the "profiles". This
	 * policy enforcement point is automatically invoked whenever the controller is
	 * accessed and before entering this method.
	 * 
	 * 2. While not a directly enforcing a policy, the BlockingSAPLAuthorizer is
	 * used to check if the current user would have the permission to create new
	 * profiles, allowing the view (thymeleaf template) to decide if it would like
	 * to render the button for creating a new patient.
	 * 
	 * @param request        The HTTP request
	 * @param model          The model part of the MVC Setup for the patient list
	 * @param authentication The Authentication of the current user
	 * @return The identifier for this MVC view.
	 */
	@EnforcePolicies
	@GetMapping("/patients")
	public String getPatients(HttpServletRequest request, Model model, Authentication authentication) {
		LOGGER.info("Entering: {}", Thread.currentThread().getStackTrace()[1].getMethodName());
		model.addAttribute("patients", patientenRepo.findAll());
		model.addAttribute("createPermitted", sapl.wouldAuthorize(authentication, RequestMethod.POST, request));
		return "patients";
	}

	@EnforcePolicies
	@PostMapping("/profiles")
	public String createProfile(@ModelAttribute(value = "newPatient") Patient newPatient) {
		if (patientenRepo.existsById(newPatient.getId())) {
			throw new IllegalArgumentException("Patient with this Id already exists");
		}
		patientenRepo.save(newPatient);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/profiles/new")
	@EnforcePolicies(action = "viewProfileCreationForm", resource = "/profiles/new")
	public String linkNew(Model model) {
		Patient newPatient = new Patient();
		model.addAttribute("newPatient", newPatient);
		return "newPatient";
	}

	@EnforcePolicies
	@GetMapping("/patient")
	public String loadProfile(@RequestParam("id") int id, Model model, Authentication authentication) {
		Patient patient = patientenRepo.findById(id).orElse(null);
		if (patient == null) {
			throw new IllegalArgumentException();
		}

		model.addAttribute("patient", patient);

		model.addAttribute("viewDiagnosisPermission", sapl.authorize(authentication, "readDiagnosis", patient));
		model.addAttribute("viewHRNPermission", sapl.authorize(authentication, "read", "HRN"));
		model.addAttribute("viewRoomNumberPermission", sapl.authorize(authentication, "viewRoomNumber", patient));
		model.addAttribute("updatePermission", sapl.wouldAuthorize(authentication, RequestMethod.PUT, "/patient"));
		model.addAttribute("deletePermission", sapl.wouldAuthorize(authentication, RequestMethod.DELETE, "/patient"));

		boolean permissionBlackenedHRN = sapl.wouldAuthorize(authentication, "getBlackenAndObligation", "anything");
		model.addAttribute("permissionBlackenedHRN", permissionBlackenedHRN);

		if (permissionBlackenedHRN) {
			String hRN = patient.getHealthRecordNumber();
			Response response = sapl.getResponse(authentication, "getBlackenAndObligation", hRN);
			model.addAttribute("blackenedHRN", response.getResource().get().asText());
		}
		return "patient";
	}

	@EnforcePolicies
	@DeleteMapping("/patient")
	public String delete(@RequestParam("id") int id) {
		patientenRepo.deleteById(id);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/patient/{id}/update")
	@EnforcePolicies(action = "viewPatientUpdateForm", resource = "/patient/id/update")
	public String linkUpdate(@PathVariable int id, Model model, Authentication authentication) {
		Patient patient = patientenRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Patient not found for id " + id));
		model.addAttribute("updatePatient", patient);
		model.addAttribute("updateDiagnosisPermission",
				sapl.wouldAuthorize(authentication, "updateDiagnosis", patient));
		model.addAttribute("updateHRNPermission", sapl.wouldAuthorize(authentication, UPDATE, "HRN"));
		model.addAttribute("updateDoctorPermission", sapl.wouldAuthorize(authentication, UPDATE, "doctor"));
		model.addAttribute("updateNursePermission", sapl.wouldAuthorize(authentication, UPDATE, "nurse"));
		return "updatePatient";
	}

	@EnforcePolicies
	@PutMapping("/patient")
	public String updatePatient(@ModelAttribute("updatePatient") Patient updatePatient, Authentication authentication) {
		if (!patientenRepo.existsById(updatePatient.getId())) {
			throw new IllegalArgumentException("not found");
		}

		Patient savePatient = patientenRepo.findById(updatePatient.getId()).get();
		savePatient.setName(updatePatient.getName());
		if (sapl.authorize(authentication, "updateDiagnosis", updatePatient)) {
			savePatient.setDiagnosis(updatePatient.getDiagnosis());
		}
		if (sapl.authorize(authentication, UPDATE, "HRN")) {
			savePatient.setHealthRecordNumber(updatePatient.getHealthRecordNumber());
		}
		savePatient.setPhoneNumber(updatePatient.getPhoneNumber());
		if (sapl.authorize(authentication, UPDATE, "doctor")) {
			savePatient.setAttendingDoctor(updatePatient.getAttendingDoctor());
		}
		if (sapl.authorize(authentication, UPDATE, "nurse")) {
			savePatient.setAttendingNurse(updatePatient.getAttendingNurse());
		}

		patientenRepo.save(savePatient);

		return REDIRECT_PROFILES;
	}

}
