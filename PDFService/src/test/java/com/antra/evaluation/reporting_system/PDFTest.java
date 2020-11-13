package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.pojo.exception.PDFGenerationException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFTest {

    @Test
    public void findFile() throws FileNotFoundException {
        File f = new File("Coffee_Landscape.jasper");
        System.out.println(f.exists());

        File file = ResourceUtils.getFile("classpath:Coffee_Landscape.jasper");
        System.out.println(file.exists());
    }

    @Test
    public void pdf() {
//        String jasperFileName = "CL.jasper";
//        Map<String, Object> parameters = new HashMap<>();
//
//        parameters.put("content_str", "123");
//
//        List<Object> itemList = List.of("string");
//        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itemList);
//
//        try {
//            JasperPrint jprint = JasperFillManager.fillReport(jasperFileName, parameters,dataSource);
//            File temp = new File("report.pdf");
//            JasperExportManager.exportReportToPdfFile(jprint, temp.getAbsolutePath());
//
//        } catch (JRException e) {
//            throw new PDFGenerationException();
//        }
    }
}
