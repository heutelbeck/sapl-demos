package io.sapl.demo.pil;

import java.io.Serializable;

import lombok.Data;

@Data
class PilPaxInf implements Serializable {
    private int fMax;
    private int fAct;
    private int cMax;
    private int cAct;
    private int eMax;
    private int eAct;
    private int yMax;
    private int yAct;
}
