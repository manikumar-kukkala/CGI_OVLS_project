package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Projection for a lightweight view
    interface LatestStatus {
        Long getApplicationId();

        String getApplicationNumber();

        String getApplicantName();

        String getStatus();

        String getPaymentStatus();

        String getModeOfPayment();

        String getApplicationDate();
    }

    @Query("""
               SELECT a.applicationId AS applicationId,
                      a.applicationNumber AS applicationNumber,
                      a.applicant.user.name AS applicantName,
                      a.status AS status,
                      a.paymentStatus AS paymentStatus,
                      a.modeOfPayment AS modeOfPayment,
                      a.applicationDate AS applicationDate
               FROM Application a
            """)
    LatestStatus findLatestByUserId(@Param("userId") Long userId);

    List<Application> findByStatus(String status);

    long countByStatus(String status);

    List<Application> findByApplicantUserEmailOrderByApplicationDateDesc(String email);

    Optional<Applicant> findByApplicationNumber(String number);

    Optional<Application> findFirstByApplicationNumberIgnoreCase(String number);

    // ✅ Fixed methods using @Query
    @Query("SELECT a FROM Application a WHERE a.applicant.user.name = :name")
    List<Application> findByApplicantName(@Param("name") String applicantName);

    @Query("SELECT a FROM Application a WHERE LOWER(a.applicant.user.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Application> findByApplicantNameContainingIgnoreCase(@Param("name") String applicantName);

    @Query("""
               SELECT a.applicationId AS applicationId,
                      a.applicationNumber AS applicationNumber,
                      a.applicant.user.name AS applicantName,
                      a.status AS status,
                      a.paymentStatus AS paymentStatus,
                      a.modeOfPayment AS modeOfPayment,
                      a.applicationDate AS applicationDate
               FROM Application a
            """)
    List<LatestStatus> findAllWithApplicantName();
}
