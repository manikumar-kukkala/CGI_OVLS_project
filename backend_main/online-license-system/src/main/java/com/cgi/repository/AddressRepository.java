package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cgi.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
