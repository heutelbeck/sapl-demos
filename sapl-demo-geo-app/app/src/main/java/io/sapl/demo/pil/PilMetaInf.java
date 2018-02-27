package io.sapl.demo.pil;

import java.io.Serializable;

import lombok.Data;

@Data
class PilMetaInf implements Serializable {
    private String depAp;
    private String arrAp;
    private String acType;
    private String fltNo;
    private String date;
    private int classification;
}
