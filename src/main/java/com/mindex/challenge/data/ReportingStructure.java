package com.mindex.challenge.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// I prefer using the lombok annotations
@Getter
@Setter
@Builder
public class ReportingStructure {

    private Employee employee;

    private int numberOfReports;
}
