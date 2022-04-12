package io.sapl.vaadindemo.pizzaform;

import java.time.LocalTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;

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
