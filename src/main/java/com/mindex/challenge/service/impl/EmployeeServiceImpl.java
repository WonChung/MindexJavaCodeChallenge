package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// I prefer using the Slf4j annotation
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        log.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        log.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Employee Does Not Exist - Invalid employeeId: " + id);
        }

        return employee;
    }

    // read after updating results in error with the original code - we can discuss this
    @Override
    public Employee update(Employee employee) {
        log.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        log.debug("Get reporting structure for employee with id [{}]", id);

        ReportingStructure reportingStructure = ReportingStructure.builder().build();

        try {
            Employee employee = read(id);
            reportingStructure.setEmployee(employee);
            reportingStructure.setNumberOfReports(returnNumberOfReports(employee));
        } catch (Exception e) {
            log.error("Error getting the reporting structure [{}]", e);

            throw new RuntimeException("Error getting the reporting structure " + e.getMessage());
        }

        return reportingStructure;
    }

    /*
     * Recursive method to visit each direct employee
     * Time Complexity is O(n), or the number of nodes
     * Space Complexity is O(log n), or the space for the recursion stack
     */
    private int returnNumberOfReports(Employee employee){
        List<Employee> directReports = employee.getDirectReports();
        if(directReports == null || directReports.isEmpty()) {
            return 0;
        }
        int count = 0;
        for(Employee directReport : directReports) {
            count += 1 + returnNumberOfReports(read(directReport.getEmployeeId()));
        }
        return count;
    }
}
