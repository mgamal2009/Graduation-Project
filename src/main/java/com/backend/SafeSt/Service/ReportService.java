package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Customer;
import com.backend.SafeSt.Entity.Location;
import com.backend.SafeSt.Entity.Report;
import com.backend.SafeSt.Mapper.ReportMapper;
import com.backend.SafeSt.Model.ReportModel;
import com.backend.SafeSt.Repository.LocationRepository;
import com.backend.SafeSt.Repository.ReportRepository;
import com.backend.SafeSt.Request.ReportReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.jdbc.Expectation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final LocationRepository locationRepository;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Transactional
    public ReportModel addReport(ReportReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        if (!(Validation.validateDouble(req.getLatitude(), req.getLatitude()))) {
            throw new Exception("Location couldn't be empty");
        }
        Optional<Location> l = locationRepository.findByLongitudeAndLatitude(req.getLongitude(), req.getLatitude());
        if (l.isEmpty()) {
            var location = Location.builder()
                    .longitude(req.getLongitude())
                    .latitude(req.getLatitude())
                    .build();
            location = locationRepository.save(location);
            var report = Report.builder()
                    .location(location)
                    .customer(c)
                    .date(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            report = reportRepository.save(report);
            return reportMapper.convertEntityToModel(report);
        } else {
            var location = l.get();
            var report = Report.builder()
                    .location(location)
                    .customer(c)
                    .date(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            report = reportRepository.save(report);
            return reportMapper.convertEntityToModel(report);
        }
    }

    public List<ReportModel> listLocationReports(ReportReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        var l = locationRepository.findByLongitudeAndLatitude(req.getLongitude(), req.getLatitude())
                .orElseThrow(() -> new Exception("No Reports Found"));
        List<Report> foundReports = reportRepository.findAllByLocation_Id(l.getId());
        if (foundReports.isEmpty()){
            throw new Exception("No Reports Found");
        }
        List<ReportModel> models = new ArrayList<>();
        for (Report report:foundReports) {
            models.add(reportMapper.convertEntityToModel(report));
        }
        return models;
    }
    public List<ReportModel> listCustomerReports(ReportReq req, Authentication auth) throws Exception {
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
    }
}
