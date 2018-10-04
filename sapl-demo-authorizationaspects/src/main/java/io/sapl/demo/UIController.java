package io.sapl.demo;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.spring.SAPLAuthorizator;
import io.sapl.spring.annotation.PdpAuthorize;

@Controller
public class UIController {

	private static final String REDIRECT_PROFILES = "redirect:profiles";
	private static final String UPDATE = "update";

	@Autowired
	private SAPLAuthorizator sapl;

	private PatientRepo patientenRepo;

	@Autowired
	public UIController(SAPLAuthorizator sapl, PatientRepo patientenRepo) {
		this.sapl = sapl;
		this.patientenRepo = patientenRepo;
	}

	@GetMapping("/profiles")
	@PdpAuthorize(subject = "user", action = "getProfiles", resource = "profiles")
	public String profileList(HttpServletRequest request, Model model, Authentication authentication) {
		model.addAttribute("profiles", patientenRepo.findAll());
		model.addAttribute("createPermission", sapl.authorize(authentication, RequestMethod.POST, request));
		return "profiles";
	}

	@PdpAuthorize
	@PostMapping("/profiles")
	public String createProfile(HttpServletRequest request, @ModelAttribute(value = "newPatient") Patient newPatient) {
		if (patientenRepo.existsById(newPatient.getId())) {
			throw new IllegalArgumentException("Profile at this Id already exists");
		}
		patientenRepo.save(newPatient);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/profiles/new")
	@PdpAuthorize(action = "viewProfileCreationForm", resource = "/profiles/new")
	public String linkNew(Model model) {
		Patient newPatient = new Patient();
		model.addAttribute("newPatient", newPatient);
		return "newPatient";
	}

	@PdpAuthorize
	@GetMapping("/patient")
	public String loadProfile(@RequestParam("id") int id, Model model, Authentication authentication) {
		Patient patient = patientenRepo.findById(id).orElse(null);
		if (patient == null) {
			throw new IllegalArgumentException();
		}

		model.addAttribute("patient", patient);

		model.addAttribute("viewDiagnosisPermission", sapl.authorize(authentication, "readDiagnosis", patient));
		model.addAttribute("viewHRNPermission", sapl.authorize(authentication, "read", "HRN"));
		model.addAttribute("updatePermission", sapl.authorize(authentication, RequestMethod.PUT, "/patient"));
		model.addAttribute("deletePermission", sapl.authorize(authentication, RequestMethod.DELETE, "/patient"));
		model.addAttribute("viewRoomNumberPermission", sapl.authorize(authentication, "viewRoomNumber", patient));

		boolean permissionBlackenedHRN = sapl.authorize(authentication, "getBlackenAndObligation", "anything");
		model.addAttribute("permissionBlackenedHRN", permissionBlackenedHRN);

		if (permissionBlackenedHRN) {
			String hRN = patient.getHealthRecordNumber();
			Response response = sapl.getResponse(authentication, "getBlackenAndObligation", hRN);
			model.addAttribute("blackenedHRN", response.getResource().get().asText());
		}
		return "patient";
	}

	@PdpAuthorize
	@DeleteMapping("/patient")
	public String delete(@RequestParam("id") int id) {
		patientenRepo.deleteById(id);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/patient/{id}/update")
	@PdpAuthorize(action = "viewPatientUpdateForm", resource = "/patient/id/update")
	public String linkUpdate(@PathVariable int id, Model model, Authentication authentication) {

		Patient patient = patientenRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Patient not found for id " + id));
		model.addAttribute("updatePatient", patient);

		model.addAttribute("updateDiagnosisPermission", sapl.authorize(authentication, "updateDiagnosis", patient));
		model.addAttribute("updateHRNPermission", sapl.authorize(authentication, UPDATE, "HRN"));
		model.addAttribute("updateDoctorPermission", sapl.authorize(authentication, UPDATE, "doctor"));
		model.addAttribute("updateNursePermission", sapl.authorize(authentication, UPDATE, "nurse"));
		return "updatePatient";
	}

	@PdpAuthorize
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
