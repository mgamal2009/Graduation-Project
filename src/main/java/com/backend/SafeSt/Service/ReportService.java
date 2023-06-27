package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Location;
import com.backend.SafeSt.Entity.Report;
import com.backend.SafeSt.Enum.ReportCat;
import com.backend.SafeSt.Mapper.LocationMapper;
import com.backend.SafeSt.Mapper.ReportMapper;
import com.backend.SafeSt.Model.LocationModel;
import com.backend.SafeSt.Model.ReportModel;
import com.backend.SafeSt.Repository.LocationRepository;
import com.backend.SafeSt.Repository.ReportRepository;
import com.backend.SafeSt.Request.ReportReq;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final LocationRepository locationRepository;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final LocationMapper locationMapper;

    @Transactional
    public ReportModel addReport(ReportReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        double long3 = (Math.floor(req.getLongitude() * 1000) / 1000.0);
        double lat3 = (Math.floor(req.getLatitude() * 1000) / 1000.0);
        Optional<Location> l = locationRepository.findByLongitudeAndLatitude(long3, lat3);
        Location location = l.orElseGet(() -> Location.builder()
                .latitude(lat3)
                .longitude(long3)
                .averageScore(0.0)
                .reportsCount(0)
                .build());
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
        var report = Report.builder()
                .date(Timestamp.valueOf(LocalDateTime.now()))
                .category(ReportCat.valueOf(req.getCategory()))
                .reportText(req.getReportText())
                .customer(c)
                .build();
        switch (report.getCategory()) {
            case Harassment -> report.setScore(3.0);
            case Accident -> report.setScore(1.0);
            case Murder -> report.setScore(4.0);
            case Robbery -> report.setScore(2.0);
        }
        location = locationRepository.save(location);
        report = reportRepository.save(report);
        location.setAverageScore((location.getAverageScore() * location.getReportsCount() + report.getScore()) / (location.getReportsCount() + 1));
        location.setReportsCount(location.getReportsCount() + 1);
        report.setLocation(location);
        locationRepository.save(location);
        report = reportRepository.save(report);
        return reportMapper.convertEntityToModel(report);
    }

    public List<ReportModel> listLocationReports(int id,double longitude, double latitude, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(id, auth);
        var l = locationRepository.findByLongitudeAndLatitude(longitude, latitude)
                .orElseThrow(() -> new Exception("No Reports Found"));
        List<Report> foundReports = reportRepository.findAllByLocation_Id(l.getId());
        if (foundReports.isEmpty()) {
            throw new Exception("No Reports Found");
        }
        List<ReportModel> models = new ArrayList<>();
        for (Report report : foundReports) {
            models.add(reportMapper.convertEntityToModel(report));
        }
        return models;
    }

    public List<LocationModel> listAllLocationWithScore(int id, Authentication auth) throws Exception {
        CustomerService.checkLoggedIn(id, auth);
        List<Location> list = locationRepository.findAll();
        if (list.isEmpty()){
            throw new Exception("No Reports Found");
        }
        List<LocationModel> models = new ArrayList<>();
        for (Location location : list) {
            models.add(locationMapper.convertEntityToModel(location));
        }
        return models;
    }
    /*public List<ReportModel> listCustomerReports(ReportReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        List<Report> foundReports = reportRepository.findAllByCustomer_Id(c.getId());
        if (foundReports.isEmpty()){
            throw new Exception("No Reports Found");
        }
        List<ReportModel> models = new ArrayList<>();
        for (Report report:foundReports) {
            models.add(reportMapper.convertEntityToModel(report));
        }
        return models;
    }*/
}
