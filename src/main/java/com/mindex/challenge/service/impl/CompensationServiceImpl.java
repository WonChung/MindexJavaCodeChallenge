package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompensationServiceImpl implements CompensationService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompensationRepository compensationRepository;

    // Similar logic to that of EmployeeServiceImpl but without creating a new employeeId
    @Override
    public Compensation create(Compensation compensation) {
        log.debug("Creating compensation [{}]", compensation);

        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Compensation read(String id) {
        log.debug("Reading compensation with id [{}]", id);

        Compensation compensation = null;

        try {
            Employee employee = employeeService.read(id);
            compensation = compensationRepository.findByEmployee(employee);
        } catch (Exception e) {
            log.error("Error getting the compensation [{}]", e);

            throw new RuntimeException("Error getting the compensation - " + e.getMessage());
        }

        if (compensation == null) {
            throw new RuntimeException("Compensation Does Not Exist - Invalid employeeId: " + id);
        }

        return compensation;
    }
}
