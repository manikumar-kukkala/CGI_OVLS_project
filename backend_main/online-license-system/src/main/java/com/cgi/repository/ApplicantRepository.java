package com.cgi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.Appointment;
import com.cgi.model.User;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
	Appointment findByApplication(Application application);

	List<Applicant> findAllByUser_EmailIgnoreCase(String email);

	Optional<User> findByApplication_ApplicationId(Long applicationId);

}
