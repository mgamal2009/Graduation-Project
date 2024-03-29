package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.*;
import com.backend.SafeSt.Enum.EmergencyCat;
import com.backend.SafeSt.Enum.ReportCat;
import com.backend.SafeSt.Mapper.EmergencyInfoMapper;
import com.backend.SafeSt.Model.EmergencyInfoModel;
import com.backend.SafeSt.Repository.EmergencyInfoRepository;
import com.backend.SafeSt.Repository.LocationRepository;
import com.backend.SafeSt.Repository.ReportRepository;
import com.backend.SafeSt.Repository.TrustedContactRepository;
import com.backend.SafeSt.Request.EmergencyInfoReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EmergencyService {

    private final EmergencyInfoRepository emergencyInfoRepository;
    private final LocationRepository locationRepository;
    private final ReportRepository reportRepository;
    private final TrustedContactRepository trustedContactRepository;
    private final EmailService emailService;

    private final EmergencyInfoMapper emergencyInfoMapper;

    @Transactional
    public EmergencyInfoModel createEmergency(EmergencyInfoReq req, Authentication auth) throws Exception {
        Customer c = CustomerService.checkLoggedIn(req.getCustomerId(), auth);
        if (!(Validation.validateString(req.getAddress(), req.getCategory(), req.getLongitude(), req.getLatitude()))) {
            throw new Exception("Fields couldn't be empty");
        }
        String long3 = BigDecimal.valueOf(Double.parseDouble(req.getLongitude()))
                .setScale(3, RoundingMode.FLOOR)
                .toString();
        String lat3 = BigDecimal.valueOf(Double.parseDouble(req.getLatitude()))
                .setScale(3, RoundingMode.FLOOR)
                .toString();
        Optional<Location> l = locationRepository.findByLongitudeAndLatitude(long3, lat3);
        Location location = l.orElseGet(() -> Location.builder()
                .latitude(lat3)
                .longitude(long3)
                .averageScore(0.0)
                .reportsCount(0)
                .build());
        if (l.isEmpty())
            location = locationRepository.save(location);
        var emergency = EmergencyInfo.builder()
                .date(Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("Africa/Cairo")).toLocalDateTime()))
                .category(EmergencyCat.valueOf(req.getCategory()))
                .customer(c)
                .build();

        if (!(req.getCategory().equals("CarFault") || req.getCategory().equals("Fire") || req.getCategory().equals("UserDidntArrive") || req.getCategory().equals("InDanger"))) {
            var report = Report.builder()
                    .date(Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("Africa/Cairo")).toLocalDateTime()))
                    .category(ReportCat.valueOf(req.getCategory()))
                    .reportText("I faced in this location a " + req.getCategory() + " Situation")
                    .customer(c)
                    .build();
            switch (report.getCategory()) {
                case Harassment -> report.setScore(2.0);
                case Kidnapping -> report.setScore(3.0);
                case Murder -> report.setScore(4.0);
                case Robbery -> report.setScore(1.0);
            }
            report = reportRepository.save(report);
            location.setAverageScore((location.getAverageScore() * location.getReportsCount() + report.getScore()) / (location.getReportsCount() + 1));
            location.setReportsCount(location.getReportsCount() + 1);
            report.setLocation(location);
            location = locationRepository.save(location);
            report = reportRepository.save(report);
            emergency.setLocation(location);
            emergency.setReport(report);
        } else {
            emergency.setLocation(location);
        }
        emergencyInfoRepository.save(emergency);
        ArrayList<TrustedContact> list = trustedContactRepository.findAllByCustomer_Id(c.getId());
        for (TrustedContact t : list) {
            Customer x = t.getTrusted();
            emailService.sendEmergency(
                    x.getFirstName(),
                    x.getEmail(),
                    String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()),
                    t.getCustomer().getFirstName().concat(" ").concat(t.getCustomer().getLastName()),
                    emergency.getCategory().toString(),
                    emergency.getDate().toString(),
                    req.getAddress());
        }
        return emergencyInfoMapper.convertEntityToModel(emergency);
    }
}
