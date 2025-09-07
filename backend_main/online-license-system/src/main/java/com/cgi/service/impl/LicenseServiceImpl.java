package com.cgi.service.impl;

import com.cgi.exception.InvalidDataException;
import com.cgi.model.*;
import com.cgi.repository.*;
import com.cgi.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LicenseServiceImpl implements LicenseService {

    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final DocumentsRepository documentsRepository;
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final AppointmentRepository appointmentRepository;
    private final ChallanRepository challanRepository;

    @Autowired
    public LicenseServiceImpl(ApplicationRepository applicationRepository,
                              ApplicantRepository applicantRepository,
                              DocumentsRepository documentsRepository,
                              DrivingLicenseRepository drivingLicenseRepository,
                              AppointmentRepository appointmentRepository,
                              ChallanRepository challanRepository) {
        this.applicationRepository = applicationRepository;
        this.applicantRepository = applicantRepository;
        this.documentsRepository = documentsRepository;
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.appointmentRepository = appointmentRepository;
        this.challanRepository = challanRepository;
    }

    @Override
    public String applyForLL(Application application) {
        applicationRepository.save(application);
        return "LL application submitted: " + application.getApplicationNumber();
    }

    @Override
    public String applyForDL(Application application) {
        applicationRepository.save(application);
        return "DL application submitted: " + application.getApplicationNumber();
    }

    @Override
    public String uploadDocuments(Documents documents) {
        documentsRepository.save(documents);
        return "Documents uploaded successfully.";
    }

    @Override
    public String payChallanByVehicleNumber(String vehicleNumber) {
        return challanRepository.findByVehicleNumber(vehicleNumber)
                .map(challan -> "Challan paid for vehicle: " + vehicleNumber +
                        " | Amount: " + challan.getAmount())
                .orElse("No challan found for vehicle: " + vehicleNumber);
    }

    @Override
    public String payFees(String paymentMode, double amount) {
        // Later: persist payment in PaymentRepository
        return "Fees paid via " + paymentMode + " | Amount: " + amount;
    }

    @Override
    public String bookSlot(Appointment appointment) {
        appointmentRepository.save(appointment);
        return "Appointment booked: " + appointment.getTestDate() +
                " | Slot: " + appointment.getTimeSlot();
    }

    @Override
    public String updateLL(Application application) {
        applicationRepository.save(application);
        return "LL application updated: " + application.getApplicationNumber();
    }

    @Override
    public String renewLL(Application application) {
        applicationRepository.save(application);
        return "LL renewed for application: " + application.getApplicationNumber();
    }

    @Override
    public String updateDL(Application application) {
        applicationRepository.save(application);
        return "DL application updated: " + application.getApplicationNumber();
    }

    @Override
    public String cancelAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
        return "Appointment cancelled: " + appointment.getTestNumber();
    }

    @Override
    public DrivingLicense generateDrivingLicense(Application application) {
        if (application == null) {
            throw new InvalidDataException("Application cannot be null.");
        }
        DrivingLicense dl = new DrivingLicense();
        dl.setLicenseNumber("DL-" + System.currentTimeMillis());
        dl.setApplicant(application.getApplicant());
        dl.setDateOfIssue(new java.util.Date());
        dl.setValidTill(new java.util.Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000))); // +1 year validity

        drivingLicenseRepository.save(dl);
        return dl;
    }

	@Override
	public char[] createApplicant(Applicant applicant) {
		// TODO Auto-generated method stub
		return null;
	}
}
