package io.sapl.vaadindemo.views;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

@Data
public class SampleFormData {
	@NotBlank(message = "Please specify First name.")
	private String firstName;

	@NotBlank(message = "Please specify Last name.")
	private String lastName;

	@NotNull(message = "Birthday must not be empty.")
	@Past
	private LocalDate birthday;

	@NotBlank(message = "Please specify your email.")
	@Email(message = "${validatedValue} is not a valid email.")
	private String email;

	@Min(20)
	@NotNull(message = "Amount must not be empty.")
	private Double amount;
}
