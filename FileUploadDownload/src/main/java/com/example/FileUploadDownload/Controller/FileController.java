package com.example.FileUploadDownload.Controller;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;

//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

//import com.example.FileUploadDownload.UploadFileResponse;
//import com.example.FileUploadDownload.Model.FileModel;
import com.example.FileUploadDownload.service.FileStorageService;

@RestController
@RequestMapping("/api")
public class FileController {

	// private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	@Autowired
	private FileStorageService fileStorageService;
/*
	  @PostMapping("/uploadFile")
	  
	    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
	        FileModel fileModel = fileStorageService.storeFile(file);

	        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(fileModel.getId())
	                .toUriString();

	        UploadFileResponse uploadFileResponse = new UploadFileResponse();
	        uploadFileResponse.setFileName(fileModel.getFileName());
	        uploadFileResponse.setFileType(file.getContentType());
	        uploadFileResponse.setSize(file.getSize());
	        uploadFileResponse.setFileDownloadUri(fileDownloadUri);

	        logger.info("File uploaded successfully: {}", fileModel.getFileName());

	        return uploadFileResponse;
	    }

	    @GetMapping("/downloadFile/{fileId}")
	    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
	        try {
	            FileModel fileModel = fileStorageService.getFile(fileId);
	            if (fileModel == null) {
	                return ResponseEntity.notFound().build();
	            }

	            ByteArrayResource resource = new ByteArrayResource(fileModel.getData());

	            return ResponseEntity.ok()
	                    .contentType(MediaType.parseMediaType(fileModel.getFileType()))
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getFileName() + "\"")
	                    .body(resource);

	        } catch (Exception e) {
	            logger.error("Failed to download file", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	    */
		@PostMapping("/upload")
		public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) throws IOException {
			// Map to hold column names and their corresponding values
			Map<String, List<String>> excelData = new LinkedHashMap<>();

			try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
				for (Sheet sheet : workbook) {
					// Iterate over the rows in the sheet
					int rowIndex = 0;
					String[] columnNames = null;

					for (Row row : sheet) {
						if (rowIndex == 0) { // First row, read column names
							columnNames = new String[row.getLastCellNum()];
							for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
								Cell cell = row.getCell(colIndex);
								columnNames[colIndex] = cell.getStringCellValue();
							}
						} else { // Other rows, display column names and values
							int colIndex = 0;
							for (Cell cell : row) {
								CellType cellType = cell.getCellType();
								switch (cellType) {
								case STRING:
									String cellValue = cell.getStringCellValue();
									System.out.println(columnNames[colIndex] + ": " + cellValue);
									break;
								case NUMERIC:
									if (DateUtil.isCellDateFormatted(cell)) { // Check if cell is date-formatted
										 Date dateValue = cell.getDateCellValue();
									        
									        // Define the date format
									        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									        
									        // Print the formatted date
									        System.out.println(columnNames[colIndex] + ": " + sdf.format(dateValue));
									} else {
										Integer numericValue = (int) cell.getNumericCellValue();
										System.out.println(columnNames[colIndex] + ": " + numericValue);
									}
									break;
								case BOOLEAN:
									boolean booleanValue = cell.getBooleanCellValue();
									System.out.println(columnNames[colIndex] + ": " + booleanValue);
									break;

								case ERROR:
									byte errorValue = cell.getErrorCellValue();
									System.out.println(columnNames[colIndex] + ": " + errorValue);
									break;
								default:
									break;
								}
								colIndex++; // Increment column index
							}
						}
						rowIndex++; // Increment row index
						System.out.println("\n"); // Print a newline character
					}
				}
			}

			// For demonstration: Print the map content
			for (Map.Entry<String, List<String>> entry : excelData.entrySet()) {
				System.out.println("Column: " + entry.getKey());
				for (String value : entry.getValue()) {
					System.out.println("  Value: " + value);
				}
			}

			fileStorageService.storeFile(file);
			return ResponseEntity.ok("Excel file uploaded and processed successfully.");
		}
		
}

