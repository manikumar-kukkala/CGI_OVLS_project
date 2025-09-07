package com.cgi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cgi.model.Challan;

public interface ChallanRepository extends JpaRepository<Challan, Integer> {

    // Find challan by challan number
    Optional<Challan> findByChallanNumber(String challanNumber);

    // Find challan by vehicle number
    Optional<Challan> findByVehicleNumber(String vehicleNumber);
}
