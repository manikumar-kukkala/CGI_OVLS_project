package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.Appointment;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
	Appointment findByApplication(Application application);

}
