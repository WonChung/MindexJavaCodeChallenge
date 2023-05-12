package com.mindex.challenge.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

// I prefer using the lombok annotations
@Getter
@Setter
@Builder
public class Compensation {

    @Valid
    private Employee employee;

    // When it comes to money, BigDecimal is more appropriate
    @NotNull
    private BigDecimal salary;

    // When it comes to date, LocalDate is recommended according to research
    @NotNull
    private LocalDate effectiveDate;

}
