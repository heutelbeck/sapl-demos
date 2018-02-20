package io.sapl.demo;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class PilMetaInf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String depAp;
	private String arrAp;
	private String acType;
	private String fltNo;
	private String date;
	private int classification;
}
