package com.bul.satellites.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
//@RequiredArgsConstructor
@Builder
@AllArgsConstructor
//@ToString(of = {"id", "email", "username", "password"})
@ToString
public class Output {
    private String base;
    private String access;
    private Instant start;
    private Instant end;
    private String duration;
    private String satelliteName;
    private String volume;

    public Output() {
       ;
    }
}
