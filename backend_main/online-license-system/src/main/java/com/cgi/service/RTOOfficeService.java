package com.cgi.service;

import com.cgi.model.RTOOffice;
import java.util.List;

public interface RTOOfficeService {
    RTOOffice createOffice(RTOOffice office);
    List<RTOOffice> getAllOffices();
    RTOOffice getOfficeById(int id);
    RTOOffice updateOffice(int id, RTOOffice office);
    String deleteOffice(int id);
}



