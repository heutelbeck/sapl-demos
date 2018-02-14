package io.sapl.sapldemoauthorizationaspects;

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
import io.sapl.demo.domain.resource.PatientResource;
import io.sapl.demo.repository.PatientenRepo;
import io.sapl.spring.SAPLAuthorizator;
import io.sapl.spring.annotation.PdpAuthorize;
import io.sapl.spring.annotation.PdpAuthorizeHttp;
import io.sapl.spring.marshall.Resource;
import io.sapl.spring.marshall.Subject;
import io.sapl.spring.marshall.action.HttpAction;
import io.sapl.spring.marshall.action.SimpleAction;
import io.sapl.spring.marshall.resource.HttpResource;
import io.sapl.spring.marshall.resource.StringResource;
import io.sapl.spring.marshall.subject.AuthenticationSubject;

@Controller
public class UIController {

	private static final String REDIRECT_PROFILES = "redirect:profiles";
	private static final String UPDATE = "update";

	@Autowired
	private SAPLAuthorizator pep;

	private PatientenRepo patientenRepo;

	@Autowired
	public UIController(SAPLAuthorizator pep, PatientenRepo patientenRepo) {
		this.pep = pep;
		this.patientenRepo = patientenRepo;
	}

	@PdpAuthorizeHttp
	@GetMapping("/profiles")
	public String profileList(HttpServletRequest request, Model model, Authentication authentication) {
		model.addAttribute("profiles", patientenRepo.findAll());
		model.addAttribute("createPermission", pep.authorize(new AuthenticationSubject(authentication),
				new HttpAction(RequestMethod.POST), new HttpResource(request)));
		return "profiles";
	}

	@PdpAuthorizeHttp
	@PostMapping("/profiles")
	public String createProfile(HttpServletRequest request, @ModelAttribute(value = "newPatient") Patient newPatient) {
		if (patientenRepo.existsById(newPatient.getId())) {
			throw new IllegalArgumentException("Profile at this Id already exists");
		}
		patientenRepo.save(newPatient);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/profiles/new")
	@PdpAuthorize(action = "GET", resource = "/profiles/new")
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

		Subject subject = new AuthenticationSubject(authentication);
		Resource patientResource = new PatientResource(patient);
		model.addAttribute("viewDiagnosisPermission",
				pep.authorize(subject, new SimpleAction("readDiagnosis"), patientResource));
		model.addAttribute("viewHRNPermission",
				pep.authorize(subject, new SimpleAction("read"), new StringResource("HRN")));
		model.addAttribute("updatePermission",
				pep.authorize(subject, new HttpAction(RequestMethod.PUT), new HttpResource("/patient")));
		model.addAttribute("deletePermission",
				pep.authorize(subject, new HttpAction(RequestMethod.DELETE), new HttpResource("/patient")));
		model.addAttribute("viewRoomNumberPermission",
				pep.authorize(subject, new SimpleAction("viewRoomNumber"), patientResource));

		boolean permissionBlackenedHRN = pep.authorize(subject, new SimpleAction("getBlackenAndObligation"),
				new StringResource("anything"));
		model.addAttribute("permissionBlackenedHRN", permissionBlackenedHRN);

		if (permissionBlackenedHRN) {
			String hRN = patient.getHealthRecordNumber();
			Response response = pep.getResponse(subject, new SimpleAction("getBlackenAndObligation"),
					new StringResource(hRN));
			model.addAttribute("blackenedHRN", response.getResource().get().asText()); // use
																						// only
																						// together
																						// with
																						// "permissionBlackenedHRN":
																						// only
																						// for
																						// NURSE
			model.addAttribute("obligation", response.getObligation().get().findValue("key1").asText()); // findValue("key1").asText());
																											// //
																											// use
																											// only
																											// together
																											// with
																											// "permissionBlackenedHRN":
																											// only
																											// for
																											// NURSE
			model.addAttribute("message", "Congratulations, you have fullfilled the obligation");
		}
		return "patient";
	}

	@PdpAuthorizeHttp
	@DeleteMapping("/patient")
	public String delete(HttpServletRequest request, @RequestParam("id") int id) {
		patientenRepo.deleteById(id);
		return REDIRECT_PROFILES;
	}

	@GetMapping("/patient/{id}/update")
	@PdpAuthorize(action = "GET", resource = "/patient/id/update")
	public String linkUpdate(@PathVariable int id, Model model, Authentication authentication) {

		Patient patient = patientenRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Patient not found for id " + id));
		model.addAttribute("updatePatient", patient);

		Subject subject = new AuthenticationSubject(authentication);
		Resource patientResource = new PatientResource(patient);
		model.addAttribute("updateDiagnosisPermission",
				pep.authorize(subject, new SimpleAction("updateDiagnosis"), patientResource));
		model.addAttribute("updateHRNPermission",
				pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("HRN")));
		model.addAttribute("updateDoctorPermission",
				pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("doctor")));
		model.addAttribute("updateNursePermission",
				pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("nurse")));
		return "updatePatient";
	}

	@PdpAuthorizeHttp
	@PutMapping("/patient")
	public String updatePatient(HttpServletRequest request, @ModelAttribute("updatePatient") Patient updatePatient,
			Authentication authentication) {
		if (!patientenRepo.existsById(updatePatient.getId())) {
			throw new IllegalArgumentException("not found");
		}

		Subject subject = new AuthenticationSubject(authentication);
		Resource patientResource = new PatientResource(updatePatient);

		Patient savePatient = patientenRepo.findById(updatePatient.getId()).get();
		savePatient.setName(updatePatient.getName());
		if (pep.authorize(subject, new SimpleAction("updateDiagnosis"), patientResource)) {
			savePatient.setDiagnosis(updatePatient.getDiagnosis());
		}
		if (pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("HRN"))) {
			savePatient.setHealthRecordNumber(updatePatient.getHealthRecordNumber());
		}
		savePatient.setPhoneNumber(updatePatient.getPhoneNumber());
		if (pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("doctor"))) {
			savePatient.setAttendingDoctor(updatePatient.getAttendingDoctor());
		}
		if (pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("nurse"))) {
			savePatient.setAttendingNurse(updatePatient.getAttendingNurse());
		}

		patientenRepo.save(savePatient);

		return REDIRECT_PROFILES;
	}

}
