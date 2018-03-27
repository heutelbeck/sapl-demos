package io.sapl.demo.geo.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class PilData implements Serializable {
	private static final long serialVersionUID = 1L;

	private PilMetaInf metaData;
	private PilPaxInf paxData;
	private PilPassenger[] passengers;
}
