package com.mindex.challenge.data;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class Employee {

    private String employeeId;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String position;

    @NotNull
    private String department;

    private List<Employee> directReports;
}
