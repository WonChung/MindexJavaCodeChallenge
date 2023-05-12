package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String employeeUrl;

    private String employeeIdUrl;

    private String compensationUrl;

    private String compensationIdUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String JOHN_LENNON_EMPLOYEE_ID = "16a596ae-edd3-4847-99fe-c4518e82c86f";

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";

        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateReadWithNewEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create Employee first
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        testEmployee.setEmployeeId(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        Compensation testCompensation = Compensation.builder()
                .employee(testEmployee)
                .salary(BigDecimal.valueOf(100000))
                .effectiveDate(LocalDate.of(2023, 5, 12))
                .build();

        // Compensation create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation,
                Compensation.class).getBody();

        assertCompensationEquivalence(testCompensation, createdCompensation);


        // Compensation read checks
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class,
                createdCompensation.getEmployee().getEmployeeId()).getBody();
        assertCompensationEquivalence(testCompensation, readCompensation);
    }

    @Test
    public void testCreateReadWithExistingEmployee() {

        // Get Employee first
        Employee testEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class,
                JOHN_LENNON_EMPLOYEE_ID).getBody();

        Compensation testCompensation = Compensation.builder()
                .employee(testEmployee)
                .salary(BigDecimal.valueOf(100000))
                .effectiveDate(LocalDate.of(2023, 5, 12))
                .build();

        // Compensation create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation,
                Compensation.class).getBody();

        assertCompensationEquivalence(testCompensation, createdCompensation);

        // Compensation read checks
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class,
                createdCompensation.getEmployee().getEmployeeId()).getBody();
        assertCompensationEquivalence(testCompensation, readCompensation);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
