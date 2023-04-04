package io.sapl.vaadindemo.pizzaform;

import java.time.LocalTime;

import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Repository
public class PizzaOrder {

	// order details
	@Min(0)
	@Max(99)
	private Integer cheese = 0;

	@Min(0)
	@Max(99)
	private Integer veggie = 0;

	@Min(0)
	@Max(99)
	private Integer pepperoni = 0;

	@Email
	@NotBlank
	private String email;

	@NotNull
	private LocalTime time;

	private Integer beer = 0;
}
