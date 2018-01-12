package io.sapl.benchmark;

import lombok.Value;

@Value
public class XlsRecord {

    private int number;
    private String name;
    private double duration;
    private String request;
    private String response;
}
