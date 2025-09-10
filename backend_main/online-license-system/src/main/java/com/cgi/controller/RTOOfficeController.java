package com.cgi.controller;

import com.cgi.model.RTOOffice;
import com.cgi.service.RTOOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/rto/offices")
public class RTOOfficeController {

    @Autowired
    private RTOOfficeService officeService;

    // Create Office
    @PostMapping
    public RTOOffice createOffice(@RequestBody RTOOffice office) {
        return officeService.createOffice(office);
    }

    // Get all offices
    @GetMapping
    public List<RTOOffice> getAllOffices() {
        return officeService.getAllOffices();
    }

    // Get office by ID
    @GetMapping("/{id}")
    public RTOOffice getOfficeById(@PathVariable int id) {
        return officeService.getOfficeById(id);
    }

    // Update office
    @PutMapping("/{id}")
    public RTOOffice updateOffice(@PathVariable int id, @RequestBody RTOOffice office) {
        return officeService.updateOffice(id, office);
    }

    // Delete office
    @DeleteMapping("/{id}")
    public String deleteOffice(@PathVariable int id) {
        return officeService.deleteOffice(id);
    }
}

