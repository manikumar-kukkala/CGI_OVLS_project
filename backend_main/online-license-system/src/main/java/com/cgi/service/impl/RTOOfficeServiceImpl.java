package com.cgi.service.impl;

import com.cgi.model.RTOOffice;
import com.cgi.repository.RTOOfficeRepository;
import com.cgi.service.RTOOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RTOOfficeServiceImpl implements RTOOfficeService {

    @Autowired
    private RTOOfficeRepository officeRepo;

    @Override
    public RTOOffice createOffice(RTOOffice office) {
        return officeRepo.save(office);
    }

    @Override
    public List<RTOOffice> getAllOffices() {
        return officeRepo.findAll();
    }

    @Override
    public RTOOffice getOfficeById(int id) {
        Optional<RTOOffice> office = officeRepo.findById(id);
        return office.orElse(null);
    }

    @Override
    public RTOOffice updateOffice(int id, RTOOffice updatedOffice) {
        Optional<RTOOffice> existing = officeRepo.findById(id);
        if (existing.isPresent()) {
            RTOOffice office = existing.get();
            office.setRtoName(updatedOffice.getRtoName());
            return officeRepo.save(office);
        }
        return null;
    }

    @Override
    public String deleteOffice(int id) {
        if (officeRepo.existsById(id)) {
            officeRepo.deleteById(id);
            return "RTO Office deleted successfully!";
        }
        return "RTO Office not found!";
    }
}
