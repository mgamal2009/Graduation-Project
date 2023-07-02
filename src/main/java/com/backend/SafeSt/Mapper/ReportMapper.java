package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Report;
import com.backend.SafeSt.Model.ReportModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ReportMapper {
    public ReportModel convertEntityToModel(Report report) {
        String [] date = report.getDate().toString().split(" ");
        return ReportModel.builder()
                .id(report.getId())
                .reportText(report.getReportText())
                .date(date[0])
                .time(date[1])
                .category(report.getCategory().toString())
                .score(report.getScore())
                .customerId(report.getCustomer().getId())
                .locationId(report.getLocation().getId())
                .build();
    }
}
