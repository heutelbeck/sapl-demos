package io.sapl.demo;

import java.io.Serializable;

import lombok.Data;

@Data
class PilData implements Serializable {
	private static final long serialVersionUID = 1L;

	private PilMetaInf metaData;
	private PilPaxInf paxData;
	private PilPassenger[] passengers;
}
