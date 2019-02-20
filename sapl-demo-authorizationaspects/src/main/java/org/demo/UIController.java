package org.demo;

import javax.servlet.http.HttpServletRequest;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.sapl.spring.PolicyEnforcementPoint;
import io.sapl.spring.annotation.EnforcePolicies;
import io.sapl.spring.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UIController {

	private static final String REDIRECT_PATIENTS = "redirect:patients";
	private static final String UPDATE = "update";

	private final PolicyEnforcementPoint pep;
	private final SecurePatientRepository patientenRepo;

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public static class ResourceNotFoundException extends RuntimeException {
	}

	@EnforcePolicies
	@GetMapping("/patients")
	public String getPatients(HttpServletRequest request, Model model, Authentication authentication) {
		LOGGER.info("Entering: {}", Thread.currentThread().getStackTrace()[1].getMethodName());
		model.addAttribute("patients", patientenRepo.findAll());
		model.addAttribute("createPermitted", pep.enforce(authentication, "accessCreationButton", "ui:view:patients"));
		Patient patient = patientenRepo.findById(1);
		LOGGER.info("patient: ", patient);
		LOGGER.info("diagnosis: ", patient.getDiagnosis());
		return "patients";
	}

	@EnforcePolicies
	@PostMapping("/patients")
	public String createPatient(@ModelAttribute(value = "newPatient") Patient newPatient) {
		if (patientenRepo.existsById(newPatient.getId())) {
			throw new IllegalArgumentException("Patient with this Id already exists");
		}
		patientenRepo.save(newPatient);
		return REDIRECT_PATIENTS;
	}

	@GetMapping("/patients/new")
	@EnforcePolicies(action = "viewPatientCreationForm", resource = "/patients/new")
	public String linkNew(Model model) {
		Patient newPatient = new Patient();
		model.addAttribute("newPatient", newPatient);
		return "newPatient";
	}

	@EnforcePolicies
	@GetMapping("/patients/{id}")
	public String getPatient(@PathVariable int id, Model model, Authentication authentication) {
		Patient patient = patientenRepo.findById(id);
		if (patient == null) {
			throw new ResourceNotFoundException();
		}

		model.addAttribute("patient", patient);

//		model.addAttribute("viewDiagnosisPermission", pep.enforce(authentication, "readDiagnosis", patient));
//		model.addAttribute("viewHRNPermission", pep.enforce(authentication, "readHealthRecordNumber", patient));
//		model.addAttribute("viewRoomNumberPermission", pep.enforce(authentication, "viewRoomNumber", patient));
//		model.addAttribute("updatePermission", pep.enforce(authentication, "accessUpdateButton", "ui:view:patient"));
//		model.addAttribute("deletePermission", pep.enforce(authentication, "accessDeleteButton", "ui:view:patient"));
//		boolean permissionBlackenedHRN = pep.enforce(authentication, "getBlackenAndObligation", "anything");
//		model.addAttribute("permissionBlackenedHRN", permissionBlackenedHRN);
//		if (permissionBlackenedHRN) {
//			String hRN = patient.getHealthRecordNumber();
//			Response response = sapl.getResponse(authentication, "getBlackenAndObligation", hRN);
//			model.addAttribute("blackenedHRN", response.getResource().get().asText());
//		}
		return "patient";
	}

	@EnforcePolicies
	@DeleteMapping("/patients/{id}")
	public String deletePatient(@PathVariable int id) {
		patientenRepo.deleteById(id);
		return REDIRECT_PATIENTS;
	}

	@GetMapping("/patients/{id}/update")
	@EnforcePolicies(action = "viewPatientUpdateForm")
	public String linkUpdate(@Resource @PathVariable int id, Model model, Authentication authentication) {
		Patient patient = patientenRepo.findById(id);
		if (patient == null) {
			throw new ResourceNotFoundException();
		}
		model.addAttribute("updatePatient", patient);
		model.addAttribute("updateDiagnosisPermission", pep.enforce(authentication, "updateDiagnosis", patient));
		model.addAttribute("updateHRNPermission", pep.enforce(authentication, UPDATE, "HRN"));
		model.addAttribute("updateDoctorPermission", pep.enforce(authentication, UPDATE, "doctor"));
		model.addAttribute("updateNursePermission", pep.enforce(authentication, UPDATE, "nurse"));
		return "updatePatient";
	}

	@EnforcePolicies
	@PutMapping("/patient")
	public String updatePatient(@ModelAttribute("updatePatient") Patient updatePatient, Authentication authentication) {
		if (!patientenRepo.existsById(updatePatient.getId())) {
			throw new IllegalArgumentException("not found");
		}

		Patient savePatient = patientenRepo.findById(updatePatient.getId());
		if (savePatient == null) {
			throw new ResourceNotFoundException();
		}
		savePatient.setName(updatePatient.getName());
		if (pep.enforce(authentication, "updateDiagnosis", updatePatient)) {
			savePatient.setDiagnosis(updatePatient.getDiagnosis());
		}
		if (pep.enforce(authentication, UPDATE, "HRN")) {
			savePatient.setHealthRecordNumber(updatePatient.getHealthRecordNumber());
		}
		savePatient.setPhoneNumber(updatePatient.getPhoneNumber());
		if (pep.enforce(authentication, UPDATE, "doctor")) {
			savePatient.setAttendingDoctor(updatePatient.getAttendingDoctor());
		}
		if (pep.enforce(authentication, UPDATE, "nurse")) {
			savePatient.setAttendingNurse(updatePatient.getAttendingNurse());
		}

		patientenRepo.save(savePatient);

		return REDIRECT_PATIENTS;
	}

}
