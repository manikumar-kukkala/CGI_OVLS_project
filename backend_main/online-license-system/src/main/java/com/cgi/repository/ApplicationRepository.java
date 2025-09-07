package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Projection for a lightweight view
    interface LatestStatus {
        Long getApplicationId();

        String getApplicationNumber();

        String getStatus(); // PENDING | APPROVED | REJECTED

        String getPaymentStatus(); // Completed | Pending

        String getModeOfPayment(); // online | challan

        String getApplicationDate(); // keep your current type (String)
    }

    LatestStatus findTopByApplicant_User_IdOrderByApplicationIdDesc(Long userId);

    List<Application> findByStatus(String status);

    long countByStatus(String status);

    List<Application> findByApplicantUserEmailOrderByApplicationDateDesc(String email);

    Optional<Applicant> findByApplicationNumber(String number);

    Optional<Application> findFirstByApplicationNumberIgnoreCase(String number);

    // Optional<Applicant> findByApplicationNumber(String number);
}
