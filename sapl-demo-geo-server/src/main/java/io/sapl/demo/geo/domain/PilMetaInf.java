package io.sapl.demo.geo.domain;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PilMetaInf implements Serializable {

	private static final long serialVersionUID = 1L;

	private String depAp;

	private String arrAp;

	private String acType;

	private String fltNo;

	private String date;

	private int classification;

}
