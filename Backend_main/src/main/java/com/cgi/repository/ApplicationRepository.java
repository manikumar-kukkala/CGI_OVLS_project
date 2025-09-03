package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cgi.model.Application;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStatus(String status);
}
