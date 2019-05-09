package io.sapl.demo.geo.domain;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PilPaxInf implements Serializable {

	private static final long serialVersionUID = 1L;

	private int fMax; // max Passengers in First Class

	private int fAct; // act Passengers in First Class

	private int cMax; // max Passengers in Business Class

	private int cAct; // act Passengers in Business Class

	private int eMax; // max Passengers in Premium Economy Class

	private int eAct; // act Passengers in Premium Economy Class

	private int yMax; // max Passengers in Economy Class

	private int yAct; // act Passengers in Economy Class

}
