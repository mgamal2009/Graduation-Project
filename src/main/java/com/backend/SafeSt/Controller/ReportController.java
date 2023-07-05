package com.backend.SafeSt.Controller;

import com.backend.SafeSt.Request.ReportReq;
import com.backend.SafeSt.Response.MainResponse;
import com.backend.SafeSt.Service.ReportService;
import com.backend.SafeSt.Util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/addReport")
    public MainResponse addReport(@RequestBody ReportReq req, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.CREATED,
                    reportService.addReport(req, auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @GetMapping("/listLocationReports")
    public MainResponse listLocationReports(@RequestParam Integer id, @RequestParam String longitude, @RequestParam String latitude, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    reportService.listLocationReports(id, longitude, latitude, auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @GetMapping("/listAllLocationWithScore")
    public MainResponse listAllLocationWithScore(@RequestParam Integer id, Authentication auth) {
        try {
            return new MainResponse(HttpStatus.OK,
                    ResponseMessage.EXECUTED,
                    reportService.listAllLocationWithScore(id, auth));
        } catch (Exception exception) {
            return new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

}
