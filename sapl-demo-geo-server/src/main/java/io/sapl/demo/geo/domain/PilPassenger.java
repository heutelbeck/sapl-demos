package io.sapl.demo.geo.domain;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PilPassenger implements Serializable {

	private static final long serialVersionUID = 1L;

	private String seat;

	private String name;

	private String gender;

	private String bdate;

	private String special;

}
