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
import io.sapl.pep.BlockingSAPLAuthorizer;
import io.sapl.pep.SAPLAuthorizer;

@Controller
public class UIController {

	private static final String REDIRECT_PROFILES = "redirect:profiles";
	private static final String UPDATE = "update";

	private BlockingSAPLAuthorizer sapl;
	private PatientRepo patientenRepo;

	@Autowired
	public UIController(SAPLAuthorizer sapl, PatientRepo patientenRepo) {
		this.sapl = new BlockingSAPLAuthorizer(sapl);
		this.patientenRepo = patientenRepo;
	}

	@GetMapping("/profiles")
	public String profileList(Model model, Authentication authentication, HttpServletRequest request) {
		model.addAttribute("profiles", patientenRepo.findAll());
		model.addAttribute("createPermission", sapl.wouldAuthorize(authentication, RequestMethod.POST, request));
		return "profiles";
	}

	@PostMapping("/profiles")
	public String createProfile(@ModelAttribute(value = "newPatient") Patient newPatient, HttpServletRequest request) {
		if (patientenRepo.existsById(newPatient.getId())) {
			throw new IllegalArgumentException("Profile at this Id already exists");
		}
		patientenRepo.save(newPatient);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/profiles/new")
	public String linkNew(Model model) {
		Patient newPatient = new Patient();
		model.addAttribute("newPatient", newPatient);
		return "newPatient";
	}

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
			model.addAttribute("obligation", response.getObligation().get().findValue("key1").asText());
			model.addAttribute("message", "Congratulations, you have fullfilled the obligation");
		}
		return "patient";
	}

	@DeleteMapping("/patient")
	public String delete(@RequestParam("id") int id) {
		patientenRepo.deleteById(id);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/patient/{id}/update")
	public String linkUpdate(@PathVariable int id, Model model, Authentication authentication) {

		Patient patient = patientenRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Patient not found for id " + id));
		model.addAttribute("updatePatient", patient);

		model.addAttribute("updateDiagnosisPermission", sapl.wouldAuthorize(authentication, "updateDiagnosis", patient));
		model.addAttribute("updateHRNPermission", sapl.wouldAuthorize(authentication, UPDATE, "HRN"));
		model.addAttribute("updateDoctorPermission", sapl.wouldAuthorize(authentication, UPDATE, "doctor"));
		model.addAttribute("updateNursePermission", sapl.wouldAuthorize(authentication, UPDATE, "nurse"));
		return "updatePatient";
	}

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
