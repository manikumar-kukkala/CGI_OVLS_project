package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cgi.model.DrivingLicense;

public interface DrivingLicenseRepository extends JpaRepository<DrivingLicense, Long> {
    DrivingLicense licenseNumber(String drivingLicenseNumber);
}
