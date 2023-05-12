package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeIdReportingStructure;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String JOHN_LENNON_EMPLOYEE_ID = "16a596ae-edd3-4847-99fe-c4518e82c86f";

    private static final String PAUL_MCCARTNEY_EMPLOYEE_ID = "b7839309-3348-463b-a7e3-5de1c168beb3";

    private static final String RINGO_STARR_EMPLOYEE_ID = "03aa1462-ffa9-4978-901b-7c001562cf6f";

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeIdReportingStructure = "http://localhost:" + port + "/employee/{id}/reporting-structure";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test
    public void testReturnNumberOfReportsForNewEmployeeDirectReports() {
        // Get an existing employee first
        Employee john = restTemplate.getForEntity(employeeIdUrl, Employee.class,
                JOHN_LENNON_EMPLOYEE_ID).getBody();

        List<Employee> directReports = new ArrayList<>();
        directReports.add(john);

        Employee testEmployee = new Employee();
        testEmployee.setFirstName("Benjamin");
        testEmployee.setLastName("Sisko");
        testEmployee.setDepartment("Command");
        testEmployee.setPosition("Captain");
        testEmployee.setDirectReports(directReports);

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        // Get reporting structure
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdReportingStructure,
                ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);

        Employee employee = reportingStructure.getEmployee();
        assertEquals(createdEmployee.getEmployeeId(), employee.getEmployeeId());
        assertEquals("Benjamin", employee.getFirstName());
        assertEquals("Sisko", employee.getLastName());
        assertEquals("Captain", employee.getPosition());
        assertEquals("Command", employee.getDepartment());

        assertEquals(5, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testReturnNumberOfReportsForNewEmployeeWithNoDirectReports() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        // Get reporting structure
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdReportingStructure,
                ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);

        Employee employee = reportingStructure.getEmployee();
        assertEquals(createdEmployee.getEmployeeId(), employee.getEmployeeId());
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("Developer", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());

        assertEquals(0, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testReturnNumberOfReportsForJohnLennon() {
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdReportingStructure,
                ReportingStructure.class, JOHN_LENNON_EMPLOYEE_ID).getBody();

        assertNotNull(reportingStructure);

        Employee employee = reportingStructure.getEmployee();
        assertEquals(JOHN_LENNON_EMPLOYEE_ID, employee.getEmployeeId());
        assertEquals("John", employee.getFirstName());
        assertEquals("Lennon", employee.getLastName());
        assertEquals("Development Manager", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());

        assertEquals(4, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testReturnNumberOfReportsForPaulMcCartney() {
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdReportingStructure,
                ReportingStructure.class, PAUL_MCCARTNEY_EMPLOYEE_ID).getBody();

        assertNotNull(reportingStructure);

        Employee employee = reportingStructure.getEmployee();
        assertEquals(PAUL_MCCARTNEY_EMPLOYEE_ID, employee.getEmployeeId());
        assertEquals("Paul", employee.getFirstName());
        assertEquals("McCartney", employee.getLastName());
        assertEquals("Developer I", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());

        assertEquals(0, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testReturnNumberOfReportsForRingoStarr() {
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdReportingStructure,
                ReportingStructure.class, RINGO_STARR_EMPLOYEE_ID).getBody();

        assertNotNull(reportingStructure);

        Employee employee = reportingStructure.getEmployee();
        assertEquals(RINGO_STARR_EMPLOYEE_ID, employee.getEmployeeId());
        assertEquals("Ringo", employee.getFirstName());
        assertEquals("Starr", employee.getLastName());
        assertEquals("Developer V", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());

        assertEquals(2, reportingStructure.getNumberOfReports());
    }
}
