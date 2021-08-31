package net.lunalabs.hl7gw.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.config.CustomFtpClient;
import net.lunalabs.hl7gw.config.FTPUploader;

@RequiredArgsConstructor
@EnableAsync
@Service
public class FTPService {

	private static final Logger logger = LoggerFactory.getLogger(FTPService.class);

	private final CustomFtpClient customFtpClient;

	
	@Async
	public void FTPTest() throws Exception {
		 FTPUploader ftpUploader = new FTPUploader("kist.lunalabs.net", "luna",
		 "new12#$!");

		//ftpUploader.getFtpBean();

		logger.debug("FTP TEST START");
		ftpUploader.uploadFile("C:\\kangminkyu\\FTPTEST.txt", "FTPTEST.txt", "/");
		ftpUploader.disconnect();
		logger.debug("FTP TEST DONE");

	}

	@Async
	public void ftpSendToCs(String filename) throws Exception { // Central Statino sever로 파일 전송
		logger.debug("file send to Cs");

		customFtpClient.uploadFile(filename, "CsFTPSendText.txt", "C:\\kangminkyu\\aaaaaaa");
		customFtpClient.customDisconnect();
		logger.debug("FTP SEND DONE");
	}
	
	
	@Async
	public void ftpSendToCs2(String file) throws Exception { // Central Statino sever로 파일 전송
		 FTPUploader ftpUploader = new FTPUploader("localhost", "kyu",
		 "1234");

		//ftpUploader.getFtpBean();"C:\\kangminkyu\\CsFTPSendText.txt"
		 		
		logger.debug("fileName: " + file);
		
		//String[] fileNameArray = file.split(File.separator); //리눅스에서는 다르게 되나?
		String[] fileNameArray = file.split("\\\\"); 
		String fileName = fileNameArray[fileNameArray.length - 1];
		logger.debug("file: " + fileName);
		
		logger.debug("FTP TEST START");
		//ftpUploader.uploadFile(filename, "CsFTPSendText.txt", "C:" + File.separator + "kangminkyu" +  File.separator  + "aaaaaaa" + File.separator);
		ftpUploader.uploadFile(file, fileName, "/");
		ftpUploader.disconnect();
		logger.debug("FTP TEST DONE");

	}

}
