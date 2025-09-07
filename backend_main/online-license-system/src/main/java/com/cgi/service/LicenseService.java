package com.cgi.service;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.Appointment;
import com.cgi.model.Documents;
import com.cgi.model.DrivingLicense;

public interface LicenseService {
    String applyForLL(Application application);
    String applyForDL(Application application);
    String uploadDocuments(Documents documents);
    String payChallanByVehicleNumber(String vehicleNumber);
    String payFees(String paymentMode, double amount);
    String bookSlot(Appointment appointment);
    String updateLL(Application application);
    String renewLL(Application application);
    String updateDL(Application application);
    String cancelAppointment(Appointment appointment);
    DrivingLicense generateDrivingLicense(Application application);
	char[] createApplicant(Applicant applicant);
}

