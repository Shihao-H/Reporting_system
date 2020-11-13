package com.antra.evaluation.reporting_system.service;

import com.amazonaws.services.s3.AmazonS3;
import com.antra.evaluation.reporting_system.exception.FileGenerationException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    ExcelRepository excelRepository;

    private final AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String s3Bucket;

    private ExcelGenerationService excelGenerationService;

    @Autowired
    public ExcelServiceImpl(ExcelRepository excelRepository, AmazonS3 s3Client, ExcelGenerationService excelGenerationService) {
        this.excelRepository = excelRepository;
        this.s3Client = s3Client;
        this.excelGenerationService = excelGenerationService;
    }

    @Override
    public InputStream getExcelBodyById(String id) throws FileNotFoundException {
// ----------------------------------



//        for (Map.Entry<String, ExcelFile> entry : excelRepository.getMap().entrySet())
//            System.out.println("Key = " + entry.getKey() +
//                    ", Value = " + entry.getValue().toString());


// ----------------------------------

        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);

        return s3Client.getObject(s3Bucket, fileInfo.get().getFileId()).getObjectContent();

//        System.out.println("FileInfo: " + fileInfo.get().toString());

//        return new FileInputStream(fileInfo.orElseThrow(FileNotFoundException::new).getFileLocation());
    }

    @Override
    public ExcelFile generateFile(ExcelRequest request, boolean multisheet) {

//        PDFFile file = new PDFFile();
//        file.setId("File-" + UUID.randomUUID().toString());
//        file.setSubmitter(request.getSubmitter());
//        file.setDescription(request.getDescription());
//        file.setGeneratedTime(LocalDateTime.now());
//
//        PDFFile generatedFile= generator.generate(request);
//
//        File temp = new File(generatedFile.getFileLocation());
//        log.debug("Upload temp file to s3 {}", generatedFile.getFileLocation());
//        s3Client.putObject(s3Bucket,file.getId(),temp);
//        log.debug("Uploaded");
//
//        file.setFileLocation(String.join("/",s3Bucket,file.getId()));
//        file.setFileSize(generatedFile.getFileSize());
//        file.setFileName(generatedFile.getFileName());
//        repository.save(file);
//
//        log.debug("clear tem file {}", file.getFileLocation());
//        if(temp.delete()){
//            log.debug("cleared");
//        }
//
//        return file;






        ExcelFile fileInfo = new ExcelFile();
//        file.setId("File-" + UUID.randomUUID().toString());
        fileInfo.setFileId(UUID.randomUUID().toString());
        ExcelData data = new ExcelData();
        data.setTitle(request.getDescription());
        data.setFileId(fileInfo.getFileId());
        data.setSubmitter(fileInfo.getSubmitter());
        if(multisheet){
            data.setSheets(generateMultiSheet(request));
        }else {
            data.setSheets(generateSheet(request));
        }
        try {
            File generatedFile = excelGenerationService.generateExcelReport(data);

            File temp = new File(generatedFile.getAbsolutePath());
            log.debug("Upload temp file to s3 {}", generatedFile.getAbsolutePath());

            fileInfo.setFileLocation(String.join("/",s3Bucket,fileInfo.getFileId()));
            fileInfo.setFileName(generatedFile.getName());
            fileInfo.setGeneratedTime(LocalDateTime.now());
            fileInfo.setSubmitter(request.getSubmitter());
            fileInfo.setFileSize(generatedFile.length());
            fileInfo.setDescription(request.getDescription());

            s3Client.putObject(s3Bucket,fileInfo.getFileId(), temp);
            log.debug("Uploaded");
            generatedFile.delete();

        } catch (IOException e) {
            log.error("Error in generateFile()", e);
            throw new FileGenerationException(e);
        }
        excelRepository.saveFile(fileInfo);

        log.debug("Excel File Generated : {}", fileInfo);
        return fileInfo;
    }

    @Override
    public List<ExcelFile> getExcelList() {
        return excelRepository.getFiles();
    }

    @Override
    public ExcelFile deleteFile(String id) throws FileNotFoundException {
        ExcelFile excelFile = excelRepository.deleteFile(id);
        if (excelFile == null) {
            throw new FileNotFoundException();
        }
        File file = new File(excelFile.getFileLocation());
        file.delete();
        return excelFile;
    }

    private List<ExcelDataSheet> generateSheet(ExcelRequest request) {
        List<ExcelDataSheet> sheets = new ArrayList<>();
        ExcelDataSheet sheet = new ExcelDataSheet();
        sheet.setHeaders(request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList()));
        sheet.setDataRows(request.getData().stream().map(listOfString -> (List<Object>) new ArrayList<Object>(listOfString)).collect(Collectors.toList()));
        sheet.setTitle("sheet-1");
        sheets.add(sheet);
        return sheets;
    }
    private List<ExcelDataSheet> generateMultiSheet(ExcelRequest request) {
        List<ExcelDataSheet> sheets = new ArrayList<>();
        int index = request.getHeaders().indexOf(((MultiSheetExcelRequest) request).getSplitBy());
        Map<String, List<List<String>>> splittedData = request.getData().stream().collect(Collectors.groupingBy(row -> (String)row.get(index)));
        List<ExcelDataHeader> headers = request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList());
        splittedData.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(
                entry ->{
                    ExcelDataSheet sheet = new ExcelDataSheet();
                    sheet.setHeaders(headers);
                    sheet.setDataRows(entry.getValue().stream().map(listOfString -> {
                        List<Object> listOfObject = new ArrayList<>();
                        listOfString.forEach(listOfObject::add);
                        return listOfObject;
                    }).collect(Collectors.toList()));
                    sheet.setTitle(entry.getKey());
                    sheets.add(sheet);
                }
        );
        return sheets;
    }
}
