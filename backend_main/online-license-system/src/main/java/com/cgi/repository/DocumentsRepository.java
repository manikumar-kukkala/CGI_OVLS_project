package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cgi.model.Documents;

public interface DocumentsRepository extends JpaRepository<Documents, Long> {
}
