package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.ReportReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.ReportService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    @PostMapping("/addReport")
    public MainResponse addReport(ReportReq req, Authentication auth){
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    reportService.addReport(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    @GetMapping("/listLocationReports")
    public MainResponse listLocationReports(ReportReq req, Authentication auth){
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    reportService.listLocationReports(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
    @GetMapping("/listCustomerReports")
    public MainResponse listCustomerReports(ReportReq req, Authentication auth){
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    reportService.listCustomerReports(req,auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

}
