
Modifications:
1. ExcelServiceImpl class : "generateFile" method,  make the excel file store in aws S3 Bucket and delete the local generated excel file.

2. ReportServiceImpl class: "getFileBodyByReqId" method, make it able to get file from S3 instead of local.

3. First "Todo" : ReportServiceImpl class, "sendDirectRequests" method, using thread pool to do parallel processing.

4. Second "Todo": ExcelGenerationController class, "downloadExcel" method, ExcelServiceImpl class method getExcelBodyById() to get file from S3, then ExcelGenerationController class downloadExcel() have a strongBuilder to dynamically build the file name.

5. Play around with the test in TestSNS class. Test the snsService class, send() method.

6. Configuration files, set my own S3 bucket, sqs, sns and secretKey/accessKey to make the project work.
