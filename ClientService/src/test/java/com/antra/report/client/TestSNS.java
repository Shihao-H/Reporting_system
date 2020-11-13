package com.antra.report.client;

import com.antra.report.client.entity.ReportRequestEntity;
import com.antra.report.client.pojo.request.ReportRequest;
import com.antra.report.client.repository.ReportRequestRepo;
import com.antra.report.client.service.ReportService;
import com.antra.report.client.service.SNSService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSNS {

    @Autowired
    SNSService snsService;

    @Autowired
    ReportService reportService;

    @Autowired
    ReportRequestRepo reportRequestRepo;


    @Test
    public void testReportService() {
        ReportRequest request = new ReportRequest();
        request.setSubmitter("Dawei_test");
        request.setDescription("This is just a test");
        request.setHeaders(List.of("Id","Name","Age"));
        request.setData(List.of(List.of("1","Dd","23"),List.of("2","AJ","32")));

        String s1 =         "ReportRequest{" +
                "headers=" + request.getHeaders() +
                ", description='" + request.getDescription() + '\'' +
                ", data=" + request.getData() +
                ", submitter='" + request.getSubmitter() + '\'' +
                '}';;
        String s2 = reportService.generateReports(request);

        assertEquals(s1,s2);


    }

    @Test
    public void testSNSSend() {
        ReportRequest request = new ReportRequest();
        request.setSubmitter("Shihao_test");
        request.setDescription("This is just a test");
        request.setHeaders(List.of("Id","Name","Age"));
        request.setData(List.of(List.of("1","Dd","23"),List.of("2","AJ","32")));

  //      snsService.generateReport(request);
        snsService.sendReportNotification(request);

        for(ReportRequestEntity entity: reportRequestRepo.findAll())
        {
            if(entity.getSubmitter().equals("Shihao_test"))
                assertTrue(true);
        }

    }
}
