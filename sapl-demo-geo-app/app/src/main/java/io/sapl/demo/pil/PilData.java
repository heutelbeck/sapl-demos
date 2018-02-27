package io.sapl.demo.pil;

import java.io.Serializable;

import lombok.Data;

@Data
class PilData implements Serializable {
    private PilMetaInf metaData;
    private PilPaxInf paxData;
    private PilPassenger[] passengers;
}
