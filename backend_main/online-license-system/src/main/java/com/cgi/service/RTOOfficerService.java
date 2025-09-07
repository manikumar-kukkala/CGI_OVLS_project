package com.cgi.service;

import java.util.List;
import java.util.Map;

import com.cgi.model.Application;
import com.cgi.model.DrivingLicense;
import com.cgi.model.RTOOfficer;

public interface RTOOfficerService {

    boolean officeLogin(RTOOfficer officer);

    List<Application> viewAllPendingApplications();

    List<Application> viewAllRejectedApplications();

    List<Application> viewAllApprovedApplications();

    Application viewApplicationById(String applicationId);

    String checkChallanByVehicleNumber(String vehicleNumber);

    String checkAllChallan();

    Application modifyTestResultById(String applicationId);

    Application generateLearnerLicense(String applicationId);

    DrivingLicense generateDrivingLicense(String applicationId);

    String emailLicense(DrivingLicense license);

    RTOOfficer addOfficer(RTOOfficer officer);

    ////
    List<Application> getAllApplications();

    Map<String, Long> getStats();

    Application reviewApplication(Long id, String status, String reviewedBy);
}
