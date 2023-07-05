package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Report;
import com.backend.SafeSt.Model.ReportModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ReportMapper {
    public ReportModel convertEntityToModel(Report report) {
        String[] date = report.getDate().toString().split(" ");
        return ReportModel.builder()
                .id(report.getId())
                .reportText(report.getReportText().replaceAll(" ","@"))
                .date(date[0])
                .time(date[1].replaceAll(":", "-"))
                .category(report.getCategory().toString())
                .score(report.getScore())
                .customerId(report.getCustomer().getId())
                .firstName(report.getCustomer().getFirstName())
                .lastName(report.getCustomer().getLastName())
                .locationId(report.getLocation().getId())
                .build();
    }
}
