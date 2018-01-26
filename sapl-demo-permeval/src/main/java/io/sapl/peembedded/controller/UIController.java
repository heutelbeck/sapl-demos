package io.sapl.peembedded.controller;

import javax.servlet.http.HttpServletRequest;

import io.sapl.api.pdp.Response;
import io.sapl.demo.domain.Patient;
import io.sapl.demo.repository.PatientenRepo;
import io.sapl.spring.StandardSAPLAuthorizator;
import io.sapl.spring.marshall.Resource;
import io.sapl.spring.marshall.Subject;
import io.sapl.spring.marshall.action.HttpAction;
import io.sapl.spring.marshall.action.SimpleAction;
import io.sapl.spring.marshall.resource.HttpResource;
import io.sapl.spring.marshall.resource.StringResource;
import io.sapl.spring.marshall.subject.AuthenticationSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UIController {
	
	private static final String REDIRECT_PROFILES = "redirect:profiles";
	private static final String UPDATE = "update";
	
	private StandardSAPLAuthorizator pep;

	private PatientenRepo patientenRepo;

	
	
	@Autowired
	public UIController(StandardSAPLAuthorizator pep, PatientenRepo patientenRepo) {
		this.pep = pep;
		this.patientenRepo = patientenRepo; 
		LOGGER.debug("created instancewith StandardSAPLAuthorizator (!= null:{}) and PatientenRepo (!= null:{})", pep != null , patientenRepo != null);
	}
	
	
	
	@GetMapping("/profiles")
	public String profileList(Model model, Authentication authentication, HttpServletRequest request){
		LOGGER.debug("Entering /profiles");
		model.addAttribute("profiles", patientenRepo.findAll());
		model.addAttribute("createPermission", pep.authorize(new AuthenticationSubject(authentication),
				new HttpAction(RequestMethod.POST),
				new HttpResource(request)));
		return "profiles";
	}
	
	@PostMapping("/profiles")
	@PreAuthorize("hasPermission(#request, #request)") //using SaplPolicies: DOCTOR
	public String createProfile(@ModelAttribute(value="newPatient") Patient newPatient, HttpServletRequest request){
		if(patientenRepo.existsById(newPatient.getId())){
			throw new IllegalArgumentException("Profile at this Id already exists");
		}
		patientenRepo.save(newPatient);
		return REDIRECT_PROFILES;
	}
	
		
	@GetMapping("/profiles/new")
	public String linkNew(Model model) {
		Patient newPatient = new Patient();
		model.addAttribute("newPatient" , newPatient);
		return "newPatient";
	}
	
	@GetMapping("/patient")
	public String loadProfile(@RequestParam("id") int id, Model model, Authentication authentication){
		
		Patient patient = patientenRepo.findById(id).orElse(null);
		if (patient == null){
			throw new IllegalArgumentException();
		}

		model.addAttribute("patient", patient);

		Subject subject = new AuthenticationSubject(authentication);
		Resource patientResource = new PatientResource(patient);
		model.addAttribute("viewDiagnosisPermission", pep.authorize(subject, new SimpleAction("readDiagnosis"), patientResource));
		model.addAttribute("viewHRNPermission", pep.authorize(subject, new SimpleAction("read"), new StringResource("HRN")));
		model.addAttribute("updatePermission", pep.authorize(subject,new HttpAction(RequestMethod.PUT), new HttpResource("/patient") ));
		model.addAttribute("deletePermission", pep.authorize(subject,new HttpAction(RequestMethod.DELETE), new HttpResource("/patient")));
		// next line only works if PatientPIP is integrated into SAPL Policies
		model.addAttribute("viewRoomNumberPermission", pep.authorize(subject,new SimpleAction("viewRoomNumber"), patientResource));
		
		
		boolean permissionBlackenedHRN = pep.authorize(subject, new SimpleAction("getBlackenAndObligation"),new StringResource("anything"));
		model.addAttribute("permissionBlackenedHRN", permissionBlackenedHRN);
		
		if (permissionBlackenedHRN) {
			String hRN = patient.getHealthRecordNumber();
			Response response = pep.getResponse(subject,  new SimpleAction("getBlackenAndObligation"), new StringResource(hRN));
			model.addAttribute("blackenedHRN" , response.getResource().get().asText() ); // use only together with "permissionBlackenedHRN": only for NURSE
			model.addAttribute("obligation",response.getObligation().get().findValue("key1").asText());  //findValue("key1").asText()); // use only together with "permissionBlackenedHRN": only for NURSE
			model.addAttribute("message", "Congratulations, you have fullfilled the obligation");
		}
		return "patient";
	}
	
	
	@DeleteMapping("/patient")
	@PreAuthorize("hasPermission(#request, #request)") //using SaplPolicies
	public String delete(@RequestParam("id") int id, HttpServletRequest request){
		patientenRepo.deleteById(id);
		return REDIRECT_PROFILES;
	}
	
	@GetMapping("/patient/{id}/update")
	public String linkUpdate(@PathVariable int id, Model model, Authentication authentication) {
		
		Patient patient = patientenRepo.findById(id).orElseThrow(() -> new RuntimeException("Patient not found for id " + id));
		model.addAttribute("updatePatient" , patient);

		Subject subject = new AuthenticationSubject(authentication);
		Resource patientResource = new PatientResource(patient);
		model.addAttribute("updateDiagnosisPermission", pep.authorize(subject,new SimpleAction("updateDiagnosis"), patientResource));
		model.addAttribute("updateHRNPermission", pep.authorize(subject,new SimpleAction(UPDATE), new StringResource("HRN")));
		model.addAttribute("updateDoctorPermission", pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("doctor")));
		model.addAttribute("updateNursePermission", pep.authorize(subject, new SimpleAction(UPDATE), new StringResource("nurse")));
		return "updatePatient";
	}
	
	@PutMapping("/patient")
	@PreAuthorize("hasPermission(#request, #request)") //using SaplPolicies
	public String updatePatient(@ModelAttribute ("updatePatient") Patient updatePatient, Authentication authentication, HttpServletRequest request){
		if(!patientenRepo.existsById(updatePatient.getId())){
			throw new IllegalArgumentException("not found");
		}

		Subject subject = new AuthenticationSubject(authentication);
		Resource patientResource = new PatientResource(updatePatient);
		
		Patient savePatient = patientenRepo.findById(updatePatient.getId()).get();
		savePatient.setName(updatePatient.getName());
		if (pep.authorize(subject, new SimpleAction("updateDiagnosis"), patientResource)) {
			savePatient.setDiagnosis(updatePatient.getDiagnosis());
		}
		if (pep.authorize(subject,new SimpleAction(UPDATE), new StringResource("HRN"))) {
			savePatient.setHealthRecordNumber(updatePatient.getHealthRecordNumber());
		}
		savePatient.setPhoneNumber(updatePatient.getPhoneNumber());
		if (pep.authorize(subject,new SimpleAction(UPDATE), new StringResource("doctor"))) {
			savePatient.setAttendingDoctor(updatePatient.getAttendingDoctor());
		}
		if (pep.authorize(subject,new SimpleAction(UPDATE), new StringResource("nurse"))) {
			savePatient.setAttendingNurse(updatePatient.getAttendingNurse());
		}
		
		patientenRepo.save(savePatient);
		
		return REDIRECT_PROFILES;
	}
	
}

