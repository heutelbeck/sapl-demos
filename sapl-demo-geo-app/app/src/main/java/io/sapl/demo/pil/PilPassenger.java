package io.sapl.demo.pil;

import java.io.Serializable;

import lombok.Data;

@Data
class PilPassenger implements Serializable {
    private String seat;
    private String name;
    private String gender;
    private String bdate;
    private String special;
}
